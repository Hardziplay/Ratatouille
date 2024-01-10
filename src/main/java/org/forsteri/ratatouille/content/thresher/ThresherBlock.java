package org.forsteri.ratatouille.content.thresher;

import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
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
}
