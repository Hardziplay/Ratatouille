package org.forsteri.ratatouille.data.recipe;

import net.minecraft.data.PackOutput;
import org.forsteri.ratatouille.entry.CRRecipeTypes;

public class BakingRecipeGen extends ProcessingRecipeGen {

//    GeneratedRecipe
//            RICE = this.create(
//            CRItems.BOIL_STONE::get,
//            b -> b.output(CRItems.MATURE_MATTER.get()).duration(200)
//    );

    public BakingRecipeGen(PackOutput p_i48262_1_) {
        super(p_i48262_1_);
    }

    @Override
    protected CRRecipeTypes getRecipeType() {
        return CRRecipeTypes.BAKING;
    }

}