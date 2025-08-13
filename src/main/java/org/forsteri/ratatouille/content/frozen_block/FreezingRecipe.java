package org.forsteri.ratatouille.content.frozen_block;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.forsteri.ratatouille.entry.CRRecipeTypes;
import org.jetbrains.annotations.NotNull;

public class FreezingRecipe extends StandardProcessingRecipe<SingleRecipeInput> {
    public FreezingRecipe(ProcessingRecipeParams params) {
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
    public boolean matches(@NotNull SingleRecipeInput singleRecipeInput, @NotNull Level level) {
        for (Ingredient ingredient : ingredients) {
            if (ingredient.test(singleRecipeInput.getItem(0))) return true;
        }
        return false;
    }
}
