package org.forsteri.ratatouille.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import org.forsteri.ratatouille.compat.jei.category.animations.AnimatedDemolder;

public abstract class AssemblyCategory{

    public static class AssemblyDemolding extends SequencedAssemblySubCategory {
        AnimatedDemolder demolder;
        public AssemblyDemolding() {
            super(25);
            demolder = new AnimatedDemolder();
        }
        @Override
        public void draw(SequencedRecipe<?> recipe, PoseStack ms, double mouseX, double mouseY, int index) {
            demolder.offset = index;
            ms.pushPose();
            ms.translate(-5, 50, 0);
            ms.scale(.6f, .6f, .6f);
            demolder.draw(ms, getWidth() / 2, 0);
            ms.popPose();
        }

    }
}
