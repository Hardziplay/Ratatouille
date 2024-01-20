package org.forsteri.ratatouille.data.recipe;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
public class RataouilleDataGen {
    public RataouilleDataGen() {
    }

    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();

        if (event.includeServer()) {
            ProcessingRecipeGen.registerAll(generator);
        }
    }
}
