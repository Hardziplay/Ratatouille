package org.forsteri.ratatouille.content.spreader;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.logistics.chute.AbstractChuteBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.utility.worldWrappers.WrappedWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.forsteri.ratatouille.entry.CRBlockEntityTypes;
import org.forsteri.ratatouille.entry.CRItems;
import org.jetbrains.annotations.NotNull;

public class SpreaderBlock extends DirectionalKineticBlock implements IBE<SpreaderBlockEntity> {
    public SpreaderBlock(Properties properties) {
        super(properties);
    }


    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, worldIn, pos, oldState, isMoving);
        blockUpdate(state, worldIn, pos);
    }

//    @Override
//    public @NotNull InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
//        ItemStack heldItem = pPlayer.getItemInHand(pHand);
//        return this.onBlockEntityUse(pLevel, pPos, (be) -> {
//            if (!heldItem.isEmpty()) {
//                if (GenericItemEmptying.canItemBeEmptied(pLevel, heldItem)
//                        || GenericItemFilling.canItemBeFilled(pLevel, heldItem))
//                    return InteractionResult.SUCCESS;
//                return InteractionResult.PASS;
//            } else {
//                IItemHandlerModifiable inv = (IItemHandlerModifiable)be.capability.orElse(new ItemStackHandler(1));
//                boolean success = false;
//
//                for(int slot = 0; slot < inv.getSlots(); ++slot) {
//                    ItemStack stackInSlot = inv.getStackInSlot(slot);
//                    if (!stackInSlot.isEmpty()) {
//                        pPlayer.getInventory().placeItemBackInInventory(stackInSlot);
//                        inv.setStackInSlot(slot, ItemStack.EMPTY);
//                        success = true;
//                    }
//                }
//
//                if (success) {
//                    pLevel.playSound((Player)null, pPos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, 1.0F + Create.RANDOM.nextFloat());
//                }
//                return InteractionResult.SUCCESS;
//            }
//        });
//    }

    @Override
    public void updateIndirectNeighbourShapes(BlockState stateIn, LevelAccessor worldIn, BlockPos pos, int flags, int count) {
        super.updateIndirectNeighbourShapes(stateIn, worldIn, pos, flags, count);
        blockUpdate(stateIn, worldIn, pos);
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
                                boolean isMoving) {
        blockUpdate(state, worldIn, pos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction face = context.getClickedFace();

        BlockState placedOn = world.getBlockState(pos.relative(face.getOpposite()));
        BlockState placedOnOpposite = world.getBlockState(pos.relative(face));
        if (AbstractChuteBlock.isChute(placedOn))
            return defaultBlockState().setValue(FACING, face.getOpposite());
        if (AbstractChuteBlock.isChute(placedOnOpposite))
            return defaultBlockState().setValue(FACING, face);

        Direction preferredFacing = getPreferredFacing(context);
        if (preferredFacing == null)
            preferredFacing = context.getNearestLookingDirection();
        return defaultBlockState().setValue(FACING, context.getPlayer() != null && context.getPlayer()
                .isShiftKeyDown() ? preferredFacing : preferredFacing.getOpposite());
    }

    protected void blockUpdate(BlockState state, LevelAccessor worldIn, BlockPos pos) {
        if (worldIn instanceof WrappedWorld)
            return;
        notifyFanBlockEntity(worldIn, pos);
    }

    protected void notifyFanBlockEntity(LevelAccessor world, BlockPos pos) {
        withBlockEntityDo(world, pos, SpreaderBlockEntity::blockInFrontChanged);
    }

    @Override
    public BlockState updateAfterWrenched(BlockState newState, UseOnContext context) {
        blockUpdate(newState, context.getLevel(), context.getClickedPos());
        return newState;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING)
                .getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING)
                .getOpposite();
    }

    @Override
    public Class<SpreaderBlockEntity> getBlockEntityClass() {
        return SpreaderBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SpreaderBlockEntity> getBlockEntityType() {
        return CRBlockEntityTypes.SPREADER_BLOCK_ENTITY.get();
    }
}
