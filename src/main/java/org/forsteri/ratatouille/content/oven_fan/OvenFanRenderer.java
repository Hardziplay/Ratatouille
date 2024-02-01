package org.forsteri.ratatouille.content.oven_fan;

import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.forsteri.ratatouille.entry.CRPartialModels;

public class OvenFanRenderer extends KineticBlockEntityRenderer<OvenFanBlockEntity> {
    public OvenFanRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    protected SuperByteBuffer getRotatedModel(OvenFanBlockEntity be, BlockState state) {
        return CachedBufferer.partialFacingVertical(AllPartialModels.SHAFTLESS_COGWHEEL, state, (Direction)state.getValue(OvenFanBlock.HORIZONTAL_FACING));
    }

    protected void renderSafe(OvenFanBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (!Backend.canUseInstancing(be.getLevel())) {
            Direction direction = (Direction)be.getBlockState().getValue(BlockStateProperties.FACING);
            VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());
            int lightBehind = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().relative(direction.getOpposite()));
            int lightInFront = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().relative(direction));
            SuperByteBuffer cogWheel = CachedBufferer.partialFacing(AllPartialModels.SHAFTLESS_COGWHEEL, be.getBlockState(), direction.getOpposite());
            SuperByteBuffer fanInner = CachedBufferer.partialFacing(CRPartialModels.OVEN_FAN_BLADE, be.getBlockState(), direction.getOpposite());
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
            standardKineticRotationTransform(cogWheel, be, lightBehind).renderInto(ms, vb);
            kineticRotationTransform(fanInner, be, direction.getAxis(), angle, lightInFront).renderInto(ms, vb);
        }
    }
}
