package org.forsteri.ratatouille.content.squeeze_basin;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.recipe.DummyCraftingContainer;
import net.createmod.catnip.data.Iterate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.forsteri.ratatouille.entry.CRItems;
import org.forsteri.ratatouille.entry.CRRecipeTypes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SqueezingRecipe extends StandardProcessingRecipe<RecipeInput> {
    public SqueezingRecipe(ProcessingRecipeParams params) {
        super(CRRecipeTypes.SQUEEZING, params);
    }

    public static boolean apply(SqueezeBasinBlockEntity basin, SqueezingRecipe recipe) {
        return apply(basin, recipe, false);
    }

    private static boolean apply(SqueezeBasinBlockEntity basin, SqueezingRecipe recipe, boolean test) {
        if (basin.getLevel() == null) return false;

        IItemHandler availableItems = basin.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, basin.getBlockPos(), null);
        IFluidHandler availableFluids = basin.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, basin.getBlockPos(), null);

        if (availableItems == null || availableFluids == null) return false;

        List<ItemStack> recipeOutputItems = new ArrayList<>();
        List<Ingredient> ingredients = new LinkedList<>(recipe.getIngredients());
        List<FluidIngredient> fluidIngredients = recipe.getFluidIngredients();

        if (recipe.useCasing() != basin.hasCasing()) return false;

        for (boolean simulate : Iterate.trueAndFalse) {

            if (!simulate && test)
                return true;

            int[] extractedItemsFromSlot = new int[availableItems.getSlots()];
            int[] extractedFluidsFromTank = new int[availableFluids.getTanks()];

            Ingredients:
            for (Ingredient ingredient : ingredients) {
                for (int slot = 0; slot < availableItems.getSlots(); slot++) {
                    if (simulate && availableItems.getStackInSlot(slot)
                            .getCount() <= extractedItemsFromSlot[slot])
                        continue;
                    ItemStack extracted = availableItems.extractItem(slot, 1, true);
                    if (!ingredient.test(extracted))
                        continue;
                    if (!simulate)
                        availableItems.extractItem(slot, 1, false);
                    extractedItemsFromSlot[slot]++;
                    continue Ingredients;
                }

                return false;
            }

            boolean fluidsAffected = false;
            FluidIngredients:
            for (FluidIngredient fluidIngredient : fluidIngredients) {
                int amountRequired = fluidIngredient.getRequiredAmount();

                for (int tank = 0; tank < availableFluids.getTanks(); tank++) {
                    FluidStack fluidStack = availableFluids.getFluidInTank(tank);
                    if (simulate && fluidStack.getAmount() <= extractedFluidsFromTank[tank])
                        continue;
                    if (!fluidIngredient.test(fluidStack))
                        continue;
                    int drainedAmount = Math.min(amountRequired, fluidStack.getAmount());
                    if (!simulate) {
                        fluidStack.shrink(drainedAmount);
                        fluidsAffected = true;
                    }
                    amountRequired -= drainedAmount;
                    if (amountRequired != 0)
                        continue;
                    extractedFluidsFromTank[tank] += drainedAmount;
                    continue FluidIngredients;
                }

                return false;
            }

            if (fluidsAffected) {
                basin.getBehaviour(SmartFluidTankBehaviour.INPUT)
                        .forEach(SmartFluidTankBehaviour.TankSegment::onFluidStackChanged);
            }

            if (simulate) {
                CraftingInput remainderInput = new DummyCraftingContainer(availableItems, extractedItemsFromSlot)
                        .asCraftInput();

                recipeOutputItems.addAll(recipe.rollResults());

                for (ItemStack stack : recipe.getRemainingItems(remainderInput))
                    if (!stack.isEmpty())
                        recipeOutputItems.add(stack);
            }

            if (!basin.acceptOutputs(recipeOutputItems, simulate))
                return false;
        }

        return true;
    }

    public boolean useCasing() {
        for (Ingredient ingredient : ingredients) {
            if (ingredient.test(CRItems.SAUSAGE_CASING.asStack())) {
                return true;
            }
        }
        return false;
    }

    public static boolean match(SqueezeBasinBlockEntity basin, SqueezingRecipe recipe) {
        return apply(basin, recipe, true);
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
    public boolean matches(@NotNull RecipeInput input, @Nonnull Level worldIn) {
        return false;
    }
}
