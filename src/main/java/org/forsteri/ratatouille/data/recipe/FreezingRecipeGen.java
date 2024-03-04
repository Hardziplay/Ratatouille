package org.forsteri.ratatouille.data.recipe;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.data.DataGenerator;
import org.forsteri.ratatouille.entry.CRItems;
import org.forsteri.ratatouille.entry.CRRecipeTypes;

public class FreezingRecipeGen extends ProcessingRecipeGen{
    GeneratedRecipe
            CHOCOLATE_MOLD_SOLID  = this.create(
            CRItems.CHOCOLATE_MOLD_FILLED::get,
            b -> b.output(CRItems.CHOCOLATE_MOLD_SOLID.get())
    );
    public FreezingRecipeGen(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CRRecipeTypes.FREEZING;
    }
}
