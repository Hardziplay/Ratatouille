package org.forsteri.ratatouille.data.recipe;

import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.StandardProcessingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import org.forsteri.ratatouille.Ratatouille;
import org.forsteri.ratatouille.content.demolder.DemoldingRecipe;
import org.forsteri.ratatouille.entry.CRItems;
import org.forsteri.ratatouille.entry.CRRecipeTypes;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class DemoldingRecipeGen extends StandardProcessingRecipeGen<DemoldingRecipe> {
    GeneratedRecipe
            BAR_OF_CHOCOLATE = this.create(
            CRItems.CHOCOLATE_MOLD_SOLID::get,
            b -> b.output(AllItems.BAR_OF_CHOCOLATE.get())
                    .output(CRItems.CHOCOLATE_MOLD.get())
    ),
            CAKE_BASE = this.create(
                    CRItems.CAKE_MOLD_BAKED::get,
                    b -> b.output(CRItems.CAKE_BASE.get())
                            .output(CRItems.CAKE_MOLD.get())
            );

    public DemoldingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Ratatouille.MOD_ID);
    }

    @Override
    protected CRRecipeTypes getRecipeType() {
        return CRRecipeTypes.DEMOLDING;
    }
}
