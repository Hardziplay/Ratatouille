package org.forsteri.ratatouille.entry;

import net.createmod.catnip.lang.Lang;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.forsteri.ratatouille.Ratatouille;

public class CRTags {
    
    public static void init() {
        CRItemTags.init();
    }

    public enum CRItemTags {
        MOLD;

        public final TagKey<Item> tag;

        CRItemTags() {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Ratatouille.MOD_ID, Lang.asId(name()));
            tag = ItemTags.create(id);
        }

        private static void init() {
        }
    }
}
