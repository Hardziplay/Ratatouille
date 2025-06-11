package org.forsteri.ratatouille.content.chocolate_mold_filled;

import net.minecraft.world.item.Item;
public class ChocolateMoldFilledItem extends Item {

    public static int MAX_CHILLNESS = 50;
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
