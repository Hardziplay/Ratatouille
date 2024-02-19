package org.forsteri.ratatouille.content.squeeze_basin;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.forsteri.ratatouille.entry.CRRecipeTypes;

public class SqueezingRecipe extends ProcessingRecipe<RecipeWrapper> {
    public SqueezingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(CRRecipeTypes.SQUEEZING, params);
    }

    protected int getMaxInputCount() {
        return 1;
    }

    protected boolean canSpecifyDuration() {
        return true;
    }

    @Override
    protected int getMaxOutputCount() {
        return 1;
    }

    @Override
    public boolean matches(RecipeWrapper recipeWrapper, Level level) {
        return recipeWrapper.isEmpty() ? false : ((Ingredient)this.ingredients.get(0)).test(recipeWrapper.getItem(0));
    }
}
