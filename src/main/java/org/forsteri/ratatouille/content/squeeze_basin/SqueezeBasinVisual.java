package org.forsteri.ratatouille.content.squeeze_basin;

import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlock;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.BlockEntityVisual;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import org.forsteri.ratatouille.content.demolder.MechanicalDemolderBlockEntity;
import org.forsteri.ratatouille.entry.CRPartialModels;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.function.Consumer;

public class SqueezeBasinVisual extends AbstractBlockEntityVisual<SqueezeBasinBlockEntity> implements SimpleDynamicVisual {
    private final OrientedInstance cover;
    public SqueezeBasinVisual(VisualizationContext context, SqueezeBasinBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);


        cover = instancerProvider().instancer(InstanceTypes.ORIENTED,
                        Models.partial(CRPartialModels.SQUEEZE_BASIN_COVER))
                .createInstance();

//        cover = materialManager.defaultSolid()
//                .material(Materials.ORIENTED)
//                .getModel(CRPartialModels.SQUEEZE_BASIN_COVER, blockState)
//                .createInstance();

        Quaternionf q = Axis.YP
                .rotationDegrees(AngleHelper.horizontalAngle(blockState.getValue(MechanicalPressBlock.HORIZONTAL_FACING)));

        cover.rotation(q);

        transformModels();
    }

    @Override
    public void beginFrame(DynamicVisual.Context ctx) {
        transformModels();
    }

    private void transformModels() {
        float renderedHeadOffset = getRenderedHeadOffset(blockEntity);
        cover.position(getVisualPosition())
                .translatePosition(0, -renderedHeadOffset, 0).setChanged();
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
    public void updateLight(float partialTick) {
        relight(cover);
    }


    @Override
    protected void _delete() {
        cover.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept(cover);
    }
}
