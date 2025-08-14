package org.forsteri.ratatouille.data.recipe;

import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.forsteri.ratatouille.Ratatouille;

public class RatatouilleRegistrateTags {
    public static void addGenerators() {
        Ratatouille.REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, RatatouilleRegistrateTags::genBlockTags);
        Ratatouille.REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, RatatouilleRegistrateTags::genItemTags);
        Ratatouille.REGISTRATE.addDataGenerator(ProviderType.FLUID_TAGS, RatatouilleRegistrateTags::genFluidTags);
        Ratatouille.REGISTRATE.addDataGenerator(ProviderType.ENTITY_TAGS, RatatouilleRegistrateTags::genEntityTags);
    }

    private static void genBlockTags(RegistrateTagsProvider<Block> provIn) {

    }

    private static void genItemTags(RegistrateTagsProvider<Item> provIn) {

    }

    private static void genFluidTags(RegistrateTagsProvider<Fluid> provIn) {

    }

    private static void genEntityTags(RegistrateTagsProvider<EntityType<?>> provIn) {
        
    }
}
