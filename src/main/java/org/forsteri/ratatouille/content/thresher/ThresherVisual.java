package org.forsteri.ratatouille.content.thresher;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.forsteri.ratatouille.content.spreader.SpreaderBlockEntity;
import org.forsteri.ratatouille.entry.CRPartialModels;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ThresherVisual extends KineticBlockEntityVisual<ThresherBlockEntity> {

    protected final RotatingInstance thresher;
    final Direction direction;

    public ThresherVisual(VisualizationContext context, ThresherBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
        BlockState referenceState = blockEntity.getBlockState();
        Direction facing = referenceState.getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise();
        this.direction = (Direction)this.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);

//        this.thresher = materialManager.defaultCutout()
//                .material(AllMaterialSpecs.ROTATING).getModel(CRPartialModels.THRESHER_BLADE, referenceState, direction, this.rotateToFace(direction)).createInstance();

        this.thresher  = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(CRPartialModels.THRESHER_BLADE))
                .createInstance();
        this.thresher.setup(blockEntity)
                .setPosition(getVisualPosition())
//                .rotateToFace(opposite)
                .setChanged();;

        this.thresher.setup(blockEntity, this.getSpeed())
                .setPosition(getVisualPosition())
                .setChanged();
    }

    private float getSpeed() {
        return ((ThresherBlockEntity)this.blockEntity).getSpeed();
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept(thresher);
    }

    @Override
    public void updateLight(float partialTick) {
        BlockPos inFront = pos.relative(direction);
        relight(inFront, thresher);
    }

    @Override
    protected void _delete() {
        thresher.delete();
    }

//    public void update() {
//        this.updateRotation(this.thresher, this.getSpeed());
//    }
//
//    public void updateLight() {
//        this.relight(this.pos, new FlatLit[]{this.thresher});
//    }
//
//    public void remove() {
//        this.thresher.delete();
//    }
//
//    private Supplier<PoseStack> rotateToFace(Direction facing) {
//        return () -> {
//            PoseStack stack = new PoseStack();
//            TransformStack stacker = (TransformStack)TransformStack.cast(stack).centre();
//            if (facing.getAxis() == Direction.Axis.X) {
//                stacker.rotateY(90.0);
//            } else if (facing.getAxis() == Direction.Axis.Z) {
//                stacker.rotateX(90.0);
//            }
//
//            stacker.unCentre();
//            return stack;
//        };
//    }

}
