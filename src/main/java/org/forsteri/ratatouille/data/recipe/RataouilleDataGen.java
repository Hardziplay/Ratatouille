package org.forsteri.ratatouille.data.recipe;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.ponder.PonderLocalization;
import com.simibubi.create.infrastructure.ponder.AllPonderTags;
import com.simibubi.create.infrastructure.ponder.GeneralText;
import com.simibubi.create.infrastructure.ponder.PonderIndex;
import com.simibubi.create.infrastructure.ponder.SharedText;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import org.forsteri.ratatouille.Ratatouille;
import org.forsteri.ratatouille.entry.CRPonders;

import java.util.function.BiConsumer;

public class RataouilleDataGen {
    public RataouilleDataGen() {
    }

    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        Ratatouille.REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> {
            BiConsumer<String, String> langConsumer = provider::add;
            providePonderLang(langConsumer);
        });

        if (event.includeServer()) {
            ProcessingRecipeGen.registerAll(generator);
        }
    }

    private static void providePonderLang(BiConsumer<String, String> consumer) {
        CRPonders.register();
        PonderIndex.register();

        SharedText.gatherText();
        PonderLocalization.generateSceneLang();

        GeneralText.provideLang(consumer);
        PonderLocalization.provideLang(Ratatouille.MOD_ID, consumer);
    }
}
