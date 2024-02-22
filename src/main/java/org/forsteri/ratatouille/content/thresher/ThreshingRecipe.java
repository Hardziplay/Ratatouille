package org.forsteri.ratatouille.content.thresher;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import org.forsteri.ratatouille.entry.CRRecipeTypes;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

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

    public boolean matches(RecipeWrapper inv, @NotNull Level worldIn) {
        return !inv.isEmpty() && this.ingredients.get(0).test(inv.getItem(0));
    }

    protected int getMaxOutputCount() {
        return 4;
    }
}
