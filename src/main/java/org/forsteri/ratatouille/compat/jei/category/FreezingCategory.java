package org.forsteri.ratatouille.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import org.forsteri.ratatouille.content.frozen_block.FreezingRecipe;
import org.forsteri.ratatouille.entry.CRBlocks;

public class FreezingCategory extends CreateRecipeCategory<FreezingRecipe> {

    protected static final int SCALE = 24;

    public FreezingCategory(Info<FreezingRecipe> info) {
        super(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, FreezingRecipe smokingRecipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder
                .addSlot(RecipeIngredientRole.INPUT, 21, 48)
                .setBackground(getRenderedSlot(), -1, -1)
                .addIngredients(smokingRecipe.getIngredients().get(0));
        iRecipeLayoutBuilder
                .addSlot(RecipeIngredientRole.OUTPUT, 141, 48)
                .setBackground(getRenderedSlot(), -1, -1)
                .addItemStack(smokingRecipe.getResultItem());
    }

    protected AllGuiTextures getBlockShadow() {
        return AllGuiTextures.JEI_LIGHT;
    }

    protected void renderWidgets(PoseStack matrixStack, FreezingRecipe recipe, double mouseX, double mouseY) {
        getBlockShadow().render(matrixStack, 65, 39);
        AllGuiTextures.JEI_LONG_ARROW.render(matrixStack, 54, 51);
    }

    @Override
    public void draw(FreezingRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        renderWidgets(stack, recipe, mouseX, mouseY);
        stack.pushPose();
        stack.translate(56, 33, 0);
        stack.mulPose(Vector3f.XP.rotationDegrees(-12.5f));
        stack.mulPose(Vector3f.YP.rotationDegrees(22.5f));
        GuiGameElement.of(CRBlocks.FROZEN_BLOCK.getDefaultState())
                .scale(SCALE)
                .atLocal(0, 0, 2)
                .lighting(AnimatedKinetics.DEFAULT_LIGHTING)
                .render(stack);
        stack.popPose();
    }
}
