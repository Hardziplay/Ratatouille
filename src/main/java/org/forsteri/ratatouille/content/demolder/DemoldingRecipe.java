package org.forsteri.ratatouille.content.demolder;

import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.forsteri.ratatouille.compat.jei.category.AssemblyCategory;
import org.forsteri.ratatouille.entry.CRBlocks;
import org.forsteri.ratatouille.entry.CRRecipeTypes;
import org.forsteri.ratatouille.util.Lang;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class DemoldingRecipe extends StandardProcessingRecipe<SingleRecipeInput> implements IAssemblyRecipe {
    public DemoldingRecipe(ProcessingRecipeParams params) {
        super(CRRecipeTypes.DEMOLDING, params);
    }

    @Override
    public boolean matches(SingleRecipeInput inv, Level worldIn) {
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
    @OnlyIn(Dist.CLIENT)
    public Component getDescriptionForAssembly() {
        return Lang.translateDirect("recipe.assembly.demolding");
    }

    public void addRequiredMachines(Set<ItemLike> list) {
        list.add(CRBlocks.MECHANICAL_DEMOLDER.get());
    }

    @Override
    public void addAssemblyIngredients(List<Ingredient> list) {
    }

    @Override
    public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
        return () -> AssemblyCategory.AssemblyDemolding::new;
    }

}
