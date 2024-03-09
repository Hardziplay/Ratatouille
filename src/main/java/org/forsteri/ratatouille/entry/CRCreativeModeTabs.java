package org.forsteri.ratatouille.entry;

import com.simibubi.create.foundation.utility.Components;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.forsteri.ratatouille.Ratatouille;

public class CRCreativeModeTabs {
    private static final DeferredRegister<CreativeModeTab> REGISTER;
    public static final RegistryObject<CreativeModeTab> BASE_CREATIVE_TAB;

    public CRCreativeModeTabs() {}

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }

    static {
        REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Ratatouille.MOD_ID);
        BASE_CREATIVE_TAB = REGISTER.register("base", () -> {
            return CreativeModeTab.builder().title(Components.translatable("itemGroup.ratatouille.base")).withTabsBefore(new ResourceKey[]{CreativeModeTabs.SPAWN_EGGS}).icon(CRBlocks.OVEN::asStack).build();
        });
    }
}


