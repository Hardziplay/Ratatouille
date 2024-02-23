package org.forsteri.ratatouille.content.squeeze_basin;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.oriented.OrientedData;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlock;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.forsteri.ratatouille.entry.CRPartialModels;

public class SqueezeBasinInstance extends BlockEntityInstance<SqueezeBasinBlockEntity> implements DynamicInstance {
    private final OrientedData cover;
    public SqueezeBasinInstance(MaterialManager materialManager, SqueezeBasinBlockEntity blockEntity) {
        super(materialManager, blockEntity);
        cover = materialManager.defaultSolid()
                .material(Materials.ORIENTED)
                .getModel(CRPartialModels.SQUEEZE_BASIN_COVER, blockState)
                .createInstance();

        Quaternion q = Vector3f.YP
                .rotationDegrees(AngleHelper.horizontalAngle(blockState.getValue(MechanicalPressBlock.HORIZONTAL_FACING)));

        cover.setRotation(q);

        transformModels();
    }

    @Override
    public void beginFrame() {
        transformModels();
    }

    private void transformModels() {
        float renderedHeadOffset = getRenderedHeadOffset(blockEntity);
        cover.setPosition(getInstancePosition())
                .nudge(0, -renderedHeadOffset, 0);
    }

    private float getRenderedHeadOffset(SqueezeBasinBlockEntity be) {
        if (be.getOperator().isEmpty()) {
            return 0F;
        } else {
            PressingBehaviour pressingBehaviour = be.getOperator().get().getPressingBehaviour();
            float offset = pressingBehaviour.getRenderedHeadOffset(AnimationTickHolder.getPartialTicks())
                    * pressingBehaviour.mode.headOffset - 1;
            return Math.max(0, offset);
        }
    }

    @Override
    public void updateLight() {
        super.updateLight();
        relight(pos, cover);
    }

    @Override
    protected void remove() {
        cover.delete();
    }
}
