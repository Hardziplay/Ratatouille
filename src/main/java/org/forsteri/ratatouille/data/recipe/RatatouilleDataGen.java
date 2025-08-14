package org.forsteri.ratatouille.data.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.tterrag.registrate.providers.ProviderType;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.forsteri.ratatouille.Ratatouille;
import org.forsteri.ratatouille.entry.CRPonderPlugin;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class RatatouilleDataGen {
    public RatatouilleDataGen() {
    }

    public static void gatherDataHighPriority(GatherDataEvent event) {
        if (event.getMods().contains(Ratatouille.MOD_ID))
            addExtraRegistrateData();
    }

    private static void addExtraRegistrateData() {

        Ratatouille.REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> {
            BiConsumer<String, String> langConsumer = provider::add;

            provideDefaultLang("en_us", langConsumer);
            providePonderLang(langConsumer);
        });
    }

    private static void provideDefaultLang(String fileName, BiConsumer<String, String> consumer) {
        String path = "assets/ratatouille/lang/default/" + fileName + ".json";
        JsonElement jsonElement = FilesHelper.loadJsonResource(path);
        if (jsonElement == null) {
            throw new IllegalStateException(String.format("Could not find default lang file: %s", path));
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().getAsString();
            consumer.accept(key, value);
        }
    }

    private static void providePonderLang(BiConsumer<String, String> consumer) {
        PonderIndex.addPlugin(new CRPonderPlugin());
        PonderIndex.getLangAccess().provideLang(Ratatouille.MOD_ID, consumer);
    }

    public static void gatherData(GatherDataEvent event) {
        if (!event.getMods().contains(Ratatouille.MOD_ID))
            return;

        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new RatatouilleStandardRecipeGen(output, lookupProvider));

        if (event.includeServer()) {
            RatatouilleRecipeProvider.registerAllProcessing(generator, output, lookupProvider);
        }
    }
}
