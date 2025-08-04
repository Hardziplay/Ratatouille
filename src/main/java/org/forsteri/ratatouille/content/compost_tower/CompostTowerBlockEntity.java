package org.forsteri.ratatouille.content.compost_tower;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import java.util.List;

public class CompostTowerBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IMultiBlockEntityContainer.Fluid, IMultiBlockEntityContainer.Inventory {
    protected LazyOptional<IFluidHandler> fluidCapability = LazyOptional.empty();
    protected LazyOptional<CombinedInvWrapper> itemCapability = LazyOptional.empty();
    protected ItemStackHandler inputInv = new ItemStackHandler(1);
    protected ItemStackHandler outputInv = new ItemStackHandler(1);

    private boolean updateConnectivity = true;
    protected BlockPos controller;
    protected BlockPos lastKnownPos;

    public int width = 1;
    public int height = 1;

    public CompostData compostData;

    public CompostTowerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        compostData = new CompostData();
    }

    @Override
    public boolean hasInventory() {
        return true;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

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

    @SuppressWarnings("unchecked")
    @Override
    public CompostTowerBlockEntity getControllerBE() {
        assert level != null;

        if (isController())
            return this;
        BlockEntity blockEntity = level.getBlockEntity(controller);
        if (blockEntity instanceof CompostTowerBlockEntity tower)
            return tower;
        return null;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CompostTowerBlockEntity controllerBE = getControllerBE();
        if (controllerBE == null)
            return false;

        return controllerBE.compostData.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }

    @Override
    public BlockPos getController() {
        return isController() ? worldPosition : controller;
    }

    @Override
    public boolean isController() {
        return controller == null || worldPosition.equals(controller);
    }

    @Override
    public void setController(BlockPos controller) {
        assert level != null;

        if (level.isClientSide && !isVirtual())
            return;
        if (controller.equals(this.controller))
            return;
        this.controller = controller;
        refreshCapability();
        setChanged();
        sendData();
    }

    private void refreshCapability() {
        itemCapability.invalidate();
        fluidCapability.invalidate();
    }

    @Override
    public void removeController(boolean keepContents) {
        assert level != null;
        if (level.isClientSide())
            return;
        updateConnectivity = true;
        controller = null;
        setWidth(1);
        setHeight(1);

        itemCapability.invalidate();
        fluidCapability.invalidate();
        compostData.clear();

        refreshCapability();
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
    protected void read(CompoundTag compound, boolean clientPacket) {
        assert level != null;
        super.read(compound, clientPacket);

        BlockPos controllerBefore = controller;
        int prevSize = width;
        int prevHeight = height;

        updateConnectivity = compound.contains("Uninitialized");
        controller = null;
        lastKnownPos = null;

        if (compound.contains("LastKnownPos"))
            lastKnownPos = NbtUtils.readBlockPos(compound.getCompound("LastKnownPos"));
        if (compound.contains("Controller"))
            controller = NbtUtils.readBlockPos(compound.getCompound("Controller"));

        if (isController()) {
            setWidth(compound.getInt("Size"));
            setHeight(compound.getInt("Height"));
        }

        this.inputInv.deserializeNBT(compound.getCompound("InputInventory"));
        this.outputInv.deserializeNBT(compound.getCompound("OutputInventory"));

        compostData.read(compound, clientPacket);

        if (!clientPacket) {
            return;
        }

        boolean changeOfController =
                !Objects.equals(controllerBefore, controller);
        if (hasLevel() && (changeOfController || prevSize != getWidth() || prevHeight != getHeight())) {
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
            compound.putInt("Size", getWidth());
            compound.putInt("Height", getHeight());

        }

        super.write(compound, clientPacket);

        compound.putString("StorageType", "CombinedInv");
        compound.put("InputInventory", this.inputInv.serializeNBT());
        compound.put("OutputInventory", this.outputInv.serializeNBT());
        compostData.write(compound, clientPacket);
    }

    @Override
    public void notifyMultiUpdated() {
        assert level != null;

        level.setBlock(getBlockPos(), getBlockState().setValue(CompostTowerBlock.IS_2x2, getWidth() == 2), 6);

        itemCapability.invalidate();
        fluidCapability.invalidate();
        updateCompostTowerState();
        setChanged();
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
        return this.height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            initCapability();
            return itemCapability.cast();
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            initCapability();
            return fluidCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    private void initCapability() {
        if (itemCapability.isPresent() && fluidCapability.isPresent())
            return;

        if (!isController()) {
            CompostTowerBlockEntity controllerBE = getControllerBE();
            if (controllerBE != null) {
                controllerBE.initCapability();
                this.itemCapability = controllerBE.itemCapability;
                this.fluidCapability = controllerBE.fluidCapability;
            }
            return;
        }

        IItemHandlerModifiable[] itemHandlers = new IItemHandlerModifiable[getWidth() * getWidth()  * getHeight()];
        IFluidHandler[] fluidHandlers = new IFluidHandler[getWidth()  * getWidth()  * getHeight()];

        int index = 0;
        for (int x = 0; x < getWidth() ; x++) {
            for (int y = 0; y < getHeight(); y++) {
                for (int z = 0; z < getWidth() ; z++) {
                    BlockPos partPos = worldPosition.offset(x, y, z);
                    CompostTowerBlockEntity part = ConnectivityHandler.partAt(
                            this.getType(), level, partPos
                    );
//                    itemHandlers[index] =
//                            part != null ? part.itemInventory : new ItemStackHandler();
//                    fluidHandlers[index] =
//                            part != null ? part.compostFluidInventory : new SmartFluidTank(1000, (a)->{});
                    index++;
                }
            }
        }
    }
}

