package org.forsteri.ratatouille.content.thresher;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import org.forsteri.ratatouille.data.recipe.RataouilleRecipeProvider.GeneratedRecipe;
import org.forsteri.ratatouille.entry.CRRecipeTypes;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class ThreshingRecipe extends ProcessingRecipe<RecipeWrapper> {
    public ThreshingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(CRRecipeTypes.THRESHING, params);
    }

    protected int getMaxInputCount() {
        return 1;
    }

    protected boolean canSpecifyDuration() {
        return true;
    }

    public boolean matches(RecipeWrapper inv, Level worldIn) {
        return inv.isEmpty() ? false : ((Ingredient)this.ingredients.get(0)).test(inv.getItem(0));
    }

    protected int getMaxOutputCount() {
        return 4;
    }
}
