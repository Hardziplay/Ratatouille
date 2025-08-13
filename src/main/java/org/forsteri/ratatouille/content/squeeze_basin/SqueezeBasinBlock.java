package org.forsteri.ratatouille.content.squeeze_basin;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.fluid.FluidHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.forsteri.ratatouille.entry.CRBlockEntityTypes;
import org.forsteri.ratatouille.entry.CRItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public class SqueezeBasinBlock extends HorizontalDirectionalBlock implements IBE<SqueezeBasinBlockEntity>, IWrenchable {
    public static final BooleanProperty CASING = BooleanProperty.create("casing");
    public static final MapCodec<SqueezeBasinBlock> CODEC = simpleCodec(SqueezeBasinBlock::new);

    public SqueezeBasinBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState((BlockState) this.defaultBlockState().setValue(CASING, false));
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, worldIn, pos, newState);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return this.onBlockEntityUseItemOn(level, pos, (be) -> {
            if (!stack.isEmpty()) {
                if (stack.is(AllBlocks.MECHANICAL_PRESS.get().asItem())) {
                    BlockPos presserPos = pos.above(2);
                    if (level.getBlockState(presserPos).getBlock() instanceof AirBlock) {
                        level.setBlockAndUpdate(presserPos, AllBlocks.MECHANICAL_PRESS.get().getStateForPlacement(new BlockPlaceContext(new UseOnContext(player, hand, hitResult.withPosition(presserPos)))));
                        if (!player.isCreative())
                            stack.shrink(1);
                        return ItemInteractionResult.SUCCESS;
                    } else {
                        return ItemInteractionResult.FAIL;
                    }
                } else if (stack.is(CRItems.SAUSAGE_CASING.get()) && !state.getValue(CASING)) {
                    stack.shrink(1);
                    level.setBlockAndUpdate(pos, state.setValue(CASING, true));
                    return ItemInteractionResult.SUCCESS;
                } else if (FluidHelper.tryEmptyItemIntoBE(level, player, hand, stack, be)) {
                    return ItemInteractionResult.SUCCESS;
                } else if (FluidHelper.tryFillItemFromBE(level, player, hand, stack, be)) {
                    return ItemInteractionResult.SUCCESS;
                } else if (GenericItemEmptying.canItemBeEmptied(level, stack)
                        || GenericItemFilling.canItemBeFilled(level, stack)) {
                    return ItemInteractionResult.SUCCESS;
                } else if (stack.getItem().equals(Items.SPONGE)) {
                    IFluidHandler fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
                    if (fluidHandler != null) {
                        FluidStack drained = fluidHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
                        if (!drained.isEmpty()) {
                            return ItemInteractionResult.SUCCESS;
                        }
                    }
                } else {
                    return ItemInteractionResult.SUCCESS;
                }
            } else {
                IItemHandlerModifiable inv = be.itemCapability;
                if (inv == null)
                    inv = new ItemStackHandler(1);
                boolean success = false;

                for (int slot = 0; slot < inv.getSlots(); ++slot) {
                    ItemStack stackInSlot = inv.getStackInSlot(slot);
                    if (!stackInSlot.isEmpty()) {
                        player.getInventory().placeItemBackInInventory(stackInSlot);
                        inv.setStackInSlot(slot, ItemStack.EMPTY);
                        success = true;
                    }
                }

                if (success) {
                    level.playSound((Player) null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, 1.0F + level.random.nextFloat());
                }

                be.onEmptied();
                return ItemInteractionResult.SUCCESS;
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        });
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, LevelReader world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos.above());
        return !(blockEntity instanceof MechanicalPressBlockEntity);
    }

    @Override
    public Class<SqueezeBasinBlockEntity> getBlockEntityClass() {
        return SqueezeBasinBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SqueezeBasinBlockEntity> getBlockEntityType() {
        return CRBlockEntityTypes.SQUEEZE_BASIN_ENTITY.get();
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferredFacing = preferredFacing = context.getHorizontalDirection().getOpposite();
        return (BlockState) this.defaultBlockState().setValue(FACING, context.getPlayer() != null && context.getPlayer().isShiftKeyDown() ? preferredFacing : preferredFacing.getOpposite()).setValue(CASING, false);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(new Property[]{CASING, FACING}));
    }
}
