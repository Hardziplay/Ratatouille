package org.forsteri.ratatouille.data.recipe.create;

import com.simibubi.create.api.data.recipe.SequencedAssemblyRecipeGen;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import org.forsteri.ratatouille.Ratatouille;
import org.forsteri.ratatouille.entry.CRFluids;
import org.forsteri.ratatouille.entry.CRItems;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class RatatouilleSequencedAssemblyRecipeGen extends SequencedAssemblyRecipeGen {
    GeneratedRecipe
            RIPEN_MATTER_FOLD = create("ripen_matter_fold", b -> b
            .require(CRItems.COMPOST_RESIDUE.get())
            .transitionTo(CRItems.UNPROCESSED_RIPEN_MATTER_FOLD.get())
            .addOutput(CRItems.RIPEN_MATTER_FOLD.get(), 1)
            .loops(2)
            .addStep(FillingRecipe::new, rb -> rb
                    .require(CRFluids.COMPOST_TEA.get(), 100)
            )
            .addStep(PressingRecipe::new, rb -> rb)
    ),

    MATURE_MATTER_FOLD = create("mature_matter_fold", b -> b
            .require(CRItems.RIPEN_MATTER.get())
            .transitionTo(CRItems.UNPROCESSED_MATURE_MATTER_FOLD.get())
            .addOutput(CRItems.MATURE_MATTER_FOLD.get(), 1)
            .loops(2)
            .addStep(FillingRecipe::new, rb -> rb
                    .require(CRFluids.COMPOST_TEA.get(), 100)
            )
            .addStep(PressingRecipe::new, rb -> rb)
    );

    public RatatouilleSequencedAssemblyRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Ratatouille.MOD_ID);
    }
}
