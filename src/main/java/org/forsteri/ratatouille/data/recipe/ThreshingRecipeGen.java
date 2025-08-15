package org.forsteri.ratatouille.data.recipe;

import com.simibubi.create.api.data.recipe.StandardProcessingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import org.forsteri.ratatouille.Ratatouille;
import org.forsteri.ratatouille.content.thresher.ThreshingRecipe;
import org.forsteri.ratatouille.entry.CRItems;
import org.forsteri.ratatouille.entry.CRRecipeTypes;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public final class ThreshingRecipeGen extends StandardProcessingRecipeGen<ThreshingRecipe> {

    GeneratedRecipe
            RICE = this.create(
            "rice",
            b -> b
                    .require(ModItems.RICE_PANICLE.get())
                    .output(ModItems.RICE.get())
                    .output(0.5F, vectorwing.farmersdelight.common.registry.ModItems.RICE.get()).duration(200)
    ),

    BOIL_STONE = this.create("boil_stone",
            b -> b
                    .require(Tags.Items.STONES)
                    .output(CRItems.BOIL_STONE.get())
                    .duration(200)
    ),

    DRIED_COCOA_BEANS = this.create(
            "dried_cocoa_beans",
            b -> b
                    .require(CRItems.DRIED_COCOA_BEANS.get())
                    .output(CRItems.DRIED_COCOA_NIBS.get(), 2)
                    .output(0.5F, CRItems.DRIED_COCOA_NIBS.get())
                    .duration(200)
    ),

    WHEAT_KERNELS = this.create(
            "wheat_kernels",
            b -> b
                    .require(Items.WHEAT)
                    .output(CRItems.WHEAT_KERNELS.get(), 2)
                    .output(0.5F, CRItems.WHEAT_KERNELS.get())
                    .duration(200)
    );

    public ThreshingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Ratatouille.MOD_ID);
    }

    @Override
    protected CRRecipeTypes getRecipeType() {
        return CRRecipeTypes.THRESHING;
    }

}