package org.forsteri.ratatouille.content.compost_tower;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.platform.ForgeCatnipServices;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraftforge.fluids.FluidStack;
import java.util.List;
import org.forsteri.ratatouille.content.compost_tower.MultiFluidTank;

public class CompostTowerRenderer extends SafeBlockEntityRenderer<CompostTowerBlockEntity> {
    public CompostTowerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(CompostTowerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        if (!be.isController())
            return;

        MultiFluidTank tank = (MultiFluidTank) be.getTankInventory();
        List<FluidStack> fluids = tank.getFluids();

        if (fluids.isEmpty())
            return;

        LerpedFloat fluidLevel = be.getFluidLevel();
        float fillRatio = (float) tank.getTotalAmount() / tank.getCapacity();
        float level = fluidLevel == null ? fillRatio : fluidLevel.getValue(partialTicks);


        float capHeight = 1 / 4f;
        float tankHullWidth =  1 / 128f;
        float minPuddleHeight = 1 / 16f;
        float totalHeight = be.getHeight() - 2 * capHeight - minPuddleHeight;

        if (level < 1 / (512f * totalHeight))
            return;
        float clampedLevel = Mth.clamp(level * totalHeight, 0, totalHeight);

        float xMin = tankHullWidth;
        float xMax = xMin + be.getWidth() - 2 * tankHullWidth;
        float zMin = tankHullWidth;
        float zMax = zMin + be.getWidth() - 2 * tankHullWidth;

        float yOffset = totalHeight + capHeight + minPuddleHeight;
        for (FluidStack fluidStack : fluids) {
            float part = clampedLevel * fluidStack.getAmount() / tank.getTotalAmount();
            if (part <= 0)
                continue;

            float yMin = yOffset - part;
            float yMax = yOffset;

            boolean top = fluidStack.getFluid().getFluidType().isLighterThanAir();
            if (top) {
                yMin += totalHeight - clampedLevel;
                yMax += totalHeight - clampedLevel;
            }

            ms.pushPose();
            ms.translate(0, part - totalHeight, 0);
            ForgeCatnipServices.FLUID_RENDERER.renderFluidBox(fluidStack, xMin, yMin, zMin, xMax, yMax, zMax, bufferSource, ms, light, false, true);
            ms.popPose();

            yOffset = yMin;
        }
    }
}
