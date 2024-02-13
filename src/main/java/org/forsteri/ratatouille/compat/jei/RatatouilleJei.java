package org.forsteri.ratatouille.compat.jei;

import com.simibubi.create.Create;
import com.simibubi.create.compat.jei.CreateJEI;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.ItemIcon;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.foundation.config.ConfigBase;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import org.forsteri.ratatouille.Ratatouille;
import org.forsteri.ratatouille.compat.jei.category.ThreshingCategory;
import org.forsteri.ratatouille.content.thresher.ThreshingRecipe;
import org.forsteri.ratatouille.entry.CRBlocks;
import org.forsteri.ratatouille.entry.CRRecipeTypes;
import org.forsteri.ratatouille.util.Lang;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@JeiPlugin
@ParametersAreNonnullByDefault
public class RatatouilleJei implements IModPlugin {
    private static final ResourceLocation ID = new ResourceLocation(Ratatouille.MOD_ID, "jei_plugin");
    private final List<CreateRecipeCategory<?>> allCategories = new ArrayList();
    private IIngredientManager ingredientManager;

    public RatatouilleJei() {}

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ID;
    }

    private void loadCategories() {
        this.allCategories.clear();
        CreateRecipeCategory<?>
                milling = builder(ThreshingRecipe.class)
                .addTypedRecipes(CRRecipeTypes.THRESHING)
                .catalyst(CRBlocks.THRESHER::get)
                .emptyBackground(177, 53)
                .build("threshing", ThreshingCategory::new);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        this.loadCategories();
        registration.addRecipeCategories(allCategories.toArray(IRecipeCategory[]::new));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        this.allCategories.forEach((c) -> {
            c.registerCatalysts(registration);
        });
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        this.ingredientManager = registration.getIngredientManager();
        this.allCategories.forEach((c) -> {
            c.registerRecipes(registration);
        });
    }
    private <T extends Recipe<?>> RatatouilleJei.CategoryBuilder<T> builder(Class<? extends T> recipeClass) {
        return new RatatouilleJei.CategoryBuilder(recipeClass);
    }

    public static boolean doInputsMatch(Recipe<?> recipe1, Recipe<?> recipe2) {
        if (!recipe1.getIngredients().isEmpty() && !recipe2.getIngredients().isEmpty()) {
            ItemStack[] matchingStacks = ((Ingredient)recipe1.getIngredients().get(0)).getItems();
            return matchingStacks.length == 0 ? false : ((Ingredient)recipe2.getIngredients().get(0)).test(matchingStacks[0]);
        } else {
            return false;
        }
    }

    public static boolean doOutputsMatch(Recipe<?> recipe1, Recipe<?> recipe2) {
        return ItemStack.isSame(recipe1.getResultItem(), recipe2.getResultItem());
    }

    private class CategoryBuilder<T extends Recipe<?>> {
        private final Class<? extends T> recipeClass;
        private Predicate<CRecipes> predicate = (cRecipes) -> {
            return true;
        };
        private IDrawable background;
        private IDrawable icon;
        private final List<Consumer<List<T>>> recipeListConsumers = new ArrayList();
        private final List<Supplier<? extends ItemStack>> catalysts = new ArrayList();

        public CategoryBuilder(Class<? extends T> recipeClass) {
            this.recipeClass = recipeClass;
        }

        public RatatouilleJei.CategoryBuilder<T> enableIf(Predicate<CRecipes> predicate) {
            this.predicate = predicate;
            return this;
        }

        public RatatouilleJei.CategoryBuilder<T> enableWhen(Function<CRecipes, ConfigBase.ConfigBool> configValue) {
            this.predicate = (c) -> {
                return (Boolean)((ConfigBase.ConfigBool)configValue.apply(c)).get();
            };
            return this;
        }

        public RatatouilleJei.CategoryBuilder<T> addRecipeListConsumer(Consumer<List<T>> consumer) {
            this.recipeListConsumers.add(consumer);
            return this;
        }

        public RatatouilleJei.CategoryBuilder<T> addRecipes(Supplier<Collection<? extends T>> collection) {
            return this.addRecipeListConsumer((recipes) -> {
                recipes.addAll((Collection)collection.get());
            });
        }

        public RatatouilleJei.CategoryBuilder<T> addTypedRecipes(IRecipeTypeInfo recipeTypeEntry) {
            Objects.requireNonNull(recipeTypeEntry);
            return this.addTypedRecipes(recipeTypeEntry::getType);
        }

        public RatatouilleJei.CategoryBuilder<T> addTypedRecipes(Supplier<RecipeType<? extends T>> recipeType) {
            return this.addRecipeListConsumer(recipes -> CreateJEI.<T>consumeTypedRecipes(recipes::add, recipeType.get()));
        }

        public RatatouilleJei.CategoryBuilder<T> catalystStack(Supplier<ItemStack> supplier) {
            this.catalysts.add(supplier);
            return this;
        }

        public RatatouilleJei.CategoryBuilder<T> catalyst(Supplier<ItemLike> supplier) {
            return this.catalystStack(() -> {
                return new ItemStack(((ItemLike)supplier.get()).asItem());
            });
        }

        public RatatouilleJei.CategoryBuilder<T> icon(IDrawable icon) {
            this.icon = icon;
            return this;
        }

        public RatatouilleJei.CategoryBuilder<T> itemIcon(ItemLike item) {
            this.icon(new ItemIcon(() -> {
                return new ItemStack(item);
            }));
            return this;
        }

        public RatatouilleJei.CategoryBuilder<T> doubleItemIcon(ItemLike item1, ItemLike item2) {
            this.icon(new DoubleItemIcon(() -> {
                return new ItemStack(item1);
            }, () -> {
                return new ItemStack(item2);
            }));
            return this;
        }

        public RatatouilleJei.CategoryBuilder<T> background(IDrawable background) {
            this.background = background;
            return this;
        }

        public RatatouilleJei.CategoryBuilder<T> emptyBackground(int width, int height) {
            this.background(new EmptyBackground(width, height));
            return this;
        }

        public CreateRecipeCategory<T> build(String name, CreateRecipeCategory.Factory<T> factory) {
            Supplier recipesSupplier;
            if (this.predicate.test(AllConfigs.server().recipes)) {
                recipesSupplier = () -> {
                    List<T> recipes = new ArrayList();
                    Iterator var2 = this.recipeListConsumers.iterator();

                    while(var2.hasNext()) {
                        Consumer<List<T>> consumer = (Consumer)var2.next();
                        consumer.accept(recipes);
                    }

                    return recipes;
                };
            } else {
                recipesSupplier = () -> {
                    return Collections.emptyList();
                };
            }

            CreateRecipeCategory.Info<T> info = new CreateRecipeCategory.Info(new mezz.jei.api.recipe.RecipeType(Create.asResource(name), this.recipeClass), Lang.translateDirect("recipe." + name, new Object[0]), this.background, this.icon, recipesSupplier, this.catalysts);
            CreateRecipeCategory<T> category = factory.create(info);
            RatatouilleJei.this.allCategories.add(category);
            return category;
        }
    }
}
