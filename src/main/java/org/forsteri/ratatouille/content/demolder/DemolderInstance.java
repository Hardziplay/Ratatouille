package org.forsteri.ratatouille.content.demolder;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.oriented.OrientedData;
import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.base.ShaftInstance;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import org.forsteri.ratatouille.entry.CRPartialModels;
import org.joml.Quaternionf;

public class DemolderInstance  extends ShaftInstance<MechanicalDemolderBlockEntity> implements DynamicInstance {
    private final OrientedData head;

    public DemolderInstance(MaterialManager materialManager, MechanicalDemolderBlockEntity blockEntity) {
        super(materialManager, blockEntity);

        head = materialManager.defaultSolid()
                .material(Materials.ORIENTED)
                .getModel(CRPartialModels.MECHANICAL_DEMOLDER_HEAD, blockState)
                .createInstance();

        Quaternionf q = Axis.YP
                .rotationDegrees(AngleHelper.horizontalAngle(blockState.getValue(MechanicalDemolderBlock.HORIZONTAL_FACING)));

        head.setRotation(q);

        transformModels();
    }

    @Override
    public void beginFrame() {
        transformModels();
    }

    private void transformModels() {
        float renderedHeadOffset = getRenderedHeadOffset(blockEntity);

        head.setPosition(getInstancePosition())
                .nudge(0, -renderedHeadOffset, 0);
    }

    private float getRenderedHeadOffset(MechanicalDemolderBlockEntity demolder) {
        PressingBehaviour pressingBehaviour = demolder.getPressingBehaviour();
        return pressingBehaviour.getRenderedHeadOffset(AnimationTickHolder.getPartialTicks())
                * pressingBehaviour.mode.headOffset;
    }

    @Override
    public void updateLight() {
        super.updateLight();

        relight(pos, head);
    }

    @Override
    public void remove() {
        super.remove();
        head.delete();
    }
}
