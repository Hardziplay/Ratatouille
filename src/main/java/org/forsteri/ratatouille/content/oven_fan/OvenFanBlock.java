package org.forsteri.ratatouille.content.oven_fan;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.forsteri.ratatouille.entry.Registrate;

public class OvenFanBlock extends HorizontalKineticBlock implements ICogWheel, IWrenchable, IBE<OvenFanBlockEntity> {
    public OvenFanBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState blockState) {
        return ((Direction)blockState.getValue(HORIZONTAL_FACING)).getAxis();
    }

    @Override
    public Class<OvenFanBlockEntity> getBlockEntityClass() {
        return OvenFanBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends OvenFanBlockEntity> getBlockEntityType() {
        return Registrate.OVEN_FAN_ENTITY.get();
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        Direction direction = pState.getValue(HORIZONTAL_FACING);
        return switch (direction) {
            case WEST -> Shapes.create(0, 0, 0, 13/16F, 1, 1);
            case EAST -> Shapes.create(3/16F, 0, 0, 1, 1, 1);
            case NORTH -> Shapes.create(0, 0, 0, 1, 1, 13/16F);
            case SOUTH -> Shapes.create(0, 0, 3/16F, 1, 1, 1);
            case UP, DOWN -> Shapes.block();
        };
    }

    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, worldIn, pos, oldState, isMoving);
    }
}
