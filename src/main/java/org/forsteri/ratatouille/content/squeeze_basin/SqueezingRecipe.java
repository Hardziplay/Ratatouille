package org.forsteri.ratatouille.content.squeeze_basin;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.forsteri.ratatouille.entry.CRItems;
import org.forsteri.ratatouille.entry.CRRecipeTypes;
import org.jetbrains.annotations.NotNull;

public class SqueezingRecipe extends StandardProcessingRecipe<SqueezeBasinInventory> {
    public SqueezingRecipe(ProcessingRecipeParams params) {
        super(CRRecipeTypes.SQUEEZING, params);
    }

    public boolean match(@NotNull SqueezeBasinBlockEntity be) {
        if (be.getOperator().isEmpty())
            return false;

        IFluidHandler inputTank = be.fluidCapability;

        if (be.inputInventory == null || inputTank == null)
            return false;

        boolean useCasing = useCasing();
        if (useCasing != be.hasCasing())
            return false;
        if (useCasing && ingredients.size() == 1) {
            if (fluidIngredients.isEmpty())
                return true;
            return fluidIngredients.getFirst().test(inputTank.getFluidInTank(0));
        } else {
            for (Ingredient ingredient : ingredients) {
                if (ingredient.test(be.inputInventory.getItem(0))) {
                    if (fluidIngredients.isEmpty())
                        return true;
                    return fluidIngredients.getFirst().test(inputTank.getFluidInTank(0));
                }
            }
        }
        return false;
    }

    public boolean useCasing() {
        for (Ingredient ingredient : ingredients) {
            if (ingredient.test(CRItems.SAUSAGE_CASING.asStack())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected int getMaxInputCount() {
        return 2;
    }

    @Override
    protected int getMaxOutputCount() {
        return 1;
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
    public boolean matches(@NotNull SqueezeBasinInventory smartInventory, @NotNull Level level) {
        if (!fluidIngredients.isEmpty()) {
            if (smartInventory.blockEntity == null) return false;
            IFluidHandler fluidHandler = smartInventory.blockEntity.fluidCapability;
            if (fluidHandler == null) return false;

            boolean result = false;
            for (FluidIngredient ingredient : fluidIngredients) {
                if (ingredient.test(fluidHandler.getFluidInTank(0))) {
                    result = true;
                    break;
                }
            }
            if (!result) return false;
        }

        boolean useCasing = useCasing();
        for (Ingredient ingredient : ingredients) {
            if (ingredient.test(CRItems.SAUSAGE_CASING.asStack())) {
                if (smartInventory.blockEntity == null) return false;
                if (!smartInventory.blockEntity.hasCasing()) return false;
            } else if (ingredient.test(smartInventory.getItem(0))) {
                return true;
            }
        }

        if (useCasing && ingredients.size() == 1) {
            return true;
        }

        return false;
    }
}
