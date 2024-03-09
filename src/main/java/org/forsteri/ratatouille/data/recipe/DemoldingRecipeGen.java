package org.forsteri.ratatouille.data.recipe;

import com.simibubi.create.AllItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import org.forsteri.ratatouille.entry.CRItems;
import org.forsteri.ratatouille.entry.CRRecipeTypes;

public class DemoldingRecipeGen extends ProcessingRecipeGen{
    GeneratedRecipe
        BAR_OF_CHOCOLATE = this.create(
            CRItems.CHOCOLATE_MOLD_SOLID::get,
            b -> b.output(AllItems.BAR_OF_CHOCOLATE.get())
                    .output(CRItems.CHOCOLATE_MOLD.get())
        ),
        CAKE_BASE = this.create(
            CRItems.CAKE_MOLD_BAKED::get,
            b -> b.output(CRItems.CAKE_BASE.get())
                    .output(CRItems.CAKE_MOLD.get())
        );
    public DemoldingRecipeGen(PackOutput generator) {
        super(generator);
    }

    @Override
    protected CRRecipeTypes getRecipeType() {
        return CRRecipeTypes.DEMOLDING;
    }
}
