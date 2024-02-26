package org.forsteri.ratatouille.entry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.forsteri.ratatouille.Ratatouille;

public final class CRTags {

    public static TagKey<Item> create(String id) {
        return ItemTags.create(new ResourceLocation(Ratatouille.MOD_ID, id));
    }

    public static final TagKey<Item> MOLD = CRTags.create("mold");

    public static void register() {}
}
