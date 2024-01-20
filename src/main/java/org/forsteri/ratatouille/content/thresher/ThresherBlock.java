package org.forsteri.ratatouille.content.thresher;

import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.forsteri.ratatouille.entry.Registrate;

public class ThresherBlock extends HorizontalKineticBlock implements IBE<ThresherBlockEntity> {
    public ThresherBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(HORIZONTAL_FACING)
                .getClockWise()
                .getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return getRotationAxis(state) == face.getAxis();
    }

    @Override
    public Class<ThresherBlockEntity> getBlockEntityClass() {
        return ThresherBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ThresherBlockEntity> getBlockEntityType() {
        return Registrate.THRESHER_ENTITY.get();
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        boolean isZ = pState.getValue(HORIZONTAL_FACING).getAxis() == Direction.Axis.Z;
        return Shapes.or(
                Shapes.create(0, 0, 0, 1, 2/16f, 1),
                Shapes.create(isZ ? 0 : 1/16f, 2/16f, isZ ? 1/16f : 0, isZ ? 1 : 15/16f, 15/16f, isZ ? 15/16f: 1)
        );
    }
}
