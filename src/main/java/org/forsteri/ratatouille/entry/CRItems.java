package org.forsteri.ratatouille.entry;

import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import org.forsteri.ratatouille.Ratatouille;

public class CRItems {
    static {
        Ratatouille.REGISTRATE.creativeModeTab(() -> {
            return CRCreativeModeTabs.BASE_CREATIVE_TAB;
        });
    }

    public static final ItemEntry<Item> SAUSAGE_CASING = Ratatouille.REGISTRATE.item("sausage_casing", Item::new).register();

    public CRItems() {}

    public static void register() {}
}
