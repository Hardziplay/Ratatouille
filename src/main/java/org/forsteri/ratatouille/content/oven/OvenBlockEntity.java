package org.forsteri.ratatouille.content.oven;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;
import java.util.Objects;

public class OvenBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IMultiBlockEntityContainer.Inventory {
    protected LazyOptional<IItemHandler> itemCapability = LazyOptional.empty();
    private boolean updateConnectivity = true;
    protected BlockPos controller;
    protected BlockPos lastKnownPos;

    protected ItemStackHandler inventory = new ItemStackHandler(10 /* todo: Demo*/) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
        }
    };

    public OvenBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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

        itemCapability.invalidate();
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

    protected int height;

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    protected int radius;

    @Override
    public int getWidth() {
        return radius;
    }

    @Override
    public void setWidth(int width) {
        radius = width;
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

        if (!clientPacket) {
            inventory.deserializeNBT(compound.getCompound("Inventory"));
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
            compound.put("Inventory", inventory.serializeNBT());
        }
    }
}
