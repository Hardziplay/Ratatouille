package org.forsteri.ratatouille.data.recipe;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Items;
import org.forsteri.ratatouille.entry.CRFluids;
import org.forsteri.ratatouille.entry.CRItems;
import org.forsteri.ratatouille.entry.CRRecipeTypes;

public class SqueezingRecipeGen extends ProcessingRecipeGen{
    GeneratedRecipe
        RAW_SAUSAGE = this.create("raw_sausage", b -> b.require(CRFluids.MINCE_MEAT.get(), 1000)
            .require(CRItems.SAUSAGE_CASING.get())
            .output(CRItems.RAW_SAUSAGE.get())),

        RAW_PASTA = this.create("raw_pasta", b -> b.require(CRItems.SALTY_DOUGH.get())
                .output(vectorwing.farmersdelight.common.registry.ModItems.RAW_PASTA.get()));

    public SqueezingRecipeGen(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CRRecipeTypes.SQUEEZING;
    }
}
