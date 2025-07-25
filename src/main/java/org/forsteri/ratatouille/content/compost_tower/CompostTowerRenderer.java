package org.forsteri.ratatouille.content.compost_tower;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
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

        FluidTank tank = (FluidTank) be.getTankInventory();
        FluidStack fluidStack = tank.getFluid();

        if (fluidStack.isEmpty())
            return;

        LerpedFloat fluidLevel = be.getFluidLevel();
        float fillRatio = (float) fluidStack.getAmount() / tank.getCapacity();
        float level = fluidLevel == null ? fillRatio : fluidLevel.getValue(partialTicks);


        float capHeight = 1 / 4f;
        float tankHullWidth =  1 / 128f;
        float minPuddleHeight = 1 / 16f;
        float totalHeight = be.getHeight() - 2 * capHeight - minPuddleHeight;

        float currentHeight = 0;

//        for (SmartFluidTank inner : tanks) {
//            FluidStack fluidStack = inner.getFluid();
//            if (fluidStack.isEmpty())
//                continue;

//            float fillRatio = (float) fluidStack.getAmount() / inner.getCapacity();
//            float level = fillRatio;

//            if (level < 1 / (512f * totalHeight))
//                continue;
            float clampedLevel = Mth.clamp(level * totalHeight, 0, totalHeight - currentHeight);
//        }
//        boolean top = fluidStack.getFluid()
//                .getFluidType()
//                .isLighterThanAir();
//
//        float xMin = tankHullWidth;
//        float xMax = xMin + be.getWidth() - 2 * tankHullWidth;
//        float yMin = totalHeight + capHeight + minPuddleHeight - clampedLevel;
//        float yMax = yMin + clampedLevel;
//
//        if (top) {
//            yMin += totalHeight - clampedLevel;
//            yMax += totalHeight - clampedLevel;
//        }
//
//        float zMin = tankHullWidth;
//        float zMax = zMin + be.getWidth() - 2 * tankHullWidth;
//
//        ms.pushPose();
//        ms.translate(0, clampedLevel - totalHeight, 0);
//        ForgeCatnipServices.FLUID_RENDERER.renderFluidBox(fluidStack, xMin, yMin, zMin, xMax, yMax, zMax, bufferSource, ms, light, false, true);
//        ms.popPose();
    }
}