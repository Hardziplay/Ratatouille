package org.forsteri.ratatouille.data.recipe.create;

import com.simibubi.create.api.data.recipe.MillingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import org.forsteri.ratatouille.Ratatouille;
import org.forsteri.ratatouille.entry.CRItems;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class RatatouilleMillingRecipeGen extends MillingRecipeGen {

    GeneratedRecipe
            COCOA_POWDER = create("cocoa_powder", b -> b
            .require(CRItems.COCOA_SOLIDS.get())
            .output(CRItems.COCOA_POWDER.get())
            .duration(200)
    ),
            MATURE_MATTER = create("mature_matter", b -> b
                    .require(CRItems.MATURE_MATTER_FOLD.get())
                    .output(CRItems.MATURE_MATTER.get(), 2)
                    .duration(200)
            ),
            RIPEN_MATTER = create("ripen_matter", b -> b
                    .require(CRItems.RIPEN_MATTER_FOLD.get())
                    .output(CRItems.RIPEN_MATTER.get(), 2)
                    .duration(200)
            ),

    WHEAT_FLOUR = create("wheat_flour", b -> b
            .require(CRItems.WHEAT_KERNELS.get())
            .output(com.simibubi.create.AllItems.WHEAT_FLOUR.get(), 2)
            .output(0.5f, com.simibubi.create.AllItems.WHEAT_FLOUR.get(), 2)
            .duration(200)
    );

    public RatatouilleMillingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Ratatouille.MOD_ID);
    }
}
