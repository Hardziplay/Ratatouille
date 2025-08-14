package org.forsteri.ratatouille.data.recipe.create;

import com.simibubi.create.api.data.recipe.CuttingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import org.forsteri.ratatouille.Ratatouille;
import org.forsteri.ratatouille.entry.CRItems;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class RatatouilleCuttingRecipeGen extends CuttingRecipeGen {
    GeneratedRecipe
            SAUSAGE_CASING = create("sausage_casing", b -> b
            .require(Items.SLIME_BALL)
            .output(CRItems.SAUSAGE_CASING.get(), 2)
    );

    public RatatouilleCuttingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Ratatouille.MOD_ID);
    }
}
