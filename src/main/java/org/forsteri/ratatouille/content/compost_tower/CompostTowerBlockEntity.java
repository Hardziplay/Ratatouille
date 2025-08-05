package org.forsteri.ratatouille.content.compost_tower;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.forsteri.ratatouille.content.thresher.ThresherBlockEntity;
import org.forsteri.ratatouille.entry.CRBlockEntityTypes;
import org.forsteri.ratatouille.entry.CRFluids;
import org.forsteri.ratatouille.entry.CRItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class CompostTowerBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IMultiBlockEntityContainer.Inventory {
    protected LazyOptional<CompostTowerInventoryHandler> itemCapability = LazyOptional.empty();
    protected LazyOptional<CombinedTankWrapper> fluidCapability = LazyOptional.empty();
    private boolean updateConnectivity = true;
    protected BlockPos controller;
    protected BlockPos lastKnownPos;
    protected CompostData compostData;
    public ItemStackHandler inputInv = new ItemStackHandler();
    public ItemStackHandler outputInv = new ItemStackHandler();
    public FluidTank tankInventory = new SmartFluidTank(1000, $->{});

    protected class CompostTowerInventoryHandler extends CombinedInvWrapper {
        public CompostTowerInventoryHandler(IItemHandlerModifiable[] itemHandlers) {
            super(itemHandlers);
        }

        public int hasInput(Item item) {
            for (int i = 0; i < radius * radius; i++) {
                ItemStack stack = getStackInSlot(i);
                if (stack.isEmpty() || !stack.is(item))
                    continue;
                return i;
            }
            // item not found
            return -1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (this.getIndexForSlot(slot) >= radius * radius)
                return false;
            return stack.is(CRItems.COMPOST_MASS.get()) && super.isItemValid(slot, stack);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (this.getIndexForSlot(slot) >= radius * radius)
                return stack;
            if (!this.isItemValid(slot, stack))
                return stack;
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (getIndexForSlot(slot) < radius * radius)
                return ItemStack.EMPTY;
            return super.extractItem(slot, amount, simulate);
        }
    }

    public CompostTowerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        compostData = new CompostData();
    }

    @Override
    public void tick() {
        super.tick();
        if (isController())
            compostData.tick(this);

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
        fluidCapability.invalidate();
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
    public CompostTowerBlockEntity getControllerBE() {
        assert level != null;

        if (isController())
            return this;
        BlockEntity blockEntity = level.getBlockEntity(controller);
        if (blockEntity instanceof CompostTowerBlockEntity CompostTowerBlockEntity)
            return CompostTowerBlockEntity;
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

        refreshCapability();
        compostData.clear();
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

        level.setBlock(getBlockPos(), getBlockState().setValue(CompostTowerBlock.IS_2x2, getWidth() == 2), 6);

        refreshCapability();
        updateCompostTowerState();
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

    public int getTotalCompostTowerSize() {
        return radius * radius * height;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CompostTowerBlockEntity controllerBE = getControllerBE();
        if (controllerBE == null)
            return false;

        return controllerBE.compostData.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }


    public void updateCompostTowerState() {
        if (!isController() && getControllerBE() != null) {
            getControllerBE().updateCompostTowerState();
            return;
        }

        if (compostData.evaluate(this)) {
            notifyUpdate();
        }
    }

    public void updateCompostData() {
        CompostTowerBlockEntity be = getControllerBE();
        if (be == null)
            return;
        be.compostData.updateRequired = 2;
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

        compostData.read(compound, clientPacket);

        if (!clientPacket) {
            inputInv.deserializeNBT(compound.getCompound("InputInventory"));
            outputInv.deserializeNBT(compound.getCompound("OutputInventory"));
            tankInventory.readFromNBT(compound.getCompound("TankInventory"));
            return;
        }

        boolean changeOfController =
                !Objects.equals(controllerBefore, controller);
        if (hasLevel() && (changeOfController || prevSize != radius || prevHeight != height)) {
            level.setBlocksDirty(getBlockPos(), Blocks.AIR.defaultBlockState(), getBlockState());
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

        if (!clientPacket) {
            compound.putString("StorageType", "CombinedInv");
            compound.put("InputInventory", inputInv.serializeNBT());
            compound.put("OutputInventory", outputInv.serializeNBT());
            compound.put("TankInventory", tankInventory.writeToNBT(new CompoundTag()));
        }
        compostData.write(compound, clientPacket);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (isItemHandlerCap(cap)) {
            initCapability();
            return itemCapability.cast();
        }
        if (isFluidHandlerCap(cap)) {
            initCapability();
            return fluidCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    public List<List<List<Inventory>>> inventories = null;

    private void initCapability() {
        assert level != null;

        var controller = getControllerBE();
        if (itemCapability.isPresent() && fluidCapability.isPresent() || controller == null)
            return;

        var h = getBlockPos().getY() - controller.getBlockPos().getY();
        var layerPos = controller.getBlockPos().above(h);
        CompostTowerBlockEntity layer = ConnectivityHandler.partAt(CRBlockEntityTypes.COMPOST_TOWER_BLOCK_ENTITY.get(), level, layerPos);
        if (layer == null) return;

        if (this == layer) {
            IItemHandlerModifiable[] itemHandlers = new IItemHandlerModifiable[radius * radius * 2];
            IFluidHandler[] fluidHandlers = new IFluidHandler[radius * radius];

            int index = 0;
            for (int x = 0; x < radius; x++) {
                for (int z = 0; z < radius; z++) {
                    BlockPos partPos = getBlockPos().offset(x, 0, z);
                    CompostTowerBlockEntity part = ConnectivityHandler.partAt(
                            this.getType(), level, partPos
                    );
                    itemHandlers[index] = part != null ? part.inputInv : new ItemStackHandler();
                    itemHandlers[index + radius * radius] = part != null ? part.outputInv : new ItemStackHandler();
                    fluidHandlers[index] = part != null ? part.tankInventory : new SmartFluidTank(1000, $->{});
                    index++;
                }
            }

            if (h == 0){
                itemCapability = LazyOptional.of(() -> new CompostTowerInventoryHandler(itemHandlers));
            } else {
                itemCapability = LazyOptional.of(() -> new CompostTowerInventoryHandler(itemHandlers) {
                    @Override
                    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                        return stack;
                    }
                });
            }
            fluidCapability = LazyOptional.of(() -> new CombinedTankWrapper(fluidHandlers));
        } else {
            itemCapability = layer.itemCapability;
            fluidCapability = layer.fluidCapability;
        }
    }
}
