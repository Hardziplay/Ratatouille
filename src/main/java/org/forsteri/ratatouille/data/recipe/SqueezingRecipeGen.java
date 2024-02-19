package org.forsteri.ratatouille.data.recipe;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.data.DataGenerator;
import org.forsteri.ratatouille.entry.CRRecipeTypes;

public class SqueezingRecipeGen extends ProcessingRecipeGen{

    public SqueezingRecipeGen(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CRRecipeTypes.SQUEEZING;
    }
}
