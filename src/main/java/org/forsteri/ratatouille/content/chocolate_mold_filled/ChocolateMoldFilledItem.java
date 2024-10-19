package org.forsteri.ratatouille.content.chocolate_mold_filled;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.utility.Color;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.forsteri.ratatouille.entry.CRBlocks;
import org.forsteri.ratatouille.entry.CRItems;
import org.jetbrains.annotations.NotNull;

public class ChocolateMoldFilledItem extends Item {

//    public static int MAX_CHILLNESS = 50;
    public ChocolateMoldFilledItem(Properties pProperties) {
        super(pProperties);
    }
//
//    public int getChillness(ItemStack stack) {
//        return stack.getOrCreateTag()
//                .getInt("CollectingChillness");
//    }
//    @Override
//    public boolean isBarVisible(ItemStack stack) {
//        return getChillness(stack) > 0;
//    }
//
//
//    @Override
//    public int getBarWidth(ItemStack stack) {
//        return Math.round(13.0F * getChillness(stack) / MAX_CHILLNESS);
//    }
//
//    @Override
//    public int getBarColor(ItemStack stack) {
//        return Color.mixColors(0x413c69, 0xFFFFFF,
//                getChillness(stack) / (float) MAX_CHILLNESS);
//    }
}
