package org.forsteri.ratatouille.content.oven;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.logistics.depot.DepotRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

import java.util.Random;

public class OvenRenderer extends SafeBlockEntityRenderer<OvenBlockEntity> {
    public OvenRenderer(BlockEntityRendererProvider.Context context) {}
    @Override
    protected void renderSafe(OvenBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        ms.pushPose();
        assert be.getLevel() != null;

        boolean isTall = be.getControllerBE() != null && be.getControllerBE().getHeight() <= 1;

        ms.translate(0.5, isTall ? 3/16f : 10/16f, 0.5);
        float scale = 1.25f;
        ms.scale(scale, scale, scale);


        DepotRenderer.renderItem(ms, bufferSource, light, overlay, be.inventory.getStackInSlot(0), 0, new Random(), VecHelper.getCenterOf(be.getBlockPos()));
        ms.popPose();
    }
}
