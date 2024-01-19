package org.forsteri.ratatouille.data.recipe;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.forsteri.ratatouille.Ratatouille;
import java.util.function.UnaryOperator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class ProcessingRecipeGen extends RataouilleRecipeProvider{
    protected static final List<ProcessingRecipeGen> GENERATORS = new ArrayList<>();
    protected static final int BUCKET = 1000;
    protected static final int BOTTLE = 250;

    public static void registerAll(DataGenerator gen) {
        GENERATORS.add(new ThreshingRecipeGen(gen));

        gen.addProvider(true, new DataProvider() {
            @Override
            public String getName() {
                return "Ratatouille's Processing Recipes";
            }

            @Override
            public void run(CachedOutput dc) throws IOException {
                GENERATORS.forEach(g -> {
                    try {
                        g.run(dc);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    public ProcessingRecipeGen(DataGenerator generator) {
        super(generator);
    }

    protected <T extends ProcessingRecipe<?>> RataouilleRecipeProvider.GeneratedRecipe create(String namespace, Supplier<ItemLike> singleIngredient, UnaryOperator<ProcessingRecipeBuilder<T>> transform) {
        ProcessingRecipeSerializer<T> serializer = this.getSerializer();
        RataouilleRecipeProvider.GeneratedRecipe generatedRecipe = (c) -> {
            ItemLike itemLike = (ItemLike)singleIngredient.get();
            ((ProcessingRecipeBuilder)transform.apply((new ProcessingRecipeBuilder(serializer.getFactory(), new ResourceLocation(namespace, RegisteredObjects.getKeyOrThrow(itemLike.asItem()).getPath()))).withItemIngredients(new Ingredient[]{Ingredient.of(new ItemLike[]{itemLike})}))).build(c);
        };
        this.all.add(generatedRecipe);
        return generatedRecipe;
    }

    <T extends ProcessingRecipe<?>> RataouilleRecipeProvider.GeneratedRecipe create(Supplier<ItemLike> singleIngredient, UnaryOperator<ProcessingRecipeBuilder<T>> transform) {
        return this.create(Ratatouille.MOD_ID, singleIngredient, transform);
    }

    protected <T extends ProcessingRecipe<?>> RataouilleRecipeProvider.GeneratedRecipe createWithDeferredId(Supplier<ResourceLocation> name, UnaryOperator<ProcessingRecipeBuilder<T>> transform) {
        ProcessingRecipeSerializer<T> serializer = this.getSerializer();
        RataouilleRecipeProvider.GeneratedRecipe generatedRecipe = (c) -> {
            ((ProcessingRecipeBuilder)transform.apply(new ProcessingRecipeBuilder(serializer.getFactory(), (ResourceLocation)name.get()))).build(c);
        };
        this.all.add(generatedRecipe);
        return generatedRecipe;
    }

    protected <T extends ProcessingRecipe<?>> RataouilleRecipeProvider.GeneratedRecipe create(ResourceLocation name, UnaryOperator<ProcessingRecipeBuilder<T>> transform) {
        return this.createWithDeferredId(() -> {
            return name;
        }, transform);
    }

    <T extends ProcessingRecipe<?>> RataouilleRecipeProvider.GeneratedRecipe create(String name, UnaryOperator<ProcessingRecipeBuilder<T>> transform) {
        return this.create(new ResourceLocation(Ratatouille.MOD_ID, name), transform);
    }
    protected abstract IRecipeTypeInfo getRecipeType();

    protected <T extends ProcessingRecipe<?>> ProcessingRecipeSerializer<T> getSerializer() {
        return (ProcessingRecipeSerializer)this.getRecipeType().getSerializer();
    }

    protected Supplier<ResourceLocation> idWithSuffix(Supplier<ItemLike> item, String suffix) {
        return () -> {
            ResourceLocation registryName = RegisteredObjects.getKeyOrThrow(((ItemLike)item.get()).asItem());
            String var10000 = registryName.getPath();
            return new ResourceLocation(Ratatouille.MOD_ID, var10000 + suffix);
        };
    }

    @Override
    public String getName() {
        return "Ratatouille's Processing Recipes: " + getRecipeType().getId().getPath();
    }
}
