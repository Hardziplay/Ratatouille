package org.forsteri.ratatouille.content.squeeze_basin;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.content.processing.basin.BasinBlock;
import com.simibubi.create.content.processing.basin.BasinInventory;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.*;

import java.util.*;

import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.forsteri.ratatouille.entry.CRItems;

import javax.annotation.Nonnull;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;
import static org.forsteri.ratatouille.content.squeeze_basin.SqueezeBasinBlock.CASING;

public class SqueezeBasinBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    public SqueezeBasinInventory inputInventory;
    public SmartFluidTankBehaviour inputTank;
    protected SmartInventory outputInventory;
    protected LazyOptional<IItemHandlerModifiable> itemCapability;
    protected LazyOptional<IFluidHandler> fluidCapability;
    private boolean contentsChanged;
    protected List<ItemStack> spoutputBuffer;
    int recipeBackupCheck;

    public SqueezeBasinBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.inputInventory = (new SqueezeBasinInventory(9, this));
        this.inputInventory.whenContentsChanged($ -> this.contentsChanged = true).withMaxStackSize(64);
        this.outputInventory = (new SqueezeBasinInventory(1, this)).forbidInsertion().withMaxStackSize(1);
        this.itemCapability = LazyOptional.of(() -> new CombinedInvWrapper(inputInventory, outputInventory));
        this.contentsChanged = true;
        this.spoutputBuffer = new ArrayList();
        this.recipeBackupCheck = 20;
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (!this.level.isClientSide) {
            if (this.recipeBackupCheck-- <= 0) {
                this.recipeBackupCheck = 20;
                if (!this.isEmpty()) {
                    this.notifyChangeOfContents();
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!spoutputBuffer.isEmpty() && !level.isClientSide)
            tryClearingSpoutputOverflow();
        if (!contentsChanged)
            return;
        contentsChanged = false;
        getOperator().ifPresent((be) -> {be.pressingBehaviour.start(PressingBehaviour.Mode.BASIN);});
    }


    private void tryClearingSpoutputOverflow() {
        BlockState blockState = getBlockState();
        if (!(blockState.getBlock() instanceof SqueezeBasinBlock))
            return;
        Direction direction = blockState.getValue(HORIZONTAL_FACING);
        BlockEntity be = this.level.getBlockEntity(this.worldPosition.below().relative(direction));
        InvManipulationBehaviour inserter = null;
        if (be != null) {
            inserter = BlockEntityBehaviour.get(this.level, be.getBlockPos(), InvManipulationBehaviour.TYPE);
        }
        IItemHandler targetInv = be == null ? null
                : be.getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite())
                .orElse(inserter == null ? null : inserter.getInventory());

        for (Iterator<ItemStack> iterator = spoutputBuffer.iterator(); iterator.hasNext();) {
            ItemStack itemStack = iterator.next();
            if (targetInv != null) {
                if (ItemHandlerHelper.insertItemStacked(targetInv, itemStack, true).isEmpty()) {
                    ItemHandlerHelper.insertItemStacked(targetInv, itemStack.copy(), false);
                    iterator.remove();
                    notifyChangeOfContents();
                    sendData();
                }
            }
        }
    }

    public boolean isEmpty() {
        return this.inputInventory.isEmpty() && this.outputInventory.isEmpty();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(this));
        this.inputTank = (new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 1, 1000, true)).whenFluidUpdates(() -> {
            this.contentsChanged = true;
        });
        behaviours.add(this.inputTank);
        fluidCapability = LazyOptional.of(() -> {
            LazyOptional<? extends IFluidHandler> inputCap = inputTank.getCapability();
            return new CombinedTankWrapper(inputCap.orElse(null));
        });
    }

    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        this.inputInventory.deserializeNBT(compound.getCompound("InputItems"));
        this.outputInventory.deserializeNBT(compound.getCompound("OutputItems"));
        this.spoutputBuffer = NBTHelper.readItemList(compound.getList("Overflow", 10));
    }

    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.put("InputItems", this.inputInventory.serializeNBT());
        compound.put("OutputItems", this.outputInventory.serializeNBT());
        compound.put("Overflow", NBTHelper.writeItemList(this.spoutputBuffer));
    }

    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(this.level, this.worldPosition, this.inputInventory);
        ItemHelper.dropContents(this.level, this.worldPosition, this.outputInventory);
        if (this.getBlockState().getValue(CASING))
            Containers.dropItemStack(this.level, (double)this.worldPosition.getX(), (double)this.worldPosition.getY(), (double)this.worldPosition.getZ(), new ItemStack(CRItems.SAUSAGE_CASING.get(), 1));

        this.spoutputBuffer.forEach((is) -> {
            Block.popResource(this.level, this.worldPosition, is);
        });
    }

    public void remove() {
        super.remove();
        this.onEmptied();
    }

    public void onEmptied() {
        this.getOperator().ifPresent((be) -> {
            be.basinRemoved = true;
        });
    }

    public void invalidate() {
        super.invalidate();
        this.itemCapability.invalidate();
        fluidCapability.invalidate();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER)
            return itemCapability.cast();
        if (cap == ForgeCapabilities.FLUID_HANDLER)
            return fluidCapability.cast();
        return super.getCapability(cap, side);
    }

    private Optional<MechanicalPressBlockEntity> getOperator() {
        if (this.level == null) {
            return Optional.empty();
        } else {
            BlockEntity be = this.level.getBlockEntity(this.worldPosition.above(2));
            return be instanceof MechanicalPressBlockEntity ? Optional.of((MechanicalPressBlockEntity)be) : Optional.empty();
        }
    }

    public void notifyChangeOfContents() {
        this.contentsChanged = true;
    }

    public boolean acceptOutputs(List<ItemStack> outputItems, List<FluidStack> outputFluids, boolean simulate) {
        this.outputInventory.allowInsertion();
        boolean acceptOutputsInner = this.acceptOutputsInner(outputItems, simulate);
        this.outputInventory.forbidInsertion();
        return acceptOutputsInner;
    }

    private boolean acceptOutputsInner(List<ItemStack> outputItems, boolean simulate) {
        BlockState blockState = this.getBlockState();
        if (!(blockState.getBlock() instanceof SqueezeBasinBlock)) {
            return false;
        } else {
            Direction direction = (Direction) blockState.getValue(SqueezeBasinBlock.FACING);
            if (direction == Direction.DOWN) {
                IItemHandler targetInv = this.outputInventory;
                if (targetInv == null && !outputItems.isEmpty()) {
                    return false;
                } else if (!this.acceptItemOutputsIntoBasin(outputItems, simulate, targetInv)) {
                    return false;
                } else {
                    return true;
                }
            } else {
                BlockEntity be = this.level.getBlockEntity(this.worldPosition.below().relative(direction));
                InvManipulationBehaviour inserter = be == null ? null : (InvManipulationBehaviour) BlockEntityBehaviour.get(this.level, be.getBlockPos(), InvManipulationBehaviour.TYPE);
                IItemHandler targetInv = be == null ? null : (IItemHandler) be.getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite()).orElse(inserter == null ? null : (IItemHandler) inserter.getInventory());
                if (!outputItems.isEmpty() && targetInv == null) {
                    return false;
                } else {
                    if (simulate) {
                        return true;
                    } else {
                        Iterator var11 = outputItems.iterator();

                        while (var11.hasNext()) {
                            ItemStack itemStack = (ItemStack) var11.next();
                            this.spoutputBuffer.add(itemStack.copy());
                        }
                        return true;
                    }
                }
            }
        }
    }

    private boolean acceptItemOutputsIntoBasin(List<ItemStack> outputItems, boolean simulate, IItemHandler targetInv) {
        Iterator var4 = outputItems.iterator();

        ItemStack itemStack;
        do {
            if (!var4.hasNext()) {
                return true;
            }

            itemStack = (ItemStack)var4.next();
        } while(ItemHandlerHelper.insertItemStacked(targetInv, itemStack.copy(), simulate).isEmpty());

        return false;
    }

}
