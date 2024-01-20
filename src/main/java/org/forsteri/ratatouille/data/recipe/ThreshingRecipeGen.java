package org.forsteri.ratatouille.data.recipe;
import net.minecraft.data.DataGenerator;
import org.forsteri.ratatouille.entry.CRRecipeTypes;

public class ThreshingRecipeGen extends ProcessingRecipeGen {

    GeneratedRecipe
            RICE = this.create(
                () -> vectorwing.farmersdelight.common.registry.ModItems.RICE_PANICLE.get(),
                b -> b.output(vectorwing.farmersdelight.common.registry.ModItems.RICE.get()).output(0.5F, vectorwing.farmersdelight.common.registry.ModItems.RICE.get()).duration(200)
            ),
            CORN_KERNELS = this.create(
                    () -> com.ncpbails.culturaldelights.item.ModItems.CORN_COB.get(),
                    b -> b.output(com.ncpbails.culturaldelights.item.ModItems.CORN_KERNELS.get()).output(0.5F, com.ncpbails.culturaldelights.item.ModItems.CORN_KERNELS.get()).duration(200)
            );
    public ThreshingRecipeGen(DataGenerator p_i48262_1_) {
        super(p_i48262_1_);
    }

    @Override
    protected CRRecipeTypes getRecipeType() {
        return CRRecipeTypes.THRESHING;
    }

}