package org.forsteri.ratatouille.data.recipe;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class RataouilleRecipeProvider extends RecipeProvider {
    protected final List<GeneratedRecipe> all = new ArrayList<>();

    public RataouilleRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> p_176532_) {
        all.forEach(c -> c.register(p_176532_));
    }

    protected GeneratedRecipe register(GeneratedRecipe recipe) {
        all.add(recipe);
        return recipe;
    }

    @FunctionalInterface
    public interface GeneratedRecipe {
        void register(Consumer<FinishedRecipe> consumer);
    }

    protected static class Marker {
    }

    protected static class I {

    }
}
