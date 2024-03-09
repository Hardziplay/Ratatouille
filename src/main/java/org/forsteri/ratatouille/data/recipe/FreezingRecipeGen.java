package org.forsteri.ratatouille.data.recipe;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import org.forsteri.ratatouille.entry.CRItems;
import org.forsteri.ratatouille.entry.CRRecipeTypes;

public class FreezingRecipeGen extends ProcessingRecipeGen{
    GeneratedRecipe
            CHOCOLATE_MOLD_SOLID  = this.create(
            CRItems.CHOCOLATE_MOLD_FILLED::get,
            b -> b.output(CRItems.CHOCOLATE_MOLD_SOLID.get())
    );
    public FreezingRecipeGen(PackOutput generator) {
        super(generator);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CRRecipeTypes.FREEZING;
    }
}
