package org.forsteri.ratatouille.content.compost_tower;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.platform.ForgeCatnipServices;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.forsteri.ratatouille.entry.CRFluids;

import java.util.ArrayList;
import java.util.List;

public class CompostTowerRenderer extends SafeBlockEntityRenderer<CompostTowerBlockEntity> {
    public CompostTowerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(CompostTowerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        if (!be.isController())
            return;

        float capHeight = 1 / 5f;
        float tankHullWidth = 1 / 16f + 1 / 128f;
        float minPuddleHeight = 1 / 16f;
        float totalHeight = be.height - 2 * capHeight - minPuddleHeight;

        var sortedFluids = be.getSortedFluids();
        float accumulatedFluidHeight = 0;
        float accumulatedGasHeight = 0;
        for (int i = 0; i < sortedFluids.size(); i++) {
            var fluid = sortedFluids.get(i);
            if (fluid.isEmpty()) continue;

            boolean isGas = fluid.getFluid().getFluidType().isLighterThanAir();
            LerpedFloat levelValue = (isGas ? be.gasLevels : be.fluidLevels).get(fluid.getFluid());
            if (levelValue == null) continue;

            float ratio = levelValue.getValue(partialTicks);
            if (ratio < 1 / (512f * totalHeight)) continue;

            float fluidHeight = ratio * totalHeight;
            float yStart;
            float yEnd;

            if (isGas) {
                yStart = totalHeight - accumulatedGasHeight - fluidHeight + capHeight + minPuddleHeight;
                yEnd   = totalHeight - accumulatedGasHeight + capHeight + minPuddleHeight;
                accumulatedGasHeight += fluidHeight;
            } else {
                yStart = accumulatedFluidHeight + capHeight + minPuddleHeight;
                yEnd   = accumulatedFluidHeight + fluidHeight + capHeight + minPuddleHeight;
                accumulatedFluidHeight += fluidHeight;
            }

            float xMin = tankHullWidth;
            float xMax = xMin + be.radius - 2 * tankHullWidth;
            float zMin = tankHullWidth;
            float zMax = zMin + be.radius - 2 * tankHullWidth;

            ms.pushPose();
            ForgeCatnipServices.FLUID_RENDERER.renderFluidBox(
                    fluid, xMin, yStart, zMin, xMax, yEnd, zMax,
                    bufferSource, ms, light, false, true
            );
            ms.popPose();

//            if (top) {
//                accumulatedGasHeight += initialLevel;
//            } else {
//                accumulatedFluidHeight += initialLevel;
//            }
        }
    }

    @Override
    public boolean shouldRenderOffScreen(CompostTowerBlockEntity be) {
        return be.isController();
    }
}