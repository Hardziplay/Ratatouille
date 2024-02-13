package org.forsteri.ratatouille.compat.jei.category.animations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import org.forsteri.ratatouille.entry.CRBlocks;
import org.forsteri.ratatouille.entry.CRPartialModels;

public class AnimatedThresher extends AnimatedKinetics {
    @Override
    public void draw(PoseStack matrixStack, int xOffset, int yOffset) {
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, 0);
        AllGuiTextures.JEI_SHADOW.render(matrixStack, -16, 13);
        matrixStack.translate(-2, 18, 0);
        int scale = 22;

        blockElement(CRPartialModels.THRESHER_BLADE)
                .rotateBlock(22.5, 112.5, 0)
                .scale(scale)
                .render(matrixStack);

        blockElement(CRBlocks.THRESHER.getDefaultState())
                .rotateBlock(22.5, 22.5, 0)
                .scale(scale)
                .render(matrixStack);

        matrixStack.popPose();
    }
}
