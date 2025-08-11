package org.forsteri.ratatouille.data.recipe;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.data.PackOutput;
import org.forsteri.ratatouille.entry.CRFluids;
import org.forsteri.ratatouille.entry.CRItems;
import org.forsteri.ratatouille.entry.CRRecipeTypes;

public class CompostingRecipeGen extends ProcessingRecipeGen {
    GeneratedRecipe DEFAULT = this.create("composting", b -> b
            .require(CRItems.COMPOST_MASS.get())
            .duration(200)
            //.output(CRItems.COMPOST_RESIDUE.get(), 1)
            .output(CRFluids.COMPOST_RESIDUE_FLUID.get(), 60)
            .output(CRFluids.COMPOST_TEA.get(), 30)
            .output(CRFluids.BIO_GAS.get(), 10));

    public CompostingRecipeGen(PackOutput generator) {
        super(generator);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CRRecipeTypes.COMPOSTING;
    }
}
