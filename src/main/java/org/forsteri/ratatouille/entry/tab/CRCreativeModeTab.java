package org.forsteri.ratatouille.entry.tab;

import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.forsteri.ratatouille.Ratatouille;

import java.util.Collection;
import java.util.Iterator;

public abstract class CRCreativeModeTab extends CreativeModeTab {
    public CRCreativeModeTab(String id) {
        super( Ratatouille.MOD_ID + "." + id);
    }

    public void fillItemList(NonNullList<ItemStack> items) {
        this.addItems(items, true);
        this.addBlocks(items);
        this.addItems(items, false);
    }

    protected Collection<RegistryEntry<Item>> registeredItems() {
        return Ratatouille.REGISTRATE.getAll(ForgeRegistries.ITEMS.getRegistryKey());
    }

    public void addBlocks(NonNullList<ItemStack> items) {
        Iterator var2 = this.registeredItems().iterator();

        while(var2.hasNext()) {
            RegistryEntry<Item> entry = (RegistryEntry)var2.next();
            Object var5 = entry.get();
            if (var5 instanceof BlockItem blockItem) {
                blockItem.fillItemCategory(this, items);
            }
        }

    }

    public void addItems(NonNullList<ItemStack> items, boolean specialItems) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        Iterator var4 = this.registeredItems().iterator();

        while(var4.hasNext()) {
            RegistryEntry<Item> entry = (RegistryEntry)var4.next();
            Item item = (Item)entry.get();
            if (!(item instanceof BlockItem)) {
                ItemStack stack = new ItemStack(item);
                BakedModel model = itemRenderer.getModel(stack, (Level)null, (LivingEntity)null, 0);
                if (model.isGui3d() == specialItems) {
                    item.fillItemCategory(this, items);
                }
            }
        }

    }
}
