package org.forsteri.ratatouille.content.frozen_block;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.forsteri.ratatouille.entry.CRItems;
import org.forsteri.ratatouille.entry.CRRecipeTypes;

public class FreezingRecipe extends ProcessingRecipe<RecipeWrapper>  {
    public FreezingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(CRRecipeTypes.FREEZING, params);
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
    public boolean matches(RecipeWrapper pContainer, Level pLevel) {
        for (Ingredient ingredient : ingredients) {
            if (ingredient.test(pContainer.getItem(0))) return true;
        }
        return false;
    }
}
