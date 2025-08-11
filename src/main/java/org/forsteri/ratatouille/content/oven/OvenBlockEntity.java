package org.forsteri.ratatouille.content.oven;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.forsteri.ratatouille.entry.CRBlockEntityTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OvenBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IMultiBlockEntityContainer {
    public LazyOptional<CombinedInvWrapper> itemCapability = LazyOptional.empty();
    private boolean updateConnectivity = false;
    protected BlockPos controller;
    protected BlockPos lastKnownPos;
    protected BakeData bakeData;
    protected Inventory inventory = new Inventory();

    public class Inventory extends ItemStackHandler {
        public int tickTillFinishCooking = -1;
        public SmokingRecipe lastRecipe = null;
        private final RecipeWrapper RECIPE_WRAPPER = new RecipeWrapper(new ItemStackHandler(1));
        @Override
        protected void onContentsChanged(int slot) {
            assert level != null;
            if (!level.isClientSide) {
                setChanged();
                sendData();
                notifyUpdate();
            }
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            assert level != null;

            ItemStack returnValue = super.insertItem(slot, stack, simulate);

            if (!simulate && returnValue.getCount() != stack.getCount()) {
                level.getRecipeManager().getRecipeFor(RecipeType.SMOKING, RECIPE_WRAPPER, level).ifPresent(recipe -> {
                    tickTillFinishCooking = recipe.getCookingTime() * ((getStackInSlot(slot).getCount() - 1) / 16 + 1);
                    lastRecipe = recipe;
                });
            }

            return returnValue;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!simulate) {
                tickTillFinishCooking = -1;
            }
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 16;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            assert level != null;
            RECIPE_WRAPPER.setItem(0, stack);
            return level.getRecipeManager()
                    .getRecipeFor(RecipeType.SMOKING, RECIPE_WRAPPER, level).isPresent();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            assert level != null;
            super.deserializeNBT(nbt);
            if (nbt.contains("tickTillFinishCooking"))
                tickTillFinishCooking = nbt.getInt("tickTillFinishCooking");
            if (nbt.contains("lastRecipe"))
                lastRecipe = (SmokingRecipe) level.getRecipeManager().byKey(new ResourceLocation(nbt.getString("lastRecipe"))).orElse(null);


        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = super.serializeNBT();
            tag.putInt("tickTillFinishCooking", tickTillFinishCooking);
            if (lastRecipe != null)
                tag.putString("lastRecipe", lastRecipe.getId().toString());
            return tag;
        }
    }

    public OvenBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        bakeData = new BakeData();
    }

    @Override
    public void tick() {
        super.tick();
        if (isController())
            bakeData.tick(this);

        if (lastKnownPos == null)
            lastKnownPos = getBlockPos();
        else if (!lastKnownPos.equals(worldPosition)) {
            removeController(true);
            lastKnownPos = worldPosition;
            return;
        }

        if (updateConnectivity)
            updateConnectivity();
    }

    private void refreshCapability() {
        itemCapability.invalidate();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    @Override
    public BlockPos getController() {
        return isController() ? worldPosition : controller;
    }

    @SuppressWarnings("unchecked")
    @Override
    public OvenBlockEntity getControllerBE() {
        assert level != null;

        if (isController())
            return this;
        BlockEntity blockEntity = level.getBlockEntity(controller);
        if (blockEntity instanceof OvenBlockEntity ovenBlockEntity)
            return ovenBlockEntity;
        return null;
    }

    @Override
    public boolean isController() {
        return controller == null || worldPosition.equals(controller);
    }

    @Override
    public void setController(BlockPos pos) {
        assert level != null;

        if (level.isClientSide && !isVirtual())
            return;
        if (pos.equals(this.controller))
            return;
        this.controller = pos;
        refreshCapability();
        setChanged();
        sendData();
    }

    @Override
    public void removeController(boolean keepContents) {
        assert level != null;
        if (level.isClientSide())
            return;
        updateConnectivity = true;
        controller = null;
        radius = 1;
        height = 1;

        itemCapability.invalidate();
        bakeData.clear();
        setChanged();
        sendData();
    }

    @Override
    public BlockPos getLastKnownPos() {
        return lastKnownPos;
    }

    @Override
    public void preventConnectivityUpdate() {
        updateConnectivity = false;
    }

    public void updateConnectivity() {
        assert level != null;

        updateConnectivity = false;
        if (level.isClientSide)
            return;
        if (!isController())
            return;
        ConnectivityHandler.formMulti(this);
    }

    @Override
    public void notifyMultiUpdated() {
        assert level != null;

        level.setBlock(getBlockPos(), getBlockState().setValue(OvenBlock.IS_2x2, getWidth() == 2), 6);

        itemCapability.invalidate();
        updateOvenState();
        setChanged();
    }

    @Override
    public Direction.Axis getMainConnectionAxis() {
        return Direction.Axis.Y;
    }

    @Override
    public int getMaxLength(Direction.Axis longAxis, int width) {
        if (longAxis == Direction.Axis.Y)
            return 7;
        return getMaxWidth();
    }

    @Override
    public int getMaxWidth() {
        return 3;
    }

    protected int height = 1;

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    protected int radius = 1;

    @Override
    public int getWidth() {
        return radius;
    }

    @Override
    public void setWidth(int width) {
        radius = width;
    }

    public int getTotalOvenSize() {
        return radius * radius * height;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        OvenBlockEntity controllerBE = getControllerBE();
        if (controllerBE == null)
            return false;

        return controllerBE.bakeData.addToGoggleTooltip(tooltip, isPlayerSneaking, controllerBE.getTotalOvenSize());
    }

    @Override
    public void initialize() {
        super.initialize();
        notifyUpdate();
        if (level.isClientSide)
            invalidateRenderBoundingBox();
    }

    public void updateOvenState() {
        if (!isController() && getControllerBE() != null) {
            getControllerBE().updateOvenState();
            return;
        }

        if (bakeData.evaluate(this)) {
            notifyUpdate();
        }
    }

    public void updateBakeData() {
        OvenBlockEntity be = getControllerBE();
        if (be == null)
            return;
        be.bakeData.updateRequired = 2;
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        assert level != null;
        super.read(compound, clientPacket);

        BlockPos controllerBefore = controller;
        int prevSize = radius;
        int prevHeight = height;

        updateConnectivity = compound.contains("Uninitialized");
        controller = null;
        lastKnownPos = null;

        if (compound.contains("LastKnownPos"))
            lastKnownPos = NbtUtils.readBlockPos(compound.getCompound("LastKnownPos"));
        if (compound.contains("Controller"))
            controller = NbtUtils.readBlockPos(compound.getCompound("Controller"));

        if (isController()) {
            radius = compound.getInt("Size");
            height = compound.getInt("Height");
        }

        inventory.deserializeNBT(compound.getCompound("Inventory"));

        bakeData.read(compound, clientPacket);

        if (!clientPacket) {
            return;
        }

        boolean changeOfController =
                !Objects.equals(controllerBefore, controller);
        if (hasLevel() && (changeOfController || prevSize != radius || prevHeight != height)) {
            level.setBlocksDirty(getBlockPos(), Blocks.AIR.defaultBlockState(), getBlockState());
            if (hasLevel())
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 16);
            invalidateRenderBoundingBox();
        }
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        if (updateConnectivity)
            compound.putBoolean("Uninitialized", true);
        if (lastKnownPos != null)
            compound.put("LastKnownPos", NbtUtils.writeBlockPos(lastKnownPos));
        if (!isController())
            compound.put("Controller", NbtUtils.writeBlockPos(controller));
        if (isController()) {
            compound.putInt("Size", radius);
            compound.putInt("Height", height);
        }

        super.write(compound, clientPacket);

        compound.putString("StorageType", "CombinedInv");
        compound.put("Inventory", inventory.serializeNBT());
        bakeData.write(compound, clientPacket);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (isItemHandlerCap(cap)) {
            initCapability();
            return itemCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    public List<List<List<Inventory>>> inventories = null;

    private void initCapability() {
        assert level != null;

        if (itemCapability.isPresent())
            return;
        if (!isController()) {
            OvenBlockEntity controllerBE = getControllerBE();
            if (controllerBE == null)
                return;
            controllerBE.initCapability();
            itemCapability = controllerBE.itemCapability;
            return;
        }

        IItemHandlerModifiable[] invs = new IItemHandlerModifiable[height * radius * radius];
        inventories = new ArrayList<>();
        for (int xOffset = 0; xOffset < radius; xOffset++) {
            List<List<Inventory>> x = new ArrayList<>();
            inventories.add(x);
            for (int yOffset = 0; yOffset < height; yOffset++) {
                List<Inventory> y = new ArrayList<>();
                x.add(y);
                for (int zOffset = 0; zOffset < radius; zOffset++) {
                    BlockPos vaultPos = worldPosition.offset(xOffset, yOffset, zOffset);
                    OvenBlockEntity vaultAt =
                            ConnectivityHandler.partAt(CRBlockEntityTypes.OVEN_ENTITY.get(), level, vaultPos);
                    Inventory inv = vaultAt != null ? vaultAt.inventory : new Inventory();
                    invs[yOffset * radius * radius + xOffset * radius + zOffset] = inv;
                    y.add(inv);
                }
            }
        }

        CombinedInvWrapper itemHandler = new CombinedInvWrapper(invs);
        itemCapability = LazyOptional.of(() -> itemHandler);
    }
}
