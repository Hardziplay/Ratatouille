package org.forsteri.ratatouille.content.thresher;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import org.forsteri.ratatouille.entry.CRRecipeTypes;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ThreshingRecipe extends StandardProcessingRecipe<RecipeInput> {
    public ThreshingRecipe(ProcessingRecipeParams params) {
        super(CRRecipeTypes.THRESHING, params);
    }

    protected int getMaxInputCount() {
        return 1;
    }

    protected int getMaxOutputCount() {
        return 4;
    }

    protected boolean canSpecifyDuration() {
        return true;
    }

    @Override
    public boolean matches(RecipeInput inv, Level worldIn) {
        if (inv.isEmpty())
            return false;
        return ingredients.getFirst()
                .test(inv.getItem(0));
    }
}
