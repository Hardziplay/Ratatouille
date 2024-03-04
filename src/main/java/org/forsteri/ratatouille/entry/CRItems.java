package org.forsteri.ratatouille.entry;

import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import org.forsteri.ratatouille.Ratatouille;
import org.forsteri.ratatouille.content.chocolate_mold_filled.ChocolateMoldFilledItem;

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
    public static final ItemEntry<Item> COCOA_POWDER = Ratatouille.REGISTRATE.item("cocoa_powder", Item::new).register();
    public static final ItemEntry<Item> COCOA_BUTTER = Ratatouille.REGISTRATE.item("cocoa_butter", Item::new).register();
    public static final ItemEntry<Item> DRIED_COCOA_BEANS = Ratatouille.REGISTRATE.item("dried_cocoa_beans", Item::new).register();
    public static final ItemEntry<Item> DRIED_COCOA_NIBS = Ratatouille.REGISTRATE.item("dried_cocoa_nibs", Item::new).register();
    public static final ItemEntry<Item> COCOA_SOLIDS = Ratatouille.REGISTRATE.item("cocoa_solids", Item::new).register();
    public static final ItemEntry<ChocolateMoldFilledItem> CHOCOLATE_MOLD_FILLED = Ratatouille.REGISTRATE.item("chocolate_mold_filled", ChocolateMoldFilledItem::new).register();
    public static final ItemEntry<Item> CHOCOLATE_MOLD_SOLID = Ratatouille.REGISTRATE.item("chocolate_mold_solid", Item::new).register();
    public static final ItemEntry<Item> CAKE_MOLD_FILLED = Ratatouille.REGISTRATE.item("cake_mold_filled", Item::new).register();
    public static final ItemEntry<Item> CAKE_MOLD_BAKED = Ratatouille.REGISTRATE.item("cake_mold_baked", Item::new).register();
    public static final ItemEntry<Item> EGG_SHELL = Ratatouille.REGISTRATE.item("egg_shell", Item::new).register();
    public static final ItemEntry<Item> WHEAT_KERNELS = Ratatouille.REGISTRATE.item("wheat_kernels", Item::new).register();
    public static final ItemEntry<Item> SALTY_DOUGH = Ratatouille.REGISTRATE.item("salty_dough", Item::new).register();
    public static final ItemEntry<Item> CAKE_BASE = Ratatouille.REGISTRATE.item("cake_base", Item::new).register();
    //public static final ItemEntry<Item> WET_COPPER_INGOT = Ratatouille.REGISTRATE.item("wet_copper_ingot", Item::new).register();
    //public static final ItemEntry<Item> WET_GOLD_INGOT = Ratatouille.REGISTRATE.item("wet_gold_ingot", Item::new).register();
    //public static final ItemEntry<Item> SUGAR_CUBE = Ratatouille.REGISTRATE.item("sugar_cube", Item::new).register();
    //public static final ItemEntry<Item> VANILLA_POWDER = Ratatouille.REGISTRATE.item("vanilla_powder", Item::new).register();
    //public static final ItemEntry<Item> ICE_CRYSTAL = Ratatouille.REGISTRATE.item("ice_crystal", Item::new).register();
    public CRItems() {}

    public static void register() {}
}
