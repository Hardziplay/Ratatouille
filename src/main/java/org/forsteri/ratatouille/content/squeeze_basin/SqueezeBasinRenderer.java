package org.forsteri.ratatouille.content.squeeze_basin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import org.forsteri.ratatouille.entry.CRPartialModels;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class SqueezeBasinRenderer extends SafeBlockEntityRenderer<SqueezeBasinBlockEntity> {

    public SqueezeBasinRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(SqueezeBasinBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        if (VisualizationManager.supportsVisualization(be.getLevel()))
            return;

        float renderedHeadOffset = 0;
        BlockState blockState = be.getBlockState();
        if (be.getOperator().isPresent()) {
            PressingBehaviour pressingBehaviour = be.getOperator().get().getPressingBehaviour();
            float offset = pressingBehaviour.getRenderedHeadOffset(AnimationTickHolder.getPartialTicks())
                    * pressingBehaviour.mode.headOffset - 1;
            renderedHeadOffset = Math.max(0, offset);
        }
        SuperByteBuffer headRender = CachedBuffers.partialFacing(CRPartialModels.SQUEEZE_BASIN_COVER, blockState,
                blockState.getValue(HORIZONTAL_FACING));
        headRender.translate(0, -renderedHeadOffset, 0)
                .light(light)
                .renderInto(ms, bufferSource.getBuffer(RenderType.solid()));
    }
}
