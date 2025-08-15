package org.forsteri.ratatouille.data.recipe.create;

import com.simibubi.create.api.data.recipe.FillingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import org.forsteri.ratatouille.Ratatouille;
import org.forsteri.ratatouille.entry.CRFluids;
import org.forsteri.ratatouille.entry.CRItems;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class RatatouilleFillingRecipeGen extends FillingRecipeGen {
    GeneratedRecipe
            CAKE = create("cake", b -> b
            .require(CRItems.CAKE_BASE.get())
            .require(Tags.Fluids.MILK, 1000)
            .output(Items.CAKE)
    ),

    CAKE_MOLD_FILLED = create("cake_mold_filled", b -> b
            .require(CRItems.CAKE_MOLD.get())
            .require(CRFluids.CAKE_BATTER.get(), 500)
            .output(CRItems.CAKE_MOLD_FILLED.get())
    ),

    CHOCOLATE_CAKE = create("chocolate_cake", b -> b
            .require(CRItems.CAKE_BASE.get())
            .require(com.simibubi.create.AllFluids.CHOCOLATE.get(), 1000)
            .output(ResourceLocation.parse("neapolitan:chocolate_cake"))
            .whenModLoaded("neapolitan")
    ),

    CHOCOLATE_MOLD_FILLED = create("chocolate_mold_filled", b -> b
            .require(CRItems.CHOCOLATE_MOLD.get())
            .require(com.simibubi.create.AllFluids.CHOCOLATE.get(), 250)
            .output(CRItems.CHOCOLATE_MOLD_FILLED.get())
    ),

    COMPOST_TEA_BOTTLE = create("compost_tea_bottle", b -> b
            .require(Items.GLASS_BOTTLE)
            .require(CRFluids.COMPOST_TEA.get(), 100)
            .output(CRItems.COMPOST_TEA_BOTTLE.get())
    ),

    HONEY_CAKE = create("honey_cake", b -> b
            .require(CRItems.CAKE_BASE.get())
            .require(Tags.Fluids.HONEY, 500) // forge:honey 流体标签
            .output(ResourceLocation.parse("createaddition:honey_cake"))
            .whenModLoaded("createaddition")
    ),

    MELON_POPSICLE_MOLD_FILLED = create("melon_popsicle_mold_filled", b -> b
            .require(CRFluids.MELON_JUICE_FLUID.get(), 100)
            .require(CRItems.POPSICLE_MOLD.get())
            .output(CRItems.MELON_POPSICLE_MOLD_FILLED.get())
    );

    public RatatouilleFillingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Ratatouille.MOD_ID);
    }
}
