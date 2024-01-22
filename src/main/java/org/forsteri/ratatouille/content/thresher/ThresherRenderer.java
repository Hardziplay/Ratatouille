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
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
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
        if (be.lastRecipe != null) {
            for (int slot = 0; slot < be.inputInv.getSlots(); slot++) {
                ItemStack stack = be.inputInv.getStackInSlot(slot);
                int duration = be.lastRecipe.getProcessingDuration();
                int timer = Mth.clamp(be.timer, 0, duration);
                float processPercent = (float) (duration - timer) / duration * 0.5F;
                if (!stack.isEmpty()) {
                    ms.pushPose();
                    ItemStack resultStack = be.lastRecipe.getResultItem();
                    Direction ejectDirection = be.getEjectDirection();
                    ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
                    ms.scale(0.5F, 0.5F, 0.5F);
                    float deltaY = 0.5F - processPercent * 0.6F;
                    float deltaM = -processPercent * 1.5F + 0.5F;
                    switch (ejectDirection) {
                        case NORTH -> {
                            ms.translate(1, deltaY, deltaM);
                            ms.mulPose(Vector3f.XP.rotationDegrees(66));
                        }
                        case SOUTH -> {
                            ms.translate(1, deltaY, 2 - deltaM);
                            ms.mulPose(Vector3f.XN.rotationDegrees(66));
                        }
                        case WEST -> {
                            ms.translate(deltaM, deltaY,1);
                            ms.mulPose(Vector3f.YP.rotationDegrees(90));
                            ms.mulPose(Vector3f.XP.rotationDegrees(66));
                        }
                        case EAST -> {
                            ms.translate(2 - deltaM, deltaY,1);
                            ms.mulPose(Vector3f.YP.rotationDegrees(-90));
                            ms.mulPose(Vector3f.XP.rotationDegrees(66));
                        }
                    }
                    itemRenderer.renderStatic(resultStack, ItemTransforms.TransformType.FIXED, light, overlay, ms, buffer, 0);
                    ms.popPose();
                    break;
                }
            }
        }
    }
}
