package org.forsteri.ratatouille.content.squeeze_basin;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.item.SmartInventory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.forsteri.ratatouille.entry.CRItems;
import org.forsteri.ratatouille.entry.CRRecipeTypes;
import org.jetbrains.annotations.NotNull;

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
    public boolean matches(@NotNull SmartInventory smartInventory, @NotNull Level level) {
        boolean useCasing = false;
        for (Ingredient ingredient : ingredients) {
            if (ingredient.test(CRItems.SAUSAGE_CASING.asStack())) {
                useCasing = true;
            } else if (ingredient.test(smartInventory.getItem(0))) {
                return true;
            }
        }
        return useCasing;
    }

    public boolean match(@NotNull SqueezeBasinBlockEntity be, boolean hasCasing) {
        if (be.getOperator().isEmpty())
            return false;

        IFluidHandler inputTank = be.getCapability(ForgeCapabilities.FLUID_HANDLER)
                .orElse(null);

        if (be.inputInventory == null || inputTank == null)
            return false;

        boolean useCasing = false;
        for (Ingredient ingredient : ingredients) {
            if (ingredient.test(CRItems.SAUSAGE_CASING.asStack())) {
                useCasing = true;
                break;
            }
        }
        if (useCasing != hasCasing)
            return false;
        if (useCasing && ingredients.size() == 1) {
            if (fluidIngredients.isEmpty())
                return true;
            return fluidIngredients.get(0).test(inputTank.getFluidInTank(0));
        } else {
            for (Ingredient ingredient : ingredients) {
                if (ingredient.test(be.inputInventory.getItem(0))) {
                    if (fluidIngredients.isEmpty())
                        return true;
                    return fluidIngredients.get(0).test(inputTank.getFluidInTank(0));
                }
            }
        }
        return false;
    }
}
