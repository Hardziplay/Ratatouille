package org.forsteri.ratatouille.data.recipe;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.forsteri.ratatouille.entry.CRRecipeTypes;

public class ThreshingRecipeGen extends ProcessingRecipeGen {

    public ThreshingRecipeGen(DataGenerator p_i48262_1_) {
        super(p_i48262_1_);
    }

    @Override
    protected CRRecipeTypes getRecipeType() {
        return CRRecipeTypes.THRESHING;
    }

}