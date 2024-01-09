package org.forsteri.ratatouille.content.oven;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.BoilerHeaters;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlock;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class BakeData {
    public int fanLevel;
    public int sizeLevel;
    public int tempLevel;
    public int updateRequired;

    public void tick(OvenBlockEntity oven) {
        if (updateOven(oven))
            oven.notifyUpdate();

        processFood(oven);
    }

    public void processFood(OvenBlockEntity oven) {
        List<List<List<OvenBlockEntity.Inventory>>> inventories = oven.inventories;

        if (inventories == null)
            return;

        for (List<List<OvenBlockEntity.Inventory>> x : inventories) {
            for (List<OvenBlockEntity.Inventory> y : x) {
                for (OvenBlockEntity.Inventory inventory : y) {
                    if (inventory == null)
                        continue;
                    if (inventory.tickTillFinishCooking < 0)
                        continue;

                    if (inventory.tickTillFinishCooking > 0)
                        inventory.tickTillFinishCooking -= Math.min(Math.min(fanLevel, sizeLevel), tempLevel);

                    if (inventory.tickTillFinishCooking <= 0) {
                        if (inventory.lastRecipe == null)
                            continue;
                        ItemStack resultStack = inventory.lastRecipe.getResultItem().copy();
                        resultStack.setCount(inventory.getStackInSlot(0).getCount());
                        inventory.setStackInSlot(0, resultStack);
                    }
                }
            }
        }

        oven.notifyUpdate();
    }

    public boolean evaluate(OvenBlockEntity oven) {
        assert oven.getLevel() != null;

        int sizeLevelBefore = sizeLevel;

        sizeLevel = oven.radius * oven.height * oven.radius / 4;

        return sizeLevelBefore != sizeLevel;
    }

    public boolean updateOven(OvenBlockEntity controller) {
        assert controller.getLevel() != null;

        BlockPos controllerPos = controller.getBlockPos();
        Level level = controller.getLevel();
        updateRequired--;

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

        int fanLevelBefore = fanLevel;

        float newFanLevel = 0;

        for (int i = 0; i < controller.radius; i++) {
            for (int j = 0; j < controller.height; j++) {
                for (int k = 0; k < controller.radius; k++) {
                    for (Direction direction : Direction.values()) {
                        BlockPos pos = controller.getBlockPos().offset(i, j, k).relative(direction);
                        BlockState state = controller.getLevel().getBlockState(pos);
                        if (state.getBlock() instanceof EncasedFanBlock
                                && state.getValue(EncasedFanBlock.FACING) == direction.getOpposite()) {
                            EncasedFanBlockEntity fan = (EncasedFanBlockEntity) controller.getLevel().getBlockEntity(pos);
                            if (fan == null)
                                continue;
                            newFanLevel += Math.abs(fan.getSpeed()) / 256f;
                        }
                    }
                }
            }
        }

        newFanLevel /= 2;

        fanLevel = (int) newFanLevel;

        return tempLevel != prevTemp || fanLevelBefore != fanLevel;
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
