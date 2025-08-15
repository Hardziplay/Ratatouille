package org.forsteri.ratatouille.data.recipe;

import com.simibubi.create.api.data.recipe.StandardProcessingRecipeGen;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import org.forsteri.ratatouille.Ratatouille;
import org.forsteri.ratatouille.content.frozen_block.FreezingRecipe;
import org.forsteri.ratatouille.entry.CRItems;
import org.forsteri.ratatouille.entry.CRRecipeTypes;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class FreezingRecipeGen extends StandardProcessingRecipeGen<FreezingRecipe> {
    GeneratedRecipe
            CHOCOLATE_MOLD_SOLID = this.create(
            CRItems.CHOCOLATE_MOLD_FILLED::get,
            b -> b.output(CRItems.CHOCOLATE_MOLD_SOLID.get())),

            BLUE_ICE = this.create(
            Items.PACKED_ICE::asItem,
            b -> b.output(Items.BLUE_ICE)
            ),

            MELON_POPSICLE_MOLD_SOLID = this.create(
            CRItems.MELON_POPSICLE_MOLD_FILLED::get,
            b -> b.output(CRItems.MELON_POPSICLE_MOLD_SOLID.get())
            );

    public FreezingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Ratatouille.MOD_ID);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CRRecipeTypes.FREEZING;
    }
}
