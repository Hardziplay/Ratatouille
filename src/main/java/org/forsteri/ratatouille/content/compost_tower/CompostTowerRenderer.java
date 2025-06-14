package org.forsteri.ratatouille.content.compost_tower;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.platform.ForgeCatnipServices;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class CompostTowerRenderer extends SafeBlockEntityRenderer<CompostTowerBlockEntity> {
    public CompostTowerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(CompostTowerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        if (!be.isController())
            return;

        LerpedFloat fluidLevel = be.getFluidLevel();
        if (fluidLevel == null)
            return;

        float capHeight = 1 / 4f;
        float tankHullWidth = 1 / 16f + 1 / 128f;
        float minPuddleHeight = 1 / 16f;
        float totalHeight = be.getHeight() - 2 * capHeight - minPuddleHeight;

        float level = fluidLevel.getValue(partialTicks);
        if (level < 1 / (512f * totalHeight))
            return;
        float clampedLevel = Mth.clamp(level * 8 * totalHeight, 0, totalHeight);

        FluidTank tank = (FluidTank) be.getTankInventory();
        FluidStack fluidStack = tank.getFluid();

        if (fluidStack.isEmpty())
            return;

        boolean top = fluidStack.getFluid()
                .getFluidType()
                .isLighterThanAir();

        float xMin = tankHullWidth + 2/16F;
        float xMax = xMin + be.getWidth() - 2 * tankHullWidth - 4/16F;
        float yMin = totalHeight + capHeight + minPuddleHeight - clampedLevel + 3/16F;
        float yMax = yMin + clampedLevel  + 6/16F;

        if (top) {
            yMin += totalHeight - clampedLevel;
            yMax += totalHeight - clampedLevel;
        }

        float zMin = tankHullWidth + 2/16F;
        float zMax = zMin + be.getWidth() - 2 * tankHullWidth - 4/16F;

        ms.pushPose();
        ms.translate(0, clampedLevel - totalHeight, 0);
        ForgeCatnipServices.FLUID_RENDERER.renderFluidBox(fluidStack, xMin, yMin, zMin, xMax, yMax, zMax, bufferSource, ms, light, false, true);
        ms.popPose();
    }
}