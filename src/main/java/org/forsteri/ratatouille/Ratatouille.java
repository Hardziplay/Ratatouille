package org.forsteri.ratatouille;

import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.forsteri.ratatouille.data.recipe.RataouilleDataGen;
import org.forsteri.ratatouille.entry.CRCreativeModeTabs;
import org.forsteri.ratatouille.entry.CRRecipeTypes;
import org.forsteri.ratatouille.entry.Registrate;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Ratatouille.MOD_ID)
public class Ratatouille {

    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "ratatouille";
    // Directly reference a slf4j logger

    // Create a Deferred Register to hold Blocks which will all be registered under the "ratatouille" namespace
    public Ratatouille() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        REGISTRATE.registerEventListeners(modEventBus);

        Registrate.register();
        CRRecipeTypes.register(modEventBus);
        CRCreativeModeTabs.init();
        modEventBus.addListener(EventPriority.LOWEST, RataouilleDataGen::gatherData);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(Ratatouille.MOD_ID);
}
