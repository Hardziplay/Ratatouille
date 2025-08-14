package org.forsteri.ratatouille.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.SmokingRecipe;
import org.forsteri.ratatouille.compat.jei.category.animations.AnimatedOven;

public class BakeSmokingCategory extends CreateRecipeCategory<SmokingRecipe> {

    protected static final int SCALE = 24;

    private final AnimatedOven oven = new AnimatedOven();
    private final AnimatedBlazeBurner heater = new AnimatedBlazeBurner();

    public BakeSmokingCategory(Info<SmokingRecipe> info) {
        super(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, SmokingRecipe SmokingRecipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder
                .addSlot(RecipeIngredientRole.INPUT, 21, 48)
                .setBackground(getRenderedSlot(), -1, -1)
                .addIngredients(SmokingRecipe.getIngredients().get(0));
        iRecipeLayoutBuilder
                .addSlot(RecipeIngredientRole.OUTPUT, 141, 48)
                .setBackground(getRenderedSlot(), -1, -1)
                .addItemStack(SmokingRecipe.getResultItem(RegistryAccess.EMPTY));
    }

    @Override
    public void draw(SmokingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        PoseStack stack = graphics.pose();
        renderWidgets(graphics, recipe, mouseX, mouseY);
        stack.pushPose();
        stack.translate(75, -15, 0);
        stack.pushPose();
        stack.translate(0, 20, -7);
        heater.withHeat(HeatCondition.HEATED.visualizeAsBlazeBurner())
                .draw(graphics);
        stack.popPose();
        oven.draw(graphics);
        stack.popPose();
    }

    protected void renderWidgets(GuiGraphics graphics, SmokingRecipe recipe, double mouseX, double mouseY) {
        getBlockShadow().render(graphics, 65, 39);
        AllGuiTextures.JEI_LONG_ARROW.render(graphics, 54, 51);
    }

    protected AllGuiTextures getBlockShadow() {
        return AllGuiTextures.JEI_LIGHT;
    }
}