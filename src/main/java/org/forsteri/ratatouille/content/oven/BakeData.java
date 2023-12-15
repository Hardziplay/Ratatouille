package org.forsteri.ratatouille.content.oven;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.BoilerHeaters;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlock;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class BakeData {
    public int fanLevel;
    public int sizeLevel;
    public int tempLevel;
    public boolean needsHeatLevelUpdate;

    public void tick(OvenBlockEntity oven) {
        if (needsHeatLevelUpdate && updateTemp(oven))
            oven.notifyUpdate();
    }

    public boolean evaluate(OvenBlockEntity oven) {
        assert oven.getLevel() != null;

        int fanLevelBefore = fanLevel;
        int sizeLevelBefore = sizeLevel;
        fanLevel = 0;

        for (int i = 0; i < oven.radius; i++) {
            for (int j = 0; j < oven.height; j++) {
                for (int k = 0; k < oven.radius; k++) {
                    for (Direction direction : Direction.values()) {
                        BlockState state = oven.getLevel().getBlockState(oven.getBlockPos().offset(i, j, k).relative(direction));
                        if (state.getBlock() instanceof EncasedFanBlock
                                && state.getValue(EncasedFanBlock.FACING) == direction.getOpposite()) {
                            fanLevel++;
                        }
                    }
                }
            }
        }
        fanLevel /= 2;
        sizeLevel = oven.radius * oven.height * oven.radius / 4;

        return fanLevelBefore != fanLevel || sizeLevelBefore != sizeLevel;
    }

    public boolean updateTemp(OvenBlockEntity controller) {
        assert controller.getLevel() != null;

        BlockPos controllerPos = controller.getBlockPos();
        Level level = controller.getLevel();
        needsHeatLevelUpdate = false;

        int prevTemp = tempLevel;
        tempLevel = 0;

        for (int xOffset = 0; xOffset < controller.radius; xOffset++) {
            for (int zOffset = 0; zOffset < controller.radius; zOffset++) {
                BlockPos pos = controllerPos.offset(xOffset, -1, zOffset);
                BlockState blockState = level.getBlockState(pos);
                float heat = BoilerHeaters.getActiveHeat(level, pos, blockState);
                if (heat > 0) {
                    tempLevel += heat;
                }
            }
        }

        return tempLevel != prevTemp;
    }

    public void clear() {
        fanLevel = 0;
        sizeLevel = 0;
        tempLevel = 0;
    }

    public void read(CompoundTag compound, boolean ignoredClientPacket) {
        fanLevel = compound.getInt("fanCount");
        sizeLevel = compound.getInt("sizeCount");
        tempLevel = compound.getInt("tempCount");
    }

    public void write(CompoundTag compound, boolean ignoredClientPacket) {
        compound.putInt("fanCount", fanLevel);
        compound.putInt("sizeCount", sizeLevel);
        compound.putInt("tempCount", tempLevel);
    }

    public boolean addToGoggleTooltip(List<Component> tooltip, boolean ignoredIsPlayerSneaking) {
        tooltip.add(Components.literal(IHaveGoggleInformation.spacing));
        tooltip.add(Component.nullToEmpty("Fan level: " + fanLevel));
        tooltip.add(Component.nullToEmpty("Size level: " + sizeLevel));
        tooltip.add(Component.nullToEmpty("Temp level: " + tempLevel));
        return true;
    }
}
