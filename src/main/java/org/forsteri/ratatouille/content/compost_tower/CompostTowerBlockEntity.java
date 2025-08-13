package org.forsteri.ratatouille.content.compost_tower;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.forsteri.ratatouille.entry.CRBlockEntityTypes;
import org.forsteri.ratatouille.entry.CRRecipeTypes;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.simibubi.create.content.fluids.tank.FluidTankBlockEntity.getCapacityMultiplier;

public class CompostTowerBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IMultiBlockEntityContainer.Fluid {
    public HashMap<net.minecraft.world.level.material.Fluid, LerpedFloat> fluidLevels = new HashMap<>();
    public HashMap<net.minecraft.world.level.material.Fluid, LerpedFloat> gasLevels = new HashMap<>();
    protected CompostTowerInventoryHandler itemCapability;
    protected CompostTowerFluidHandler fluidCapability;
    protected BlockPos controller;
    protected BlockPos lastKnownPos;
    protected CompostData compostData;
    protected CompostFluidTank tankInventory = new CompostFluidTank(getCapacityMultiplier(), this::onFluidStackChanged);
    protected int height = 1;
    protected int radius = 1;
    private boolean updateConnectivity = true;

    public CompostTowerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        compostData = new CompostData();
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                CRBlockEntityTypes.COMPOST_TOWER_BLOCK_ENTITY.get(),
                (be, context) -> {
                    be.initCapability();
                    if (be.fluidCapability == null)
                        return null;
                    return be.fluidCapability;
                }
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                CRBlockEntityTypes.COMPOST_TOWER_BLOCK_ENTITY.get(),
                (be, context) -> {
                    be.initCapability();
                    if (be.itemCapability == null)
                        return null;
                    return be.itemCapability;
                }
        );
    }

    private void initCapability() {
        assert level != null;

        var controller = getControllerBE();
        if (itemCapability != null && fluidCapability != null || controller == null)
            return;

        var tank = isController() ? tankInventory : controller.tankInventory;

        itemCapability = new CompostTowerInventoryHandler(tank);
        fluidCapability = new CompostTowerFluidHandler(tank);
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

    private int getTowerHeight() {
        var be = getControllerBE();
        if (be == null) return 1;
        return be.height;
    }

    private int getOutputHeight() {
        var be = getControllerBE();
        if (be == null) return 0;
        return getBlockPos().getY() - be.getBlockPos().getY();
    }

    @Override
    public int getTankSize(int tank) {
        return getCapacityMultiplier();
    }

    @Override
    public void setTankSize(int tank, int blocks) {
        applyFluidTankSize(1);
    }

    public void applyFluidTankSize(int blocks) {
        tankInventory.setCapacity(blocks * getCapacityMultiplier());
        tankInventory.clearOverflow();
    }

    @Override
    public FluidStack getFluid(int tank) {
        return tankInventory.getFluidInTank(tank);
    }

    @Override
    protected AABB createRenderBoundingBox() {
        if (isController())
            return super.createRenderBoundingBox().expandTowards(radius - 1, height - 1, radius - 1);
        else
            return super.createRenderBoundingBox();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    public void initialize() {
        super.initialize();
        notifyUpdate();
        if (level == null) return;
        if (level.isClientSide)
            invalidateRenderBoundingBox();
    }

    @Override
    public void tick() {
        super.tick();

        if (lastKnownPos == null)
            lastKnownPos = getBlockPos();
        else if (!lastKnownPos.equals(worldPosition)) {
            removeController(true);
            lastKnownPos = worldPosition;
            return;
        }

        if (updateConnectivity)
            updateConnectivity();
        if (isController())
            compostData.tick(this);
        fluidLevels.values().forEach(LerpedFloat::tickChaser);
        gasLevels.values().forEach(LerpedFloat::tickChaser);
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

    private void refreshCapability() {
        itemCapability = null;
        fluidCapability = null;
        invalidateCapabilities();
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        if (updateConnectivity)
            tag.putBoolean("Uninitialized", true);
        if (lastKnownPos != null)
            tag.put("LastKnownPos", NbtUtils.writeBlockPos(lastKnownPos));
        if (!isController())
            tag.put("Controller", NbtUtils.writeBlockPos(controller));
        if (isController()) {
            tag.putInt("Size", radius);
            tag.putInt("Height", height);
            applyFluidTankSize(getTotalTankSize());
            tag.put("TankContent", tankInventory.serializeNBT(registries));
        }

        super.write(tag, registries, clientPacket);
        compostData.write(tag, clientPacket);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);

        BlockPos controllerBefore = controller;
        int prevSize = radius;
        int prevHeight = height;

        updateConnectivity = tag.contains("Uninitialized");
        controller = null;
        lastKnownPos = null;

        if (tag.contains("LastKnownPos"))
            lastKnownPos = NBTHelper.readBlockPos(tag, "LastKnownPos");
        if (tag.contains("Controller"))
            controller = NBTHelper.readBlockPos(tag, "Controller");

        if (isController()) {
            radius = tag.getInt("Size");
            height = tag.getInt("Height");
            tankInventory.deserializeNBT(registries, tag.getCompound("TankContent"));
            applyFluidTankSize(getTotalTankSize());

            for (var fluid : tankInventory.getSortedFluids()) {
                var targetLevel = fluid.getFluidType().isLighterThanAir() ? gasLevels : fluidLevels;
                var fluidLevel = targetLevel.get(fluid);
                if (fluidLevel == null) {
                    fluidLevel = LerpedFloat.linear()
                            .startWithValue(tankInventory.getFilledPercentage(fluid));
                    targetLevel.put(fluid, fluidLevel);
                }
                fluidLevel.chase(tankInventory.getFilledPercentage(fluid), 0.5f, LerpedFloat.Chaser.EXP);
            }
        }

        compostData.read(tag, clientPacket);
        if (!clientPacket) {
            return;
        }

        boolean changeOfController =
                !Objects.equals(controllerBefore, controller);
        if (level != null && (changeOfController || prevSize != radius || prevHeight != height)) {
            level.setBlocksDirty(getBlockPos(), Blocks.AIR.defaultBlockState(), getBlockState());
            if (hasLevel())
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 16);
            if (isController())
                applyFluidTankSize(getTotalTankSize());

            invalidateRenderBoundingBox();
        }
    }

    public int getTotalTankSize() {
        return radius * radius * height;
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
        if (!keepContents)
            applyFluidTankSize(1);

        controller = null;
        radius = 1;
        height = 1;

        refreshCapability();
        compostData.clear();
        tankInventory.onContentsChanged();
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

    @Override
    public void notifyMultiUpdated() {
        assert level != null;

        level.setBlock(getBlockPos(), getBlockState().setValue(CompostTowerBlock.IS_2x2, getWidth() == 2), 6);

        refreshCapability();
        tankInventory.onContentsChanged();
        updateCompostTowerState();
        if (isController()) {
            for (int xOffset = 0; xOffset < radius; xOffset++) {
                for (int yOffset = 0; yOffset < height; yOffset++) {
                    for (int zOffset = 0; zOffset < radius; zOffset++) {
                        BlockPos vaultPos = worldPosition.offset(xOffset, yOffset, zOffset);
                        CompostTowerBlockEntity vaultAt =
                                ConnectivityHandler.partAt(CRBlockEntityTypes.COMPOST_TOWER_BLOCK_ENTITY.get(), level, vaultPos);
                        if (vaultAt == null || this == vaultAt) continue;
                        tankInventory.fillFrom(vaultAt.tankInventory);
                        vaultAt.tankInventory.clear();
                    }
                }
            }
        }

        notifyUpdate();
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

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

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

    public CompostItemHandler getItemInventory() {
        return new CompostItemHandler(getControllerBE().tankInventory);
    }

    protected void onFluidStackChanged(HashMap<net.minecraft.world.level.material.Fluid, Integer> tanks) {
        if (level == null)
            return;
        if (!level.isClientSide) {
            notifyUpdate();
        }
    }

    protected class CompostTowerInventoryHandler extends CompostItemHandler {
        public CompostTowerInventoryHandler(CompostFluidTank tank) {
            super(tank);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {

            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return CompostTowerBlockEntity.this.canProcess(stack) && super.isItemValid(slot, stack);
        }
    }

    protected class CompostTowerFluidHandler extends CompostFluidTank {
        public CompostTowerFluidHandler(CompostFluidTank tank) {
            super(tank.fluidIds, tank.tanks, tank.updateCallback, tank.index, tank.capacity);
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            var outputHeight = CompostTowerBlockEntity.this.getOutputHeight();
            var towerHeight = CompostTowerBlockEntity.this.getTowerHeight();
            var availFluid = getFluidAtBlockHeight(outputHeight, towerHeight);

            if (!resource.getFluid().isSame(availFluid)) return FluidStack.EMPTY;
            return super.drain(resource, action);
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            var outputHeight = CompostTowerBlockEntity.this.getOutputHeight();
            var towerHeight = CompostTowerBlockEntity.this.getTowerHeight();
            var availFluid = getFluidAtBlockHeight(outputHeight, towerHeight);

            return super.drain(new FluidStack(availFluid, maxDrain), action);
        }
    }
}
