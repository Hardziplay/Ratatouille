package org.forsteri.ratatouille.content.oven_fan;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.core.materials.FlatLit;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.foundation.render.AllMaterialSpecs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.forsteri.ratatouille.entry.Registrate;

import java.util.function.Supplier;

public class OvenFanInstance extends KineticBlockEntityInstance<OvenFanBlockEntity> {
    protected final RotatingData cogWheel;
    protected final RotatingData fan;
    final Direction direction;
    private final Direction opposite;
    public OvenFanInstance(MaterialManager materialManager, OvenFanBlockEntity blockEntity) {
        super(materialManager, blockEntity);
        this.direction = (Direction)this.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        this.opposite = this.direction.getOpposite();
        this.cogWheel = (RotatingData)this.getRotatingMaterial().getModel(AllPartialModels.SHAFTLESS_COGWHEEL, this.blockState, this.opposite, this.rotateToFace(this.opposite)).createInstance();
        this.fan = (RotatingData)materialManager.defaultCutout().material(AllMaterialSpecs.ROTATING).getModel(Registrate.OVEN_FAN_BLADE, this.blockState, this.opposite).createInstance();
        this.setup(this.cogWheel);
        this.setup(this.fan, this.getFanSpeed());
    }

    private float getFanSpeed() {
        float speed = ((OvenFanBlockEntity)this.blockEntity).getSpeed() * 5.0F;
        if (speed > 0.0F) {
            speed = Mth.clamp(speed, 80.0F, 1280.0F);
        }

        if (speed < 0.0F) {
            speed = Mth.clamp(speed, -1280.0F, -80.0F);
        }

        return speed;
    }

    public void update() {
        this.updateRotation(this.cogWheel);
        this.updateRotation(this.fan, this.getFanSpeed());
    }

    public void updateLight() {
        BlockPos inFront = this.pos.relative(this.direction);
        this.relight(inFront, new FlatLit[]{this.fan});
        this.relight(inFront, new FlatLit[]{this.cogWheel});
    }

    public void remove() {
        this.cogWheel.delete();
        this.fan.delete();
    }

    private Supplier<PoseStack> rotateToFace(Direction facing) {
        return () -> {
            PoseStack stack = new PoseStack();
            TransformStack stacker = (TransformStack)TransformStack.cast(stack).centre();
            if (facing.getAxis() == Direction.Axis.X) {
                stacker.rotateZ(90.0);
            } else if (facing.getAxis() == Direction.Axis.Z) {
                stacker.rotateX(90.0);
            }

            stacker.unCentre();
            return stack;
        };
    }
}
