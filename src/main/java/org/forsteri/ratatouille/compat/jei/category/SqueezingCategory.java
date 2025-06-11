package org.forsteri.ratatouille.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.item.ItemHelper;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.createmod.catnip.data.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.mutable.MutableInt;
import org.forsteri.ratatouille.compat.jei.category.animations.AnimatedSqueeze;
import org.forsteri.ratatouille.compat.jei.category.animations.AnimatedThresher;
import org.forsteri.ratatouille.content.squeeze_basin.SqueezingRecipe;
import org.forsteri.ratatouille.content.thresher.ThreshingRecipe;
import org.forsteri.ratatouille.util.Lang;

import java.util.ArrayList;
import java.util.List;

public class SqueezingCategory extends CreateRecipeCategory<SqueezingRecipe> {
    private final AnimatedSqueeze squeeze = new AnimatedSqueeze();

    public SqueezingCategory(Info<SqueezingRecipe> info) {
        super(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SqueezingRecipe recipe, IFocusGroup focuses) {
        List<Pair<Ingredient, MutableInt>> condensedIngredients = ItemHelper.condenseIngredients(recipe.getIngredients());

        int size = condensedIngredients.size() + recipe.getFluidIngredients().size();
        int xOffset = size < 3 ? (3 - size) * 19 / 2 : 0;
        int i = 0;

        for (Pair<Ingredient, MutableInt> pair : condensedIngredients) {
            List<ItemStack> stacks = new ArrayList<>();
            for (ItemStack itemStack : pair.getFirst().getItems()) {
                ItemStack copy = itemStack.copy();
                copy.setCount(pair.getSecond().getValue());
                stacks.add(copy);
            }

            builder
                    .addSlot(RecipeIngredientRole.INPUT, 17 + xOffset + (i % 3) * 19, 51 - (i / 3) * 19)
                    .setBackground(getRenderedSlot(), -1, -1)
                    .addItemStacks(stacks);
            i++;
        }
        for(FluidIngredient fluidIngredient : recipe.getFluidIngredients()) {
            addFluidSlot(builder, 17 + xOffset + i % 3 * 19, 51 - i / 3 * 19, fluidIngredient);
            ++i;
        }

        size = recipe.getRollableResults().size() + recipe.getFluidResults().size();
        i = 0;

        for(ProcessingOutput result : recipe.getRollableResults()) {
            int xPosition = 142 - (size % 2 != 0 && i == size - 1 ? 0 : (i % 2 == 0 ? 10 : -9));
            int yPosition = -19 * (i / 2) + 51;
            ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.OUTPUT, xPosition, yPosition).setBackground(getRenderedSlot(result), -1, -1).addItemStack(result.getStack())).addRichTooltipCallback(addStochasticTooltip(result));
            ++i;
        }

        for(FluidStack fluidResult : recipe.getFluidResults()) {
            int xPosition = 142 - (size % 2 != 0 && i == size - 1 ? 0 : (i % 2 == 0 ? 10 : -9));
            int yPosition = -19 * (i / 2) + 51;
            addFluidSlot(builder, xPosition, yPosition, fluidResult);
            ++i;
        }
    }

    @Override
    public void draw(SqueezingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        int vRows = (1 + recipe.getFluidResults().size() + recipe.getRollableResults().size()) / 2;
        if (vRows <= 2)
            AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 136, -19 * (vRows - 1) + 32);
        squeeze.setUseCasing(recipe.useCasing());
        squeeze.draw(graphics, getBackground().getWidth() / 2 + 3, 34);
        if (recipe.useCasing())
            graphics.drawString(Minecraft.getInstance().font, Lang.translateDirect("recipe.sausage_casing_requirement"), 9, 86, 0xffffff, false);
    }
}
