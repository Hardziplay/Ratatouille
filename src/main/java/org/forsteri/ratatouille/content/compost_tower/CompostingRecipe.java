package org.forsteri.ratatouille.content.compost_tower;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.forsteri.ratatouille.entry.CRRecipeTypes;
import org.jetbrains.annotations.NotNull;

public class CompostingRecipe extends ProcessingRecipe<RecipeWrapper> {
    public CompostingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(CRRecipeTypes.COMPOSTING, params);
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 1;
    }

    @Override
    protected int getMaxFluidOutputCount() {
        return 2;
    }

    @Override
    public boolean matches(@NotNull RecipeWrapper inv, @NotNull Level world) {
        // hard-coded in compost tower
        return true;
    }
}
