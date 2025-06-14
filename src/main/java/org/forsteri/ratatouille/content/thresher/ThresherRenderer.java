package org.forsteri.ratatouille.content.thresher;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.forsteri.ratatouille.content.oven_fan.OvenFanBlock;
import org.forsteri.ratatouille.content.oven_fan.OvenFanBlockEntity;
import org.forsteri.ratatouille.entry.CRPartialModels;
import org.forsteri.ratatouille.entry.CRRecipeTypes;

import java.util.Optional;

public class ThresherRenderer extends KineticBlockEntityRenderer<ThresherBlockEntity> {
    public ThresherRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(ThresherBlockEntity be, BlockState state) {
        return CachedBuffers.partialFacingVertical(CRPartialModels.THRESHER_BLADE, state, state.getValue(ThresherBlock.HORIZONTAL_FACING));
    }

    @Override
    protected void renderSafe(ThresherBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        renderItems(be, partialTicks, ms, buffer, light, overlay);
    }

    protected void renderItems(ThresherBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                               int light, int overlay) {
        RecipeWrapper inventoryIn = new RecipeWrapper(be.inputInv);
        if (be.lastRecipe == null || !be.lastRecipe.matches(inventoryIn, be.getLevel())) {
            Optional<ThreshingRecipe> recipe = CRRecipeTypes.THRESHING.find(inventoryIn, be.getLevel());
            if (recipe.isEmpty()) {
                return;
            }

            be.lastRecipe =  recipe.get();
        }
        if (be.lastRecipe != null) {
            for (int slot = 0; slot < be.inputInv.getSlots(); slot++) {
                ItemStack stack = be.inputInv.getStackInSlot(slot);
                int duration = be.lastRecipe.getProcessingDuration();
                int timer = Mth.clamp(be.timer, 0, duration);
                float processPercent = (float) (duration - timer) / duration * 0.5F;
                if (!stack.isEmpty()) {
                    ms.pushPose();
                    ItemStack resultStack = be.lastRecipe.getResultItem(RegistryAccess.EMPTY);
                    Direction ejectDirection = be.getEjectDirection();
                    ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
                    ms.scale(0.5F, 0.5F, 0.5F);
                    float deltaY = 0.5F - processPercent * 0.6F;
                    float deltaM = -processPercent * 1.5F + 0.5F;
                    switch (ejectDirection) {
                        case NORTH -> {
                            ms.translate(1, deltaY, deltaM);
                            ms.mulPose(Axis.XP.rotationDegrees(66));
                        }
                        case SOUTH -> {
                            ms.translate(1, deltaY, 2 - deltaM);
                            ms.mulPose(Axis.XN.rotationDegrees(66));
                        }
                        case WEST -> {
                            ms.translate(deltaM, deltaY,1);
                            ms.mulPose(Axis.YP.rotationDegrees(90));
                            ms.mulPose(Axis.XP.rotationDegrees(66));
                        }
                        case EAST -> {
                            ms.translate(2 - deltaM, deltaY,1);
                            ms.mulPose(Axis.YP.rotationDegrees(-90));
                            ms.mulPose(Axis.XP.rotationDegrees(66));
                        }
                    }
                    itemRenderer.renderStatic(resultStack, ItemDisplayContext.FIXED, light, overlay, ms, buffer, be.getLevel(), 0);
                    ms.popPose();
                    break;
                }
            }
        }
    }
}
