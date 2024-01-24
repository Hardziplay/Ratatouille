package org.forsteri.ratatouille.content.oven_fan;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
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
}
