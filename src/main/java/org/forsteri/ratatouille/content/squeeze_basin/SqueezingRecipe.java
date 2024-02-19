package org.forsteri.ratatouille.content.squeeze_basin;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.forsteri.ratatouille.entry.CRRecipeTypes;

public class SqueezingRecipe extends ProcessingRecipe<SmartInventory> {
    public SqueezingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(CRRecipeTypes.SQUEEZING, params);
    }

    @Override
    protected int getMaxInputCount() {
        return 2;
    }

    @Override
    protected boolean canSpecifyDuration() {
        return true;
    }

    @Override
    protected int getMaxFluidInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 1;
    }

    @Override
    public boolean matches(SmartInventory smartInventory, Level level) {
        return false;
    }
}
