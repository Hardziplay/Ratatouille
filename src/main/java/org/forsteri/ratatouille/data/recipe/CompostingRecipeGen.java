package org.forsteri.ratatouille.data.recipe;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.data.PackOutput;
import org.forsteri.ratatouille.entry.CRFluids;
import org.forsteri.ratatouille.entry.CRItems;
import org.forsteri.ratatouille.entry.CRRecipeTypes;

/**
 * Example recipes for the compost tower.
 */
public class CompostingRecipeGen extends ProcessingRecipeGen {
    GeneratedRecipe ITEM_TO_FLUID = this.create("composting_item", b -> b
            .require(CRItems.Compost_Residue::get)
            .output(CRFluids.Compost_Tea.get(), BUCKET));

    GeneratedRecipe FLUID_TO_FLUID = this.create("composting_fluids", b -> b
            .require(CRFluids.COCOA_LIQUOR.get(), BUCKET)
            .output(CRFluids.EGG_YOLK.get(), BOTTLE)
            .output(CRFluids.BIO_GAS.get(), BOTTLE));

    public CompostingRecipeGen(PackOutput generator) {
        super(generator);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CRRecipeTypes.COMPOSTING;
    }
}
