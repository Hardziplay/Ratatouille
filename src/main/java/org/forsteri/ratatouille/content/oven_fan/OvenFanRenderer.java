package org.forsteri.ratatouille.content.oven_fan;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.trains.display.FlapDisplayBlock;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class OvenFanRenderer extends KineticBlockEntityRenderer<OvenFanBlockEntity> {
    public OvenFanRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    protected SuperByteBuffer getRotatedModel(OvenFanBlockEntity be, BlockState state) {
        System.out.println("YES");
        return CachedBufferer.partialFacingVertical(AllPartialModels.SHAFTLESS_COGWHEEL, state, (Direction)state.getValue(FlapDisplayBlock.HORIZONTAL_FACING));
    }
}
