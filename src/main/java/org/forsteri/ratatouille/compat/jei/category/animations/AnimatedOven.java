package org.forsteri.ratatouille.compat.jei.category.animations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import org.forsteri.ratatouille.entry.CRBlocks;
import org.jetbrains.annotations.NotNull;

public class AnimatedOven extends AnimatedKinetics {
    @Override
    public void draw(@NotNull PoseStack matrixStack, int xOffset, int yOffset) {
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, 200);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(22.5f));
        int scale = 23;
        blockElement(CRBlocks.OVEN.getDefaultState())
                .atLocal(0, 1.65, 0)
                .scale(scale)
                .render(matrixStack);
        matrixStack.popPose();
    }
}
