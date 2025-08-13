package org.forsteri.ratatouille.data.recipe;

import com.simibubi.create.api.data.recipe.StandardProcessingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import org.forsteri.ratatouille.Ratatouille;
import org.forsteri.ratatouille.content.thresher.ThreshingRecipe;
import org.forsteri.ratatouille.entry.CRRecipeTypes;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public final class ThreshingRecipeGen extends StandardProcessingRecipeGen<ThreshingRecipe> {

    GeneratedRecipe
            RICE = this.create(
            ModItems.RICE_PANICLE::get,
            b -> b.output(vectorwing.farmersdelight.common.registry.ModItems.RICE.get()).output(0.5F, vectorwing.farmersdelight.common.registry.ModItems.RICE.get()).duration(200).whenModLoaded("farmersdelight")
    );

    //            CORN_KERNELS = this.create(
//                    () -> com.ncpbails.culturaldelights.item.ModItems.CORN_COB.get(),
//                    b -> b.output(com.ncpbails.culturaldelights.item.ModItems.CORN_KERNELS.get()).output(0.5F, com.ncpbails.culturaldelights.item.ModItems.CORN_KERNELS.get()).duration(200).whenModLoaded("culturaldelights")
//            );
    public ThreshingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Ratatouille.MOD_ID);
    }

    @Override
    protected CRRecipeTypes getRecipeType() {
        return CRRecipeTypes.THRESHING;
    }

}