package org.forsteri.ratatouille.content.demolder;

import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.base.ShaftVisual;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.createmod.catnip.math.AngleHelper;
import org.forsteri.ratatouille.entry.CRPartialModels;
import org.joml.Quaternionf;

public class DemolderVisual extends ShaftVisual<MechanicalDemolderBlockEntity> implements SimpleDynamicVisual {
    private final OrientedInstance head;

    public DemolderVisual(VisualizationContext context, MechanicalDemolderBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);

        head = instancerProvider().instancer(InstanceTypes.ORIENTED,
                Models.partial(CRPartialModels.MECHANICAL_DEMOLDER_HEAD))
                .createInstance();

        Quaternionf q = Axis.YP
                .rotationDegrees(AngleHelper.horizontalAngle(blockState.getValue(MechanicalDemolderBlock.HORIZONTAL_FACING)));

        head.rotation(q);

        transformModels(partialTick);
    }

    @Override
    public void beginFrame(DynamicVisual.Context ctx) {
        transformModels(ctx.partialTick());
    }

    private void transformModels(float pt) {
        float renderedHeadOffset = getRenderedHeadOffset(pt);

        head.position(getVisualPosition())
                .translatePosition(0, -renderedHeadOffset, 0).setChanged();
    }

    private float getRenderedHeadOffset(float pt) {
        PressingBehaviour pressingBehaviour = blockEntity.getPressingBehaviour();
        return pressingBehaviour.getRenderedHeadOffset(pt)
                * pressingBehaviour.mode.headOffset;
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        relight(head);
    }


    @Override
    protected void _delete() {
        super._delete();
        head.delete();
    }

}
