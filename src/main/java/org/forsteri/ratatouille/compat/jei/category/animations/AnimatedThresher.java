package org.forsteri.ratatouille.compat.jei.category.animations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import org.forsteri.ratatouille.entry.CRBlocks;
import org.forsteri.ratatouille.entry.CRPartialModels;

public class AnimatedThresher extends AnimatedKinetics {
    @Override
    public void draw(PoseStack matrixStack, int xOffset, int yOffset) {
        int scale = 22;
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, 0);
        AllGuiTextures.JEI_SHADOW.render(matrixStack, -16, 13);

        matrixStack.pushPose();
        matrixStack.translate(-6, 15, 0);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-22.5F));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(22.5F));
        blockElement(CRPartialModels.THRESHER_BLADE)
                .rotateBlock(getCurrentAngle() * 2, 90, 180)
                .scale(scale)
                .render(matrixStack);
        matrixStack.popPose();

        matrixStack.translate(-2, 18, 0);
        blockElement(CRBlocks.THRESHER.getDefaultState())
                .rotateBlock(22.5, 22.5, 0)
                .scale(scale)
                .render(matrixStack);

        matrixStack.popPose();
    }
}
