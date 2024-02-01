package org.forsteri.ratatouille.content.thresher;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.content.kinetics.base.ShaftInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.foundation.render.AllMaterialSpecs;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.forsteri.ratatouille.entry.CRPartialModels;

public class ThresherInstance extends ShaftInstance<ThresherBlockEntity> {
    public ThresherInstance(MaterialManager materialManager, ThresherBlockEntity blockEntity) {
        super(materialManager, blockEntity);
    }

    @Override
    protected Instancer<RotatingData> getModel() {
        BlockState referenceState = blockEntity.getBlockState();
        Direction facing = referenceState.getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise();
        return materialManager.defaultCutout()
                .material(AllMaterialSpecs.ROTATING).getModel(CRPartialModels.THRESHER_BLADE, referenceState, facing);
    }


}
