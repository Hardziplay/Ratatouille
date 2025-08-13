package org.forsteri.ratatouille.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.item.ItemHelper;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.createmod.catnip.data.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.commons.lang3.mutable.MutableInt;
import org.forsteri.ratatouille.compat.jei.category.animations.AnimatedCompostTower;
import org.forsteri.ratatouille.content.compost_tower.CompostingRecipe;

import java.util.ArrayList;
import java.util.List;

public class CompostingCategory extends CreateRecipeCategory<CompostingRecipe> {

    private final AnimatedCompostTower tower = new AnimatedCompostTower();
    private final AnimatedBlazeBurner heater = new AnimatedBlazeBurner();

    public CompostingCategory(Info<CompostingRecipe> info) {
        super(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CompostingRecipe recipe, IFocusGroup focuses) {
        List<Pair<Ingredient, MutableInt>> condensed = ItemHelper.condenseIngredients(recipe.getIngredients());
        int size = condensed.size();
        int xOffset = size < 3 ? (3 - size) * 19 / 2 : 0;

        int i = 0;
        for (Pair<Ingredient, MutableInt> pair : condensed) {
            List<ItemStack> stacks = new ArrayList<>();
            for (ItemStack is : pair.getFirst().getItems()) {
                ItemStack copy = is.copy();
                copy.setCount(pair.getSecond().getValue());
                stacks.add(copy);
            }
            builder.addSlot(RecipeIngredientRole.INPUT, 5 + xOffset + (i % 3) * 19, 51 - (i / 3) * 19)
                    .setBackground(getRenderedSlot(), -1, -1)
                    .addItemStacks(stacks);
            i++;
        }

        List<FluidStack> outputs = recipe.getFluidResults();
        int outSize = outputs.size();
        int j = 0;
        for (FluidStack fluid : outputs) {
            int x = 142 - (outSize % 2 != 0 && j == outSize - 1 ? 0 : j % 2 == 0 ? 10 : -9);
            int y = -19 * (j / 2) + 51;
            addFluidOutputSlot(builder, x, y, fluid);
            j++;
        }
    }

    private void addFluidOutputSlot(IRecipeLayoutBuilder builder, int x, int y, FluidStack stack) {
        builder.addSlot(RecipeIngredientRole.OUTPUT, x, y)
                .setBackground(getRenderedSlot(), -1, -1)
                .setFluidRenderer(stack.getAmount(), false, 16, 16)
                .addIngredient(NeoForgeTypes.FLUID_STACK, stack);
    }

    @Override
    public void draw(CompostingRecipe recipe, IRecipeSlotsView view, GuiGraphics g, double mouseX, double mouseY) {
        PoseStack stack = g.pose();

        renderWidgets(g, recipe, mouseX, mouseY);
        stack.pushPose();
        stack.translate(75, -15, 0);
        stack.pushPose();
        stack.translate(0, 20, -7);
        heater.withHeat(HeatCondition.HEATED.visualizeAsBlazeBurner())
                .draw(g);
        stack.popPose();
        tower.draw(g);
        stack.popPose();
    }

    protected void renderWidgets(GuiGraphics graphics, CompostingRecipe recipe, double mouseX, double mouseY) {
        getBlockShadow().render(graphics, 65, 39);
        AllGuiTextures.JEI_LONG_ARROW.render(graphics, 54, 51);
    }

    protected AllGuiTextures getBlockShadow() {
        return AllGuiTextures.JEI_LIGHT;
    }
}
