package org.forsteri.ratatouille.content.oven_fan;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.forsteri.ratatouille.entry.CRPartialModels;

import java.util.function.Supplier;

public class OvenFanRenderer extends KineticBlockEntityRenderer<OvenFanBlockEntity> {
    public OvenFanRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    protected void renderSafe(OvenFanBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        if (!VisualizationManager.supportsVisualization(be.getLevel())) {
            Direction direction = (Direction)be.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
            VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());
            int lightBehind = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().relative(direction.getOpposite()));
            int lightInFront = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().relative(direction));
            SuperByteBuffer fanInner = CachedBuffers.partialFacing(CRPartialModels.OVEN_FAN_BLADE, be.getBlockState(), direction.getOpposite());
            float time = AnimationTickHolder.getRenderTime(be.getLevel());
            float speed = be.getSpeed() * 5.0F;
            if (speed > 0.0F) {
                speed = Mth.clamp(speed, 80.0F, 1280.0F);
            }

            if (speed < 0.0F) {
                speed = Mth.clamp(speed, -1280.0F, -80.0F);
            }

            float angle = time * speed * 3.0F / 10.0F % 360.0F;
            angle = angle / 180.0F * 3.1415927F;
            kineticRotationTransform(fanInner, be, direction.getAxis(), angle, lightInFront).renderInto(ms, vb);
        }
    }

//    @Override
//    protected SuperByteBuffer getRotatedModel(OvenFanBlockEntity be, BlockState state) {
//        return CachedBufferer.partialFacingVertical(AllPartialModels.SHAFTLESS_COGWHEEL, state,
//                state.getValue(OvenFanBlock.HORIZONTAL_FACING));
//    }
}
