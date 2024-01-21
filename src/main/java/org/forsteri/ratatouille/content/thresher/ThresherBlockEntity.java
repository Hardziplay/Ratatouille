package org.forsteri.ratatouille.content.thresher;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.Direction.Axis;
import java.util.List;
import java.util.Optional;

import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.forsteri.ratatouille.entry.CRRecipeTypes;

public class ThresherBlockEntity extends KineticBlockEntity {
    public ItemStackHandler inputInv;
    public ItemStackHandler outputInv;
    public LazyOptional<IItemHandler> capability;
    public int timer;
    private ThreshingRecipe lastRecipe;

    public ThresherBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        inputInv = new ItemStackHandler(1);
        outputInv = new ItemStackHandler(4);
        capability = LazyOptional.of(ThresherInventoryHandler::new);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(this));
        super.addBehaviours(behaviours);
    }

    @Override
    public void tick() {
        super.tick();
        if (getSpeed() == 0.0F)
            return;
        for (int i = 0; i < outputInv.getSlots(); i++)
            if (outputInv.getStackInSlot(i).getCount() == outputInv.getSlotLimit(i))
                return;
        if (timer > 0) {
            timer -= getProcessingSpeed();
            if (level.isClientSide) {
                spawnParticles();
                return;
            }
            if (timer <= 0)
                process();
            return;
        }

        if (canOutput()) {
            Direction direction = getEjectDirection();
            for (int slot = 0; slot < outputInv.getSlots(); slot++) {
                ItemStack stack = outputInv.getStackInSlot(slot);
                if (!stack.isEmpty()) {
                    BlockEntity be = this.level.getBlockEntity(this.worldPosition.below().relative(direction));
                    InvManipulationBehaviour inserter = null;
                    if (be != null) {
                        inserter = (InvManipulationBehaviour)BlockEntityBehaviour.get(this.level, be.getBlockPos(), InvManipulationBehaviour.TYPE);
                    }

                    IItemHandler targetInv = be == null ? null : (IItemHandler)be.getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite()).orElse(inserter == null ? null : (IItemHandler)inserter.getInventory());
                    if (targetInv != null) {
                        if (ItemHandlerHelper.insertItemStacked(targetInv, stack, true).isEmpty()) {
                            ItemHandlerHelper.insertItemStacked(targetInv, stack.copy(), false);
                            outputInv.setStackInSlot(0, ItemStack.EMPTY);
                            notifyUpdate();
                        }
                    }
                }
            }
        }

        if (inputInv.getStackInSlot(0).isEmpty()) return;
        RecipeWrapper inventoryIn = new RecipeWrapper(inputInv);
        if (lastRecipe == null || !lastRecipe.matches(inventoryIn, level)) {
            Optional<ThreshingRecipe> recipe = CRRecipeTypes.THRESHING.find(inventoryIn, level);
            if (recipe.isEmpty()) {
                timer = 100;
            } else {
                lastRecipe = (ThreshingRecipe) recipe.get();
                timer = lastRecipe.getProcessingDuration();
            }
            sendData();
        } else {
            timer = lastRecipe.getProcessingDuration();
            sendData();
        }
    }

    private Direction getEjectDirection() {
        var block = ((ThresherBlock) getBlockState().getBlock());
        var speed = getSpeed();
        block.getRotationAxis(getBlockState());
        boolean rotation = speed >= 0;
        Direction ejectDirection = Direction.UP;
        switch (block.getRotationAxis(getBlockState())) {
            case X -> {
                ejectDirection = rotation ? Direction.NORTH : Direction.SOUTH;
            }
            case Z -> {
                ejectDirection = rotation ? Direction.EAST : Direction.WEST;
            }
        }
        return ejectDirection;
    }

    public void invalidate() {
        super.invalidate();
        this.capability.invalidate();
    }
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(this.level, this.worldPosition, this.inputInv);
        ItemHelper.dropContents(this.level, this.worldPosition, this.outputInv);
    }

    private void process() {
        RecipeWrapper inventoryIn = new RecipeWrapper(this.inputInv);
        if (this.lastRecipe == null || !this.lastRecipe.matches(inventoryIn, this.level)) {
            Optional<ThreshingRecipe> recipe = CRRecipeTypes.THRESHING.find(inventoryIn, this.level);
            if (recipe.isEmpty()) {
                return;
            }

            this.lastRecipe = (ThreshingRecipe) recipe.get();
        }

        ItemStack stackInSlot = this.inputInv.getStackInSlot(0);
        stackInSlot.shrink(1);
        this.inputInv.setStackInSlot(0, stackInSlot);
        this.lastRecipe.rollResults().forEach((stack) -> {
            ItemHandlerHelper.insertItemStacked(this.outputInv, stack, false);
        });
        this.sendData();
        this.setChanged();
    }

    public void spawnParticles() {
        ItemStack stackInSlot = inputInv.getStackInSlot(0);
        if (stackInSlot.isEmpty()) return;

        ItemParticleOption data = new ItemParticleOption(ParticleTypes.ITEM, stackInSlot);
        float angle = level.random.nextFloat() * 360;
        Vec3 offset = new Vec3(0, 0, 0.5f);
        offset = VecHelper.rotate(offset, angle, Axis.Y);
        Vec3 target = VecHelper.rotate(offset, getSpeed() > 0 ? 25 : -25, Axis.Y);

        Vec3 center = offset.add(VecHelper.getCenterOf(worldPosition));
        target = VecHelper.offsetRandomly(target.subtract(offset), level.random, 1 / 128f);
        level.addParticle(data, center.x, center.y, center.z, target.x, target.y, target.z);
    }
    public void write(CompoundTag compound, boolean clientPacket) {
        compound.putInt("Timer", this.timer);
        compound.put("InputInventory", this.inputInv.serializeNBT());
        compound.put("OutputInventory", this.outputInv.serializeNBT());
        super.write(compound, clientPacket);
    }

    protected void read(CompoundTag compound, boolean clientPacket) {
        this.timer = compound.getInt("Timer");
        this.inputInv.deserializeNBT(compound.getCompound("InputInventory"));
        this.outputInv.deserializeNBT(compound.getCompound("OutputInventory"));
        super.read(compound, clientPacket);
    }
    public int getProcessingSpeed() {
        return Mth.clamp((int) Math.abs(getSpeed() / 16f), 1, 512);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (isItemHandlerCap(cap))
            return capability.cast();
        return super.getCapability(cap, side);
    }

    private boolean canProcess(ItemStack stack) {
        ItemStackHandler tester = new ItemStackHandler(1);
        tester.setStackInSlot(0, stack);
        RecipeWrapper inventoryIn = new RecipeWrapper(tester);

        if (lastRecipe != null && lastRecipe.matches(inventoryIn, level))
            return true;
        return CRRecipeTypes.THRESHING.find(inventoryIn, level)
                .isPresent();
    }

    private boolean canOutput() {
        Direction direction = getEjectDirection();
        BlockPos neighbour = getBlockPos().relative(direction);
        BlockPos output = neighbour.below();
        BlockState blockState = level.getBlockState(neighbour);
        if (FunnelBlock.isFunnel(blockState)) {
            if (FunnelBlock.getFunnelFacing(blockState) == direction) {
                return false;
            }
        } else {
            if (!blockState.getCollisionShape(level, neighbour).isEmpty()) {
                return false;
            }

            BlockEntity blockEntity = level.getBlockEntity(output);
            if (blockEntity instanceof BeltBlockEntity) {
                BeltBlockEntity belt = (BeltBlockEntity)blockEntity;
                return belt.getSpeed() == 0.0F || belt.getMovementFacing() != direction.getOpposite();
            }
        }

        DirectBeltInputBehaviour directBeltInputBehaviour = (DirectBeltInputBehaviour) BlockEntityBehaviour.get(level, output, DirectBeltInputBehaviour.TYPE);
        return directBeltInputBehaviour != null ? directBeltInputBehaviour.canInsertFromSide(direction) : false;
    }

    private class ThresherInventoryHandler extends CombinedInvWrapper {
        public ThresherInventoryHandler() {
            super(inputInv, outputInv);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (outputInv == getHandlerFromIndex(getIndexForSlot(slot)))
                return false;
            return canProcess(stack) && super.isItemValid(slot, stack);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (outputInv == getHandlerFromIndex(getIndexForSlot(slot)))
                return stack;
            if (!isItemValid(slot, stack))
                return stack;
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (inputInv == getHandlerFromIndex(getIndexForSlot(slot)))
                return ItemStack.EMPTY;
            return super.extractItem(slot, amount, simulate);
        }
    }
}

