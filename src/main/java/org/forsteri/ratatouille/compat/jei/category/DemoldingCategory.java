package org.forsteri.ratatouille.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import org.forsteri.ratatouille.compat.jei.category.animations.AnimatedDemolder;
import org.forsteri.ratatouille.content.demolder.DemoldingRecipe;
import org.forsteri.ratatouille.entry.CRTags;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.crafting.Ingredient;

public class DemoldingCategory extends CreateRecipeCategory<DemoldingRecipe> {
    private final AnimatedDemolder demolder = new AnimatedDemolder();

    public DemoldingCategory(Info<DemoldingRecipe> info) {
        super(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DemoldingRecipe recipe, IFocusGroup focuses) {
        builder
                .addSlot(RecipeIngredientRole.INPUT, 27, 51)
                .setBackground(getRenderedSlot(), -1, -1)
                .addIngredients(recipe.getIngredients().get(0));

        List<ProcessingOutput> results = recipe.getRollableResults();
        int i = 0;
        for (ProcessingOutput output : results) {
            if (output.getStack().is(CRTags.MOLD)) {
                builder.addSlot(RecipeIngredientRole.OUTPUT, 104, 1)
                        .setBackground(getRenderedSlot(output), -1, -1)
                        .addItemStack(output.getStack())
                        .addRichTooltipCallback(addStochasticTooltip(output));
            } else {
                builder.addSlot(RecipeIngredientRole.OUTPUT, 131, 50)
                        .setBackground(getRenderedSlot(output), -1, -1)
                        .addItemStack(output.getStack())
                        .addRichTooltipCallback(addStochasticTooltip(output));

            }
            i++;
        }
    }

    @Override
    public void draw(DemoldingRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_SHADOW.render(graphics, 61, 41);
        AllGuiTextures.JEI_LONG_ARROW.render(graphics, 52, 54);

        demolder.draw(graphics, getBackground().getWidth() / 2 - 17, 22);
    }

}
