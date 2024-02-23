package org.forsteri.ratatouille.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import org.forsteri.ratatouille.compat.jei.category.animations.AnimatedSqueeze;
import org.forsteri.ratatouille.compat.jei.category.animations.AnimatedThresher;
import org.forsteri.ratatouille.content.squeeze_basin.SqueezingRecipe;
import org.forsteri.ratatouille.content.thresher.ThreshingRecipe;

import java.util.List;

public class SqueezingCategory extends CreateRecipeCategory<SqueezingRecipe> {
    private final AnimatedSqueeze squeeze = new AnimatedSqueeze();

    public SqueezingCategory(Info<SqueezingRecipe> info) {
        super(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SqueezingRecipe recipe, IFocusGroup focuses) {
        builder
                .addSlot(RecipeIngredientRole.INPUT, 27, 51)
                .setBackground(getRenderedSlot(), -1, -1)
                .addIngredients(recipe.getIngredients().get(0));

        List<ProcessingOutput> results = recipe.getRollableResults();
        int i = 0;
        for (ProcessingOutput output : results) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 131 + 19 * i, 50)
                    .setBackground(getRenderedSlot(output), -1, -1)
                    .addItemStack(output.getStack())
                    .addTooltipCallback(addStochasticTooltip(output));
            i++;
        }
    }

    @Override
    public void draw(SqueezingRecipe recipe, IRecipeSlotsView iRecipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        AllGuiTextures.JEI_SHADOW.render(matrixStack, 61, 41);
        AllGuiTextures.JEI_LONG_ARROW.render(matrixStack, 52, 54);

        squeeze.draw(matrixStack, getBackground().getWidth() / 2 - 17, 22);
    }
}
