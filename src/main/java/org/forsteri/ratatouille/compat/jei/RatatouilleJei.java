package org.forsteri.ratatouille.compat.jei;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmokingRecipe;
import org.forsteri.ratatouille.Ratatouille;
import org.forsteri.ratatouille.compat.jei.category.*;
import org.forsteri.ratatouille.content.compost_tower.CompostingRecipe;
import org.forsteri.ratatouille.content.demolder.DemoldingRecipe;
import org.forsteri.ratatouille.content.frozen_block.FreezingRecipe;
import org.forsteri.ratatouille.content.squeeze_basin.SqueezingRecipe;
import org.forsteri.ratatouille.content.thresher.ThreshingRecipe;
import org.forsteri.ratatouille.entry.CRBlocks;
import org.forsteri.ratatouille.entry.CRRecipeTypes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@JeiPlugin
@ParametersAreNonnullByDefault
public class RatatouilleJei implements IModPlugin {
    private static final ResourceLocation ID = Ratatouille.asResource("jei_plugin");
    private final List<CreateRecipeCategory<?>> allCategories = new ArrayList<>();
    private IIngredientManager ingredientManager;

    public RatatouilleJei() {
    }

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        allCategories.clear();
        final CreateRecipeCategory<?> threshing = builder(ThreshingRecipe.class)
                .addTypedRecipes(CRRecipeTypes.THRESHING::getType)
                .catalyst(CRBlocks.THRESHER::get)
                .itemIcon(CRBlocks.THRESHER.get())
                .emptyBackground(177, 53)
                .build("threshing", ThreshingCategory::new);

        final CreateRecipeCategory<?> squeezing = builder(SqueezingRecipe.class)
                .addTypedRecipes(CRRecipeTypes.SQUEEZING::getType)
                .catalyst(AllBlocks.MECHANICAL_PRESS::get)
                .catalyst(CRBlocks.SQUEEZE_BASIN::get)
                .doubleItemIcon(AllBlocks.MECHANICAL_PRESS.get(), CRBlocks.SQUEEZE_BASIN.get())
                .emptyBackground(177, 103)
                .build("squeezing", SqueezingCategory::new);

        final CreateRecipeCategory<?> demolding = builder(DemoldingRecipe.class)
                .addTypedRecipes(CRRecipeTypes.DEMOLDING::getType)
                .catalyst(CRBlocks.MECHANICAL_DEMOLDER::get)
                .emptyBackground(177, 70)
                .build("demolding", DemoldingCategory::new);

        final CreateRecipeCategory<?> baking = builder(SmokingRecipe.class)
                .addTypedRecipes(() -> RecipeType.SMOKING)
                .catalyst(CRBlocks.OVEN::get)
                .doubleItemIcon(CRBlocks.OVEN.get(), Items.CAMPFIRE)
                .emptyBackground(178, 72)
                .build("baking", BakingCategory::new);

        final CreateRecipeCategory<?> freezing = builder(FreezingRecipe.class)
                .addTypedRecipes(CRRecipeTypes.FREEZING::getType)
                .catalyst(CRBlocks.FROZEN_BLOCK::get)
                .itemIcon(CRBlocks.FROZEN_BLOCK.get())
                .emptyBackground(178, 72)
                .build("freezing", FreezingCategory::new);

        final CreateRecipeCategory<?> composting = builder(CompostingRecipe.class)
                .addTypedRecipes(CRRecipeTypes.COMPOSTING::getType)
                .catalyst(CRBlocks.COMPOST_TOWER_BLOCK::get)
                .itemIcon(CRBlocks.COMPOST_TOWER_BLOCK.get())
                .emptyBackground(178, 72)
                .build("composting", CompostingCategory::new);
        allCategories.forEach(registration::addRecipeCategories);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ingredientManager = registration.getIngredientManager();
        allCategories.forEach(c -> c.registerRecipes(registration));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        allCategories.forEach(c -> c.registerCatalysts(registration));
    }

    private <T extends Recipe<?>> CategoryBuilder<T> builder(Class<? extends T> recipeClass) {
        return new CategoryBuilder<>(recipeClass);
    }

    private class CategoryBuilder<T extends Recipe<?>> extends CreateRecipeCategory.Builder<T> {
        public CategoryBuilder(Class<? extends T> recipeClass) {
            super(recipeClass);
        }

        @Override
        public @NotNull CreateRecipeCategory<T> build(ResourceLocation id, CreateRecipeCategory.Factory<T> factory) {
            CreateRecipeCategory<T> category = super.build(id, factory);
            allCategories.add(category);
            return category;
        }
    }
}
