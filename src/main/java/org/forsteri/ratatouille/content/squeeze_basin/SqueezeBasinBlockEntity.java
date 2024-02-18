package org.forsteri.ratatouille.content.squeeze_basin;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;

import java.util.*;

import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.forsteri.ratatouille.entry.CRItems;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;
import static org.forsteri.ratatouille.content.squeeze_basin.SqueezeBasinBlock.CASING;

public class SqueezeBasinBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    public SqueezeBasinInventory inputInventory = new SqueezeBasinInventory(9, this);
    protected SmartInventory outputInventory;
    protected LazyOptional<IItemHandlerModifiable> itemCapability;
    private boolean contentsChanged;
    List<Direction> disabledSpoutputs;
    Direction preferredSpoutput;
    protected List<ItemStack> spoutputBuffer;
    int recipeBackupCheck;

    public SqueezeBasinBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.inputInventory.whenContentsChanged(($) -> {
            this.contentsChanged = true;
        });
        this.outputInventory = (new SqueezeBasinInventory(9, this)).forbidInsertion().withMaxStackSize(64);
        this.itemCapability = LazyOptional.of(() -> {
            return new CombinedInvWrapper(new IItemHandlerModifiable[]{this.inputInventory, this.outputInventory});
        });
        this.contentsChanged = true;
        this.disabledSpoutputs = new ArrayList();
        this.preferredSpoutput = null;
        this.spoutputBuffer = new ArrayList();
        this.recipeBackupCheck = 20;
    }

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

    public boolean isEmpty() {
        return this.inputInventory.isEmpty() && this.outputInventory.isEmpty();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(this));
    }

    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        this.inputInventory.deserializeNBT(compound.getCompound("InputItems"));
        this.outputInventory.deserializeNBT(compound.getCompound("OutputItems"));
        this.preferredSpoutput = null;
        if (compound.contains("PreferredSpoutput")) {
            this.preferredSpoutput = (Direction)NBTHelper.readEnum(compound, "PreferredSpoutput", Direction.class);
        }

        this.disabledSpoutputs.clear();
        ListTag disabledList = compound.getList("DisabledSpoutput", 8);
        disabledList.forEach((d) -> {
            this.disabledSpoutputs.add(Direction.valueOf(((StringTag)d).getAsString()));
        });
        this.spoutputBuffer = NBTHelper.readItemList(compound.getList("Overflow", 10));
    }

    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.put("InputItems", this.inputInventory.serializeNBT());
        compound.put("OutputItems", this.outputInventory.serializeNBT());
        if (this.preferredSpoutput != null) {
            NBTHelper.writeEnum(compound, "PreferredSpoutput", this.preferredSpoutput);
        }

        ListTag disabledList = new ListTag();
        this.disabledSpoutputs.forEach((d) -> {
            disabledList.add(StringTag.valueOf(d.name()));
        });
        compound.put("DisabledSpoutput", disabledList);
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
    }

    private Optional<MechanicalPressBlockEntity> getOperator() {
        if (this.level == null) {
            return Optional.empty();
        } else {
            BlockEntity be = this.level.getBlockEntity(this.worldPosition.above(2));
            return be instanceof MechanicalPressBlockEntity ? Optional.of((MechanicalPressBlockEntity)be) : Optional.empty();
        }
    }

    public void onWrenched(Direction face) {
        BlockState blockState = this.getBlockState();
        Direction currentFacing = (Direction) blockState.getValue(SqueezeBasinBlock.FACING);
        this.disabledSpoutputs.remove(face);
        if (currentFacing == face) {
            if (this.preferredSpoutput == face) {
                this.preferredSpoutput = null;
            }

            this.disabledSpoutputs.add(face);
        } else {
            this.preferredSpoutput = face;
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
