package org.forsteri.ratatouille.content.squeeze_basin;

import com.simibubi.create.foundation.item.SmartInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public class SqueezeBasinInventory extends SmartInventory implements RecipeInput {
    public SqueezeBasinBlockEntity blockEntity;

    public SqueezeBasinInventory(int slots, SqueezeBasinBlockEntity be) {
        super(slots, be, 16, true);
        this.blockEntity = be;
    }

    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        for (int i = 0; i < this.getSlots(); ++i) {
            if (i != slot && ItemStack.isSameItemSameComponents(stack, this.inv.getStackInSlot(i))) {
                return stack;
            }
        }

        return super.insertItem(slot, stack, simulate);
    }

    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack extractItem = super.extractItem(slot, amount, simulate);
        if (!simulate && !extractItem.isEmpty()) {
            this.blockEntity.notifyChangeOfContents();
        }

        return extractItem;
    }

    @Override
    public int size() {
        return this.getSlots();
    }
}
