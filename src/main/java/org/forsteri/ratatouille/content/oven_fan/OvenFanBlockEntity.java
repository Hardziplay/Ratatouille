package org.forsteri.ratatouille.content.oven_fan;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

public class OvenFanBlockEntity extends KineticBlockEntity implements IAirCurrentSource {
    public AirCurrent airCurrent = new AirCurrent(this);
    protected int airCurrentUpdateCooldown;
    protected int entitySearchCooldown;
    protected boolean updateAirFlow = true;

    public OvenFanBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        if (clientPacket) {
            this.airCurrent.rebuild();
        }

    }

    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
    }

    @Nullable
    @Override
    public AirCurrent getAirCurrent() {
        return this.airCurrent;
    }

    @Nullable
    @Override
    public Level getAirCurrentWorld() {
        return this.level;
    }

    @Override
    public BlockPos getAirCurrentPos() {
        return this.worldPosition;
    }

    @Override
    public Direction getAirflowOriginSide() {
        return (Direction)this.getBlockState().getValue(OvenFanBlock.HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public Direction getAirFlowDirection() {
        float speed = this.getSpeed();
        if (speed == 0.0F) {
            return null;
        } else {
            Direction facing = (Direction)this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
            speed = convertToDirection(speed, facing);
            return speed > 0.0F ? facing : facing.getOpposite();
        }
    }

    @Override
    public boolean isSourceRemoved() {
        return this.remove;
    }

    public void onSpeedChanged(float prevSpeed) {
        super.onSpeedChanged(prevSpeed);
        this.updateAirFlow = true;
    }

    public void blockInFrontChanged() {
        this.updateAirFlow = true;
    }

    public void tick() {
        super.tick();
        boolean server = !this.level.isClientSide || this.isVirtual();
        if (server && this.airCurrentUpdateCooldown-- <= 0) {
            this.airCurrentUpdateCooldown = (Integer) AllConfigs.server().kinetics.fanBlockCheckRate.get();
            this.updateAirFlow = true;
        }

        if (this.updateAirFlow) {
            this.updateAirFlow = false;
            this.airCurrent.rebuild();
            this.sendData();
        }

        if (this.getSpeed() != 0.0F) {
            if (this.entitySearchCooldown-- <= 0) {
                this.entitySearchCooldown = 5;
                this.airCurrent.findEntities();
            }

            this.airCurrent.tick();
        }
    }

    @Override
    public float calculateStressApplied() {
        return 2.0f;
    }
}
