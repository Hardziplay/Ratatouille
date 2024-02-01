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
    public static final ItemEntry<Item> SAUSAGE = Ratatouille.REGISTRATE.item("sausage", Item::new).register();
    public static final ItemEntry<Item> RAW_SAUSAGE = Ratatouille.REGISTRATE.item("raw_sausage", Item::new).register();
    public static final ItemEntry<Item> SALT = Ratatouille.REGISTRATE.item("salt", Item::new).register();
    public static final ItemEntry<Item> CAKE_MOLD = Ratatouille.REGISTRATE.item("cake_mold", Item::new).register();
    public static final ItemEntry<Item> CHOCOLATE_MOLD = Ratatouille.REGISTRATE.item("chocolate_mold", Item::new).register();
    public static final ItemEntry<Item> COCOABEAN_POWDER = Ratatouille.REGISTRATE.item("cocoabean_powder", Item::new).register();
    public static final ItemEntry<Item> COCOABUTTER = Ratatouille.REGISTRATE.item("cocoabutter", Item::new).register();
    public static final ItemEntry<Item> DRIED_COCOABEANS = Ratatouille.REGISTRATE.item("dried_cocoabeans", Item::new).register();
    public static final ItemEntry<Item> DRIED_COCOANIBS = Ratatouille.REGISTRATE.item("dried_cocoanibs", Item::new).register();
    public static final ItemEntry<Item> ICE_CRYSTAL = Ratatouille.REGISTRATE.item("ice_crystal", Item::new).register();
    public static final ItemEntry<Item> VANILLA_POWDER = Ratatouille.REGISTRATE.item("vanilla_powder", Item::new).register();
    public static final ItemEntry<Item> SUGAR_BAR = Ratatouille.REGISTRATE.item("sugar_bar", Item::new).register();

    public CRItems() {}

    public static void register() {}
}
