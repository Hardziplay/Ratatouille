package org.forsteri.ratatouille.content.compost_tower;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.forsteri.ratatouille.content.thresher.ThresherBlockEntity;
import org.forsteri.ratatouille.entry.CRBlockEntityTypes;
import org.forsteri.ratatouille.entry.CRFluids;
import org.forsteri.ratatouille.entry.CRItems;
import org.forsteri.ratatouille.entry.CRRecipeTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.simibubi.create.content.fluids.tank.FluidTankBlockEntity.getCapacityMultiplier;

public class CompostTowerBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IMultiBlockEntityContainer.Inventory {
    protected LazyOptional<CompostTowerInventoryHandler> itemCapability = LazyOptional.empty();
    protected LazyOptional<CompostTowerFluidHandler> fluidCapability = LazyOptional.empty();
    private boolean updateConnectivity = true;
    protected BlockPos controller;
    protected BlockPos lastKnownPos;
    protected CompostData compostData;
    public ItemStackHandler inputInv = new ItemStackHandler();
    public ItemStackHandler outputInv = new ItemStackHandler();
    public FluidTank tankInventory = new SmartFluidTank(getCapacityMultiplier(), this::onFluidStackChanged);
    public IFluidHandler[] tanks = null;
    public HashMap<net.minecraft.world.level.material.Fluid, LerpedFloat> fluidLevels = new HashMap<>();
    public HashMap<net.minecraft.world.level.material.Fluid, LerpedFloat> gasLevels = new HashMap<>();

    public List<FluidStack> getSortedFluids() {
        List<FluidStack> fluids = new ArrayList<>();
        fluids.add(new FluidStack(CRFluids.COMPOST_FLUID.get(), getInputFluidAmount()));
        if (tanks == null) return fluids;

        for (IFluidHandler handler : tanks) {
            FluidStack stack = handler.getFluidInTank(0);
            if (!stack.isEmpty()) {
                fluids.add(stack.copy());
            }
        }
        fluids.sort(Comparator.comparingInt(
                (FluidStack fs) -> fs.getFluid().getFluidType().getDensity()
        ).reversed());

        return fluids;
    }

    private boolean canProcess(ItemStack stack) {
        ItemStackHandler tester = new ItemStackHandler(1);
        tester.setStackInSlot(0, stack);
        RecipeWrapper inventoryIn = new RecipeWrapper(tester);

        assert level != null;
        if (compostData.lastRecipe != null && compostData.lastRecipe.matches(inventoryIn, level))
            return true;
        return CRRecipeTypes.COMPOSTING.find(inventoryIn, level)
                .isPresent();
    }

    protected class CompostTowerInventoryHandler extends CombinedInvWrapper {
        private final int blockHeight;
        public CompostTowerInventoryHandler(IItemHandlerModifiable[] itemHandlers, int blockHeight) {
            super(itemHandlers);
            this.blockHeight = blockHeight;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (CompostTowerBlockEntity.this.outputInv == this.getHandlerFromIndex(this.getIndexForSlot(slot)))
                return false;
            return CompostTowerBlockEntity.this.canProcess(stack) && super.isItemValid(slot, stack);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (CompostTowerBlockEntity.this.outputInv == this.getHandlerFromIndex(this.getIndexForSlot(slot)))
                return stack;
            if (!this.isItemValid(slot, stack))
                return stack;
            var result = super.insertItem(slot, stack, simulate);
            if (!simulate) onInputStackChanged();
            return result;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (CompostTowerBlockEntity.this.inputInv == this.getHandlerFromIndex(this.getIndexForSlot(slot)) || blockHeight > 0)
                return ItemStack.EMPTY;
            var result = super.extractItem(slot, amount, simulate);
            if (!simulate) onInputStackChanged();
            return result;
        }
    }
    protected class CompostTowerFluidHandler extends CombinedTankWrapper {

        private final int blockHeight;

        public CompostTowerFluidHandler(IFluidHandler[] fluidHandlers, int blockHeight) {
            super(fluidHandlers);
            this.blockHeight = blockHeight;
            this.enforceVariety = true;
        }
    }

    public CompostTowerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        compostData = new CompostData();
    }

    @Override
    protected AABB createRenderBoundingBox() {
        if (isController())
            return super.createRenderBoundingBox().expandTowards(radius - 1, height - 1, radius - 1);
        else
            return super.createRenderBoundingBox();
    }

    @Override
    public void initialize() {
        super.initialize();
        sendData();
        if (level.isClientSide)
            invalidateRenderBoundingBox();
    }

    @Override
    public void tick() {
        super.tick();
        if (isController()) {
            compostData.tick(this);
            assert level != null;
            if (level.isClientSide) {
                fluidLevels.values().forEach(LerpedFloat::tickChaser);
                gasLevels.values().forEach(LerpedFloat::tickChaser);
            }
        }


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
        return controller == null || worldPosition.getX() == controller.getX()
                && worldPosition.getY() == controller.getY() && worldPosition.getZ() == controller.getZ();
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
        notifyUpdate();
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
        onFluidStackChanged(tankInventory.getFluid());
        notifyUpdate();
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
        onFluidStackChanged(tankInventory.getFluid());
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



    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CompostTowerBlockEntity controllerBE = getControllerBE();
        if (controllerBE == null)
            return false;

        return controllerBE.compostData.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }

    public float getFillState(int amount) {
        return amount / (radius * radius * height * (float) getCapacityMultiplier());
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

    public int getInputFluidAmount() {
        return inputInv.getStackInSlot(0).getCount() * getCapacityMultiplier() / 64 * radius * radius;
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
            inputInv.deserializeNBT(compound.getCompound("InputInventory"));
            outputInv.deserializeNBT(compound.getCompound("OutputInventory"));
            var inputLevel = fluidLevels.get(CRFluids.COMPOST_FLUID.get());
            var amount = getInputFluidAmount();
            if (inputLevel == null) {
                inputLevel = LerpedFloat.linear()
                        .startWithValue(0)
                        .chase(getFillState(amount), 0.5f, LerpedFloat.Chaser.EXP);
                fluidLevels.put(CRFluids.COMPOST_FLUID.get(), inputLevel);
            }

            inputLevel.chase(getFillState(amount), 0.5f, LerpedFloat.Chaser.EXP);
            if (compound.contains("Tanks")) {
//                tanks = new IFluidHandler[radius * radius * height];
                ListTag tanksNBT = compound.getList("Tanks", Tag.TAG_COMPOUND);
                for (int i = 0; i < tanksNBT.size() && i < tanks.length; i++) {
                    CompoundTag tankTag = tanksNBT.getCompound(i);
                    tanks[i] = new SmartFluidTank(getCapacityMultiplier(), this::onFluidStackChanged);
                    if (tanks[i] instanceof SmartFluidTank tank) {
                        tank.readFromNBT(tankTag);
                    }
                    var newFluidStack = tanks[i].getFluidInTank(0);
                    var levels = newFluidStack.getFluid().getFluidType().isLighterThanAir() ? gasLevels : fluidLevels;
                    LerpedFloat fluidLevel = levels.get(newFluidStack.getFluid());
                    amount = newFluidStack.getAmount();
                    if (fluidLevel == null) {
                        fluidLevel = LerpedFloat.linear()
                                .startWithValue(0)
                                .chase(getFillState(amount), 0.5f, LerpedFloat.Chaser.EXP);
                        levels.put(newFluidStack.getFluid(), fluidLevel);
                    }
                    fluidLevel.chase(getFillState(amount), 0.5f, LerpedFloat.Chaser.EXP);
                }
            }
        }

        compostData.read(compound, clientPacket);

        if (!clientPacket) {
            tankInventory.readFromNBT(compound.getCompound("TankInventory"));
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
            compound.putString("StorageType", "CombinedInv");
            compound.put("InputInventory", inputInv.serializeNBT());
            compound.put("OutputInventory", outputInv.serializeNBT());
            if (fluidCapability.isPresent()) {
                ListTag tanksNBT = new ListTag();
                for (IFluidHandler handler : tanks) {
                    if (handler instanceof SmartFluidTank tank) {
                        CompoundTag tankTag = new CompoundTag();
                        tank.writeToNBT(tankTag);
                        tanksNBT.add(tankTag);
                    }
                }
                compound.put("Tanks", tanksNBT);
            }
        }

        super.write(compound, clientPacket);

        if (!clientPacket) {
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

    private void initCapability() {
        assert level != null;

        var controller = getControllerBE();
        if (itemCapability.isPresent() && fluidCapability.isPresent() || controller == null)
            return;

        var blockHeight = getBlockPos().getY() - controller.getBlockPos().getY();
        if (this == controller) {
            tanks = new IFluidHandler[radius * radius * height];

            int index = 0;
            for (int x = 0; x < radius; x++) {
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < radius; z++) {
                        BlockPos partPos = getBlockPos().offset(x, y, z);
                        CompostTowerBlockEntity part = ConnectivityHandler.partAt(
                                this.getType(), level, partPos
                        );
                        tanks[index] = part != null ? part.tankInventory : new SmartFluidTank(getCapacityMultiplier(), this::onFluidStackChanged);
                        index++;
                    }
                }
            }
            itemCapability = LazyOptional.of(() -> new CompostTowerInventoryHandler(new IItemHandlerModifiable[]{inputInv, outputInv}, blockHeight));
        } else {
            controller.initCapability();
            tanks = controller.tanks;
            itemCapability = LazyOptional.of(() -> new CompostTowerInventoryHandler(new IItemHandlerModifiable[]{controller.inputInv, controller.outputInv}, blockHeight));
        }
        fluidCapability = LazyOptional.of(() -> new CompostTowerFluidHandler(tanks, blockHeight));
    }

    protected void onInputStackChanged() {
        if (level == null)
            return;
        if (!level.isClientSide) {
            notifyUpdate();
        }
//        if (isVirtual()) {
//        }
    }

    protected void onFluidStackChanged(FluidStack newFluidStack) {
        if (level == null)
            return;
        if (!level.isClientSide) {
            notifyUpdate();
        }
    }
}
