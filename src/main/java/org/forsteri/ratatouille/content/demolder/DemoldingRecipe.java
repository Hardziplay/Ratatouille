package org.forsteri.ratatouille.content.demolder;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.forsteri.ratatouille.compat.jei.category.AssemblyCategory;
import org.forsteri.ratatouille.entry.CRBlocks;
import org.forsteri.ratatouille.entry.CRRecipeTypes;
import org.forsteri.ratatouille.util.Lang;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class DemoldingRecipe extends ProcessingRecipe<RecipeWrapper> implements IAssemblyRecipe {
    public DemoldingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(CRRecipeTypes.DEMOLDING, params);
    }

    @Override
    public boolean matches(RecipeWrapper inv, Level worldIn) {
        if (inv.isEmpty())
            return false;
        return ingredients.get(0)
                .test(inv.getItem(0));
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 2;
    }

    @Override
    public void addAssemblyIngredients(List<Ingredient> list) {}

    @Override
    @OnlyIn(Dist.CLIENT)
    public Component getDescriptionForAssembly() {
        return Lang.translateDirect("recipe.assembly.demolding");
    }

    public void addRequiredMachines(Set<ItemLike> list) {
        list.add(CRBlocks.MECHANICAL_DEMOLDER.get());
    }

    @Override
    public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
        return () -> AssemblyCategory.AssemblyDemolding::new;
    }

}
