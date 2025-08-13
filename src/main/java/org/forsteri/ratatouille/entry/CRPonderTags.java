package org.forsteri.ratatouille.entry;

import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import org.forsteri.ratatouille.Ratatouille;

public class CRPonderTags {
    public static final ResourceLocation RATATOUILLE_BLOCKS = Ratatouille.asResource("ratatouille");

    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<RegistryEntry<?, ?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);
        helper.registerTag(RATATOUILLE_BLOCKS).addToIndex().item((ItemLike) CRBlocks.OVEN.get(), true, false).title("Ratatouille").description("Components in Ratatouille").register();
        HELPER.addToTag(RATATOUILLE_BLOCKS)
                .add(CRBlocks.SPREADER_BLOCK)
                .add(CRBlocks.OVEN)
                .add(CRBlocks.OVEN_FAN)
                .add(CRBlocks.THRESHER)
                .add(CRBlocks.IRRIGATION_TOWER_BLOCK)
                //.add(CRBlocks.COMPOST_TOWER_BLOCK)
                .add(CRBlocks.SQUEEZE_BASIN);
    }
}
