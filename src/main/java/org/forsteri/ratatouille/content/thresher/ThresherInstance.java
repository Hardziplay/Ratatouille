package org.forsteri.ratatouille.content.thresher;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.core.materials.FlatLit;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.base.ShaftInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.foundation.render.AllMaterialSpecs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.forsteri.ratatouille.content.oven_fan.OvenFanBlockEntity;
import org.forsteri.ratatouille.entry.CRPartialModels;

public class ThresherInstance extends KineticBlockEntityInstance<ThresherBlockEntity> {

    protected final RotatingData thresher;
    final Direction direction;

    public ThresherInstance(MaterialManager materialManager, ThresherBlockEntity blockEntity) {
        super(materialManager, blockEntity);
        BlockState referenceState = blockEntity.getBlockState();
        Direction facing = referenceState.getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise();
        this.direction = (Direction)this.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        this.thresher = materialManager.defaultCutout()
                .material(AllMaterialSpecs.ROTATING).getModel(CRPartialModels.THRESHER_BLADE, referenceState, facing).createInstance();
        this.setup(this.thresher, this.getSpeed());
    }

    private float getSpeed() {
        return ((ThresherBlockEntity)this.blockEntity).getSpeed();
    }

    public void update() {
        this.updateRotation(this.thresher, this.getSpeed());
    }

    public void updateLight() {
        this.relight(this.pos, new FlatLit[]{this.thresher});
    }

    public void remove() {
        this.thresher.delete();
    }

}
