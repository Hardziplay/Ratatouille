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
        return 0;
    }

    @Override
    protected int getMaxFluidInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 0;
    }

    @Override
    protected int getMaxFluidOutputCount() {
        return 3;
    }

    @Override
    public boolean matches(@NotNull RecipeWrapper inv, @NotNull Level world) {
        return true;
    }
}
