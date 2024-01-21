package org.forsteri.ratatouille.content.thresher;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import com.jozufozu.flywheel.backend.Backend;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class ThresherRenderer extends SafeBlockEntityRenderer<ThresherBlockEntity> {
    public ThresherRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(ThresherBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        renderItems(be, partialTicks, ms, buffer, light, overlay);
    }

    protected void renderItems(ThresherBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                               int light, int overlay) {
        for (int slot = 0; slot < be.outputInv.getSlots(); slot++) {
            ItemStack stack = be.outputInv.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
                BakedModel modelWithOverrides = itemRenderer.getModel(stack, be.getLevel(), null, 0);
                boolean blockItem = modelWithOverrides.isGui3d();
            }
        }
    }
}
