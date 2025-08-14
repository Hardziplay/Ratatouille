package org.forsteri.ratatouille.data.recipe.create;

import com.simibubi.create.api.data.recipe.EmptyingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import org.forsteri.ratatouille.Ratatouille;
import org.forsteri.ratatouille.entry.CRFluids;
import org.forsteri.ratatouille.entry.CRItems;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class RatatouilleEmptyingRecipeGen extends EmptyingRecipeGen {
    GeneratedRecipe
            BIO_GAS_BUCKET_EMPTY = create("bio_gas_bucket_empty", b -> b
            .require(CRFluids.BIO_GAS.getBucket().get())
            .output(CRFluids.BIO_GAS.get(), 1000)
            .output(Items.BUCKET)
    ),
            COMPOST_TEA_BOTTLE_EMPTY = create("compost_tea_bottle_empty", b -> b
                    .require(CRItems.COMPOST_TEA_BOTTLE.get())
                    .output(CRFluids.COMPOST_TEA.get(), 100)
                    .output(Items.GLASS_BOTTLE)
            ),
            EGG_TO_YOLK = create("egg_to_yolk", b -> b
                    .require(Items.EGG)
                    .output(CRFluids.EGG_YOLK.get(), 250)
                    .output(CRItems.EGG_SHELL.get())
            );
    ;

    public RatatouilleEmptyingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Ratatouille.MOD_ID);
    }
}
