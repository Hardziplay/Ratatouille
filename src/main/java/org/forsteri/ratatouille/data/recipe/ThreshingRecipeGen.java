package org.forsteri.ratatouille.data.recipe;
import net.minecraft.data.DataGenerator;
import org.forsteri.ratatouille.entry.CRRecipeTypes;
import vectorwing.farmersdelight.common.registry.ModItems;

public class ThreshingRecipeGen extends ProcessingRecipeGen {

    GeneratedRecipe
            RICE = this.create(
                () -> ModItems.RICE_PANICLE.get(),
                b -> b.output(ModItems.RICE.get()).output(0.5F, ModItems.RICE.get())
            );
    public ThreshingRecipeGen(DataGenerator p_i48262_1_) {
        super(p_i48262_1_);
    }

    @Override
    protected CRRecipeTypes getRecipeType() {
        return CRRecipeTypes.THRESHING;
    }

}