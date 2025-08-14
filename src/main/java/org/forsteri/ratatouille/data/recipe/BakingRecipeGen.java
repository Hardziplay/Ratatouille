package org.forsteri.ratatouille.data.recipe;

import com.simibubi.create.api.data.recipe.StandardProcessingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import org.forsteri.ratatouille.Ratatouille;
import org.forsteri.ratatouille.content.oven.BakingRecipe;
import org.forsteri.ratatouille.entry.CRRecipeTypes;

import java.util.concurrent.CompletableFuture;

public class BakingRecipeGen extends StandardProcessingRecipeGen<BakingRecipe> {

//    GeneratedRecipe
//            RICE = this.create(
//            CRItems.BOIL_STONE::get,
//            b -> b.output(CRItems.MATURE_MATTER.get()).duration(200)
//    );

    public BakingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Ratatouille.MOD_ID);
    }

    @Override
    protected CRRecipeTypes getRecipeType() {
        return CRRecipeTypes.BAKING;
    }

}