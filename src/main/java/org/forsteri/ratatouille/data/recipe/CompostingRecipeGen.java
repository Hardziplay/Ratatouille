package org.forsteri.ratatouille.data.recipe;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.data.PackOutput;
import org.forsteri.ratatouille.entry.CRFluids;
import org.forsteri.ratatouille.entry.CRRecipeTypes;

public class CompostingRecipeGen extends ProcessingRecipeGen {
    GeneratedRecipe DEFAULT = this.create("composting_example", b -> b
            .require(CRFluids.COCOA_LIQUOR.get(), BUCKET)
            .output(CRFluids.EGG_YOLK.get(), BUCKET)
            .output(CRFluids.Compost_Tea.get(), BOTTLE)
            .output(CRFluids.BIO_GAS.get(), BOTTLE));

    public CompostingRecipeGen(PackOutput generator) {
        super(generator);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CRRecipeTypes.COMPOSTING;
    }
}
