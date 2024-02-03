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
    public static final ItemEntry<Item> COCOA_BEAN_POWDER = Ratatouille.REGISTRATE.item("cocoa_bean_powder", Item::new).register();
    public static final ItemEntry<Item> COCOA_BUTTER = Ratatouille.REGISTRATE.item("cocoa_butter", Item::new).register();
    public static final ItemEntry<Item> DRIED_COCOA_BEANS = Ratatouille.REGISTRATE.item("dried_cocoa_beans", Item::new).register();
    public static final ItemEntry<Item> DRIED_COCOA_NIBS = Ratatouille.REGISTRATE.item("dried_cocoa_nibs", Item::new).register();
    public static final ItemEntry<Item> COCOA_BAR = Ratatouille.REGISTRATE.item("cocoa_bar", Item::new).register();
    public static final ItemEntry<Item> ICE_CRYSTAL = Ratatouille.REGISTRATE.item("ice_crystal", Item::new).register();
    public static final ItemEntry<Item> VANILLA_POWDER = Ratatouille.REGISTRATE.item("vanilla_powder", Item::new).register();
    public static final ItemEntry<Item> SUGAR_BAR = Ratatouille.REGISTRATE.item("sugar_bar", Item::new).register();

    public CRItems() {}

    public static void register() {}
}
