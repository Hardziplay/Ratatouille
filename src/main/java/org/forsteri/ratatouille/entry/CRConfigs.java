package org.forsteri.ratatouille.entry;

import com.simibubi.create.api.stress.BlockStressValues;
import net.createmod.catnip.config.ConfigBase;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public class CRConfigs {
    private static final Map<ModConfig.Type, ConfigBase> CONFIGS = new EnumMap<>(ModConfig.Type.class);
    //    private static CClient client;
//    private static CCommon common;
    private static CRServer server;

//    public static CClient client() {
//        return client;
//    }
//
//    public static CCommon common() {
//        return common;
//    }

    public static ConfigBase byType(ModConfig.Type type) {
        return (ConfigBase) CONFIGS.get(type);
    }

    public static void register(ModLoadingContext context, ModContainer container) {
//        client = (CClient)register(CClient::new, ModConfig.Type.CLIENT);
//        common = (CCommon)register(CCommon::new, ModConfig.Type.COMMON);
        server = (CRServer) register(CRServer::new, ModConfig.Type.SERVER);

        for (Map.Entry<ModConfig.Type, ConfigBase> pair : CONFIGS.entrySet()) {
            container.registerConfig((ModConfig.Type) pair.getKey(), ((ConfigBase) pair.getValue()).specification);
        }

        CRStress stress = server().kinetics.stressValues;
        BlockStressValues.IMPACTS.registerProvider(stress::getImpact);
        BlockStressValues.CAPACITIES.registerProvider(stress::getCapacity);
    }

    private static <T extends ConfigBase> T register(Supplier<T> factory, ModConfig.Type side) {
        Pair<T, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(builder -> {
            T config = factory.get();
            config.registerAll(builder);
            return config;
        });

        T config = specPair.getLeft();
        config.specification = specPair.getRight();
        CONFIGS.put(side, config);
        return config;
    }

    public static CRServer server() {
        return server;
    }

    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {
        for (ConfigBase config : CONFIGS.values())
            if (config.specification == event.getConfig()
                    .getSpec())
                config.onLoad();
    }

    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading event) {
        for (ConfigBase config : CONFIGS.values()) {
            if (config.specification == event.getConfig().getSpec()) {
                config.onReload();
            }
        }

    }
}
