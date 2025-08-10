package org.forsteri.ratatouille.content.oven_fan;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.model.Models;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.forsteri.ratatouille.entry.CRPartialModels;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;
public class OvenFanVisual extends KineticBlockEntityVisual<OvenFanBlockEntity> {
    protected final RotatingInstance cogWheel;
    protected final RotatingInstance fan;
    final Direction direction;
    private final Direction opposite;
    public OvenFanVisual(VisualizationContext context, OvenFanBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
        this.direction = (Direction)this.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        this.opposite = this.direction.getOpposite();

        this.cogWheel = (RotatingInstance)instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFTLESS_COGWHEEL))
                .createInstance();
        this.fan  = (RotatingInstance)instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(CRPartialModels.OVEN_FAN_BLADE))//CRPartialModels.OVEN_FAN_BLADE
                .createInstance();
        this.cogWheel.setup(blockEntity)
                .setPosition(getVisualPosition())
                .rotateToFace(opposite)
                .setChanged();;
        this.fan.setup(blockEntity, this.getFanSpeed())
                .setPosition(getVisualPosition())
                .rotateToFace(Direction.SOUTH, opposite)
                .setChanged();
    }

    private float getFanSpeed() {
        float speed = ((OvenFanBlockEntity)this.blockEntity).getSpeed() * 2.0F;
        if (speed > 0.0F) {
            speed = Mth.clamp(speed, 80.0F, 1280.0F);
        }

        if (speed < 0.0F) {
            speed = Mth.clamp(speed, -1280.0F, -80.0F);
        }

        return speed;
    }

    @Override
    public void update(float pt) {
        cogWheel.setup(blockEntity)
                .setChanged();
        fan.setup(blockEntity, getFanSpeed())
                .setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        BlockPos behind = pos.relative(opposite);
        relight(behind, cogWheel);

        BlockPos inFront = pos.relative(direction);
        relight(inFront, fan);
    }

    @Override
    protected void _delete() {
        cogWheel.delete();
        fan.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept(cogWheel);
        consumer.accept(fan);
    }

//    private Supplier<PoseStack> rotateToFace(Direction facing) {
//        return () -> {
//            PoseStack stack = new PoseStack();
//            TransformStack stacker = (TransformStack)TransformStack.cast(stack).centre();
//            if (facing.getAxis() == Direction.Axis.X) {
//                stacker.rotateZ(90.0);
//            } else if (facing.getAxis() == Direction.Axis.Z) {
//                stacker.rotateX(90.0);
//            }
//
//            stacker.unCentre();
//            return stack;
//        };
//    }
}
