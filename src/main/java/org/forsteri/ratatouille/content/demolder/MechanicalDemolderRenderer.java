package org.forsteri.ratatouille.content.demolder;

import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.forsteri.ratatouille.entry.CRPartialModels;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class MechanicalDemolderRenderer extends KineticBlockEntityRenderer<MechanicalDemolderBlockEntity> {
    public MechanicalDemolderRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(MechanicalDemolderBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        BlockState blockState = be.getBlockState();
        PressingBehaviour pressingBehaviour = be.getPressingBehaviour();
        float renderedHeadOffset =
                pressingBehaviour.getRenderedHeadOffset(partialTicks) * pressingBehaviour.mode.headOffset;
        if (!Backend.canUseInstancing(be.getLevel())) {
            SuperByteBuffer headRender = CachedBufferer.partialFacing(CRPartialModels.MECHANICAL_DEMOLDER_HEAD, blockState,
                    blockState.getValue(HORIZONTAL_FACING));
            headRender.translate(0, -renderedHeadOffset, 0)
                    .light(light)
                    .renderInto(ms, buffer.getBuffer(RenderType.solid()));
        }

        ItemStack itemStack = be.outputInv.getStackInSlot(0);
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        boolean renderUpright = BeltHelper.isItemUpright(itemStack);
        int count = (int) (Mth.log2((int) (itemStack.getCount()))) / 2;

        ms.pushPose();
        ms.translate(0.5, -renderedHeadOffset + 1/ 16d, 0.5);
        for (int i = 0; i <= count; i++) {
            ms.pushPose();

            if (!renderUpright) {
                ms.translate(0, -.09375, 0);
                ms.mulPose(Vector3f.XP.rotationDegrees(90));
            }
            ms.scale(.5f, .5f, .5f);
            itemRenderer.renderStatic(null, itemStack, ItemTransforms.TransformType.FIXED, false, ms, buffer, be.getLevel(), light, overlay, 0);
            ms.popPose();

            if (!renderUpright) {
                ms.mulPose(Vector3f.YP.rotationDegrees(10));
                ms.translate(0, - 1 / 16d, 0);
            } else {
                ms.translate(0, 0, -1 / 16f);
            }
        }
        ms.popPose();
    }

    @Override
    protected BlockState getRenderedBlockState(MechanicalDemolderBlockEntity be) {
        return shaft(getRotationAxisOf(be));
    }
}
