package org.forsteri.ratatouille.compat.jei.category.animations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.minecraft.client.gui.GuiGraphics;
import org.forsteri.ratatouille.entry.CRBlocks;
import org.jetbrains.annotations.NotNull;

public class AnimatedCompostTower extends AnimatedKinetics {
    @Override
    public void draw(@NotNull GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, 200);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
        int scale = 23;
        blockElement(CRBlocks.COMPOST_TOWER_BLOCK.getDefaultState())
                .atLocal(0, 1.65, 0)
                .scale(scale)
                .render(graphics);
        matrixStack.popPose();
    }
}
