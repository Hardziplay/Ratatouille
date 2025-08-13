package org.forsteri.ratatouille.content.chef_hat;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;


@MethodsReturnNonnullByDefault
public class ChefHatItem extends Item implements Equipable {
    public ChefHatItem(Properties properties) {
        super(properties);
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
    }

    @Override
    public @NotNull EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }

    @Override
    @ParametersAreNonnullByDefault
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        return swapWithEquipmentSlot(this, worldIn, playerIn, handIn);
    }
}
