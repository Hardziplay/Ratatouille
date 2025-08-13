package org.forsteri.ratatouille.content.compost_tower;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import org.forsteri.ratatouille.entry.CRRecipeTypes;
import org.jetbrains.annotations.NotNull;

public class CompostingRecipe extends StandardProcessingRecipe<RecipeInput> {
    public CompostingRecipe(ProcessingRecipeParams params) {
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
    protected boolean canSpecifyDuration() {
        return true;
    }

    @Override
    protected int getMaxFluidOutputCount() {
        return 3;
    }

    @Override
    public boolean matches(RecipeInput recipeInput, @NotNull Level level) {
        for (int slot = 0; slot < recipeInput.size(); slot++) {
            if (ingredients.getFirst()
                    .test(recipeInput.getItem(slot))) return true;
        }
        return false;
    }
}
