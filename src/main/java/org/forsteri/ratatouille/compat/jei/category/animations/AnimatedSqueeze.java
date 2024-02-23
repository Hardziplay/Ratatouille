package org.forsteri.ratatouille.compat.jei.category.animations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.core.Direction;
import org.forsteri.ratatouille.content.squeeze_basin.SqueezeBasinBlock;
import org.forsteri.ratatouille.entry.CRBlocks;
import org.forsteri.ratatouille.entry.CRPartialModels;

public class AnimatedSqueeze extends AnimatedKinetics {
    private boolean useCasing = false;

    public void setUseCasing(boolean useCasing) {
        this.useCasing = useCasing;
    }

    @Override
    public void draw(PoseStack matrixStack, int xOffset, int yOffset) {
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, 200);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(22.5f));
        int scale = 23;

        blockElement(shaft(Direction.Axis.Z))
                .rotateBlock(0, 0, getCurrentAngle())
                .scale(scale)
                .render(matrixStack);

        blockElement(AllBlocks.MECHANICAL_PRESS.getDefaultState())
                .scale(scale)
                .render(matrixStack);

        blockElement(AllPartialModels.MECHANICAL_PRESS_HEAD)
                .atLocal(0, -getAnimatedHeadOffset(), 0)
                .scale(scale)
                .render(matrixStack);

        blockElement(CRBlocks.SQUEEZE_BASIN.getDefaultState().setValue(SqueezeBasinBlock.CASING, useCasing))
                .atLocal(0, 1.65, 0)
                .scale(scale)
                .render(matrixStack);

        blockElement(CRPartialModels.SQUEEZE_BASIN_COVER)
                .atLocal(0, -getAnimatedCoverOffset(), 0)
                .scale(scale)
                .render(matrixStack);

        matrixStack.popPose();
    }

    private float getAnimatedHeadOffset() {
        float cycle = (AnimationTickHolder.getRenderTime() - offset * 8) % 30;
        if (cycle < 10) {
            float progress = cycle / 10;
            return -(progress * progress * progress);
        }
        if (cycle < 15)
            return -1;
        if (cycle < 20)
            return -1 + (1 - ((20 - cycle) / 5));
        return 0;
    }

    private float getAnimatedCoverOffset() {
        float offset = getAnimatedHeadOffset() - 1F;
        return Math.min(-1.65F, offset);
    }
}
