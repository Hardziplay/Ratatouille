package org.forsteri.ratatouille.content.compost_tower;

import com.simibubi.create.content.fluids.tank.BoilerHeaters;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import joptsimple.internal.Strings;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.forsteri.ratatouille.content.thresher.ThreshingRecipe;
import org.forsteri.ratatouille.entry.CRFluids;
import org.forsteri.ratatouille.entry.CRItems;
import org.forsteri.ratatouille.entry.CRRecipeTypes;
import org.forsteri.ratatouille.util.Lang;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CompostData {
    public int sizeLevel;
    public int tempLevel;
    public int updateRequired;
    public int timer;
    public CompostingRecipe lastRecipe;

    public void clear() {
        sizeLevel = 0;
        tempLevel = 0;
        timer = 0;
        lastRecipe = null;
    }

    public boolean updateCompostTower(CompostTowerBlockEntity controller) {
        assert controller.getLevel() != null;

        BlockPos controllerPos = controller.getBlockPos();
        Level level = controller.getLevel();
        updateRequired--;

        int prevTemp = tempLevel;
        tempLevel = 0;

        for (int xOffset = 0; xOffset < controller.getWidth(); xOffset++) {
            for (int zOffset = 0; zOffset < controller.getWidth(); zOffset++) {
                BlockPos pos = controllerPos.offset(xOffset, -1, zOffset);
                BlockState blockState = level.getBlockState(pos);

                if (blockState.getBlock() instanceof BlazeBurnerBlock
                        || blockState.hasProperty(BlazeBurnerBlock.HEAT_LEVEL)) {

                    float heat = BoilerHeaters.blazeBurner(level, pos, blockState);
                    if (heat > 0)
                        tempLevel += heat;
                }
            }
        }
        return tempLevel != prevTemp;
    }

    public void tick(CompostTowerBlockEntity tower) {
        if (updateCompostTower(tower))
            tower.notifyUpdate();

        if (tower.tanks == null) return;
        var itemHandler = tower.itemCapability.orElse(null);
        var fluidHandler = tower.fluidCapability.orElse(null);

        if (itemHandler == null || fluidHandler == null) return;
        assert tower.getLevel() != null;

        RecipeWrapper inventoryIn = new RecipeWrapper(tower.getInputInvs());
        if (timer > 0) {
            if (this.lastRecipe == null || !this.lastRecipe.matches(inventoryIn, tower.getLevel())) {
                Optional<CompostingRecipe> recipe = CRRecipeTypes.COMPOSTING.find(inventoryIn, tower.getLevel());
                if (recipe.isEmpty()) {
                    return;
                }
                this.lastRecipe =  recipe.get();
            }

            boolean canOutput = true;
            for (ItemStack outputStack : lastRecipe.rollResults()) {
                if (outputStack.isEmpty()) continue;
                if (!ItemHandlerHelper.insertItemStacked(tower.getOutputInvs(), outputStack, true).isEmpty()) {
                    canOutput = false;
                    break;
                }
            }
            for (FluidStack fluidStack : lastRecipe.getFluidResults()) {
                if (fluidStack.isEmpty()) continue;
                if (fluidHandler.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE) < fluidStack.getAmount()) {
                    canOutput = false;
                    break;
                }
            }
            if (!canOutput) {
                timer = 100;
                return;
            }

            timer -= getProcessingSpeed();
            if (tower.getLevel().isClientSide) {
                return;
            }
            if (timer <= 0) {
                tower.getInputInvs().consume(lastRecipe.getIngredients().get(0).getItems()[0], false);
                this.lastRecipe.rollResults().forEach((stack) -> {
                    ItemHandlerHelper.insertItemStacked(tower.getOutputInvs(), stack, false);
                });
                this.lastRecipe.getFluidResults().forEach((fluidStack) -> {
                    fluidHandler.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                });
                tower.notifyUpdate();
            }
            return;
        }

        if (lastRecipe == null || !lastRecipe.matches(inventoryIn, tower.getLevel())) {
            Optional<CompostingRecipe> recipe = CRRecipeTypes.COMPOSTING.find(inventoryIn, tower.getLevel());
            if (recipe.isEmpty()) {
                timer = 100;
            } else {
                lastRecipe = recipe.get();
                timer = lastRecipe.getProcessingDuration();
            }
        } else {
            timer = lastRecipe.getProcessingDuration();
        }
        tower.notifyUpdate();
    }

    public boolean evaluate(CompostTowerBlockEntity tower) {
        assert tower.getLevel() != null;

        int sizeLevelBefore = sizeLevel;

        sizeLevel = tower.getWidth() * tower.getHeight() * tower.getWidth() / 4;

        return sizeLevelBefore != sizeLevel;
    }


    public void read(CompoundTag compound, boolean ignoredClientPacket) {
        sizeLevel = compound.getInt("sizeCount");
        tempLevel = compound.getInt("tempCount");
    }

    public void write(CompoundTag compound, boolean ignoredClientPacket) {
        compound.putInt("sizeCount", sizeLevel);
        compound.putInt("tempCount", tempLevel);
    }

    public MutableComponent getSizeComponent(boolean forGoggles, boolean useBlocksAsBars, ChatFormatting... styles) {
        return componentHelper("size", sizeLevel, forGoggles, useBlocksAsBars, styles);
    }

    public MutableComponent getHeatComponent(boolean forGoggles, boolean useBlocksAsBars, ChatFormatting... styles) {
        return componentHelper("heat", tempLevel, forGoggles, useBlocksAsBars, styles);
    }

    private MutableComponent componentHelper(String label, int level, boolean forGoggles, boolean useBlocksAsBars,
                                             ChatFormatting... styles) {
        MutableComponent base = useBlocksAsBars ? blockComponent(level) : barComponent(level);

        if (!forGoggles)
            return base;

        ChatFormatting style1 = styles.length >= 1 ? styles[0] : ChatFormatting.GRAY;
        ChatFormatting style2 = styles.length >= 2 ? styles[1] : ChatFormatting.DARK_GRAY;

        return Lang.translateDirect("compost_tower." + label)
                .withStyle(style1)
                .append(Lang.translateDirect("compost_tower." + label + "_dots")
                        .withStyle(style2))
                .append(base);
    }

    private MutableComponent blockComponent(int level) {
        int clamped = Mth.clamp(level, 0, 8);
        return Component.literal("\u2588".repeat(clamped) + "\u2591".repeat(8 - clamped));}

    private MutableComponent barComponent(int level) {
        return Component.empty()
                .append(bars(Math.max(0, 0 - 1), ChatFormatting.DARK_GREEN))
                .append(bars(0 > 0 ? 1 : 0, ChatFormatting.GREEN))
                .append(bars(Math.max(0, level - 0), ChatFormatting.DARK_GREEN))
                .append(bars(Math.max(0, 8 - level), ChatFormatting.DARK_RED))
                .append(bars(Math.max(0, Math.min(18 - 8, ((8 / 5 + 1) * 5) - 8)),
                        ChatFormatting.DARK_GRAY));

    }

    private MutableComponent bars(int level, ChatFormatting format) {
        return Component.literal(Strings.repeat('|', level))
                .withStyle(format);
    }

    public @NotNull MutableComponent getLevelComponent() {
        int compostTowerLevel = Math.min(this.sizeLevel, this.tempLevel);
        if (compostTowerLevel == 0) {
            return Lang.translateDirect("compost_tower.idle", new Object[0]);
        } else {
            return compostTowerLevel == 8 ? Lang.translateDirect("compost_tower.max_lvl", new Object[0]) : Lang.translateDirect("compost_tower.lvl", new Object[0]).append(String.valueOf(compostTowerLevel));
        }
    }

    public boolean addToGoggleTooltip(List<Component> tooltip, boolean ignoredIsPlayerSneaking) {
        Component indent = Component.literal("    ");
        Component indent2 = Component.literal("     ");

        tooltip.add(indent.copy()
                .append(Lang.translateDirect("compost_tower.status")
                        .append(this.getLevelComponent().withStyle(ChatFormatting.GREEN))));

        tooltip.add(indent2.copy().append(this.getSizeComponent(true, false)));
        tooltip.add(indent2.copy().append(this.getHeatComponent(true, false)));

        return true;
    }

    public int getProcessingSpeed() {
        return (int) (Math.max(1.0, sizeLevel / 8.0 * tempLevel / 8.0) * 512);
    }
}
