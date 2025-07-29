package org.forsteri.ratatouille;

import com.simibubi.create.foundation.data.CreateRegistrate;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.forsteri.ratatouille.data.recipe.RatatouilleDataGen;
import org.forsteri.ratatouille.entry.*;
import org.forsteri.ratatouille.entry.CRPonderPlugin;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Ratatouille.MOD_ID)
public class Ratatouille {

    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "ratatouille";
    // Directly reference a slf4j logger
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(Ratatouille.MOD_ID);

    // Create a Deferred Register to hold Blocks which will all be registered under the "ratatouille" namespace
    public Ratatouille() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext modLoadingContext = ModLoadingContext.get();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onClientSetup);
        MinecraftForge.EVENT_BUS.register(this);
        REGISTRATE.registerEventListeners(modEventBus);
        CRPartialModels.register();
        CRSpriteShifts.register();
        CRBlocks.register();
        CRItems.register();
        CRFluids.register();
        CRBlockEntityTypes.register();
        CRTags.register();
        CRCreativeModeTabs.register(modEventBus);
        CRRecipeTypes.register(modEventBus);
        CRParticleTypes.register(modEventBus);
        CRConfigs.register(modLoadingContext);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modEventBus.addListener(CRParticleTypes::registerFactories));
        modEventBus.addListener(EventPriority.LOWEST, RatatouilleDataGen::gatherData);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    private void onClientSetup(FMLClientSetupEvent event) {
        PonderIndex.addPlugin(new CRPonderPlugin());
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        public ClientModEvents() {}
    }


}
