package org.forsteri.ratatouille.content.squeeze_basin;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
import org.forsteri.ratatouille.entry.CRBlockEntityTypes;
import org.forsteri.ratatouille.entry.CRItems;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class SqueezeBasinBlock extends Block implements IBE<SqueezeBasinBlockEntity>, IWrenchable {
    public static final DirectionProperty FACING;
    public static final BooleanProperty CASING = BooleanProperty.create("casing");

    public SqueezeBasinBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue(CASING, false));
    }

    @Override
    public @NotNull InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack heldItem = pPlayer.getItemInHand(pHand);
        return this.onBlockEntityUse(pLevel, pPos, (be) -> {
            if (heldItem.is(CRItems.SAUSAGE_CASING.get()) && !pState.getValue(CASING)) {
                heldItem.shrink(1);
                pLevel.setBlockAndUpdate(pPos, pState.setValue(CASING, true));
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.FAIL;
            }
        });
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(new Property[]{CASING, FACING}));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos.above());
        return !(blockEntity instanceof MechanicalPressBlockEntity);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (!context.getLevel().isClientSide) {
            this.withBlockEntityDo(context.getLevel(), context.getClickedPos(), (bte) -> {
                bte.onWrenched(context.getClickedFace());
            });
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, worldIn, pos, newState);
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
        return (BlockState)this.defaultBlockState().setValue(FACING, context.getPlayer() != null && context.getPlayer().isShiftKeyDown() ? preferredFacing : preferredFacing.getOpposite()).setValue(CASING, false);
    }

    public static boolean canOutputTo(BlockGetter world, BlockPos basinPos, Direction direction) {
        BlockPos neighbour = basinPos.relative(direction);
        BlockPos output = neighbour.below();
        BlockState blockState = world.getBlockState(neighbour);
        if (FunnelBlock.isFunnel(blockState)) {
            if (FunnelBlock.getFunnelFacing(blockState) == direction) {
                return false;
            }
        } else {
            if (!blockState.getCollisionShape(world, neighbour).isEmpty()) {
                return false;
            }

            BlockEntity blockEntity = world.getBlockEntity(output);
            if (blockEntity instanceof BeltBlockEntity) {
                BeltBlockEntity belt = (BeltBlockEntity)blockEntity;
                return belt.getSpeed() == 0.0F || belt.getMovementFacing() != direction.getOpposite();
            }
        }

        DirectBeltInputBehaviour directBeltInputBehaviour = (DirectBeltInputBehaviour) BlockEntityBehaviour.get(world, output, DirectBeltInputBehaviour.TYPE);
        return directBeltInputBehaviour != null ? directBeltInputBehaviour.canInsertFromSide(direction) : false;
    }

    static {
        FACING = BlockStateProperties.FACING_HOPPER;
    }
}
