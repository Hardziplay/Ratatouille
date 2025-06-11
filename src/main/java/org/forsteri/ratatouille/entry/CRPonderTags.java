package org.forsteri.ratatouille.entry;

import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

public class CRPonderTags {
    public CRPonderTags() {
    }

    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<RegistryEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);
        helper.registerTag(new ResourceLocation("ratatouille", "ratatouille")).addToIndex().item((ItemLike)CRBlocks.OVEN.get(), true, false).title("Ratatouille").description("Components in Ratatouille").register();
        HELPER.addToTag(new ResourceLocation("ratatouille", "ratatouille")).add(CRBlocks.SPREADER_BLOCK).add(CRBlocks.OVEN).add(CRBlocks.OVEN_FAN).add(CRBlocks.THRESHER).add(CRBlocks.IRRIGATION_TOWER_BLOCK).add(CRBlocks.SQUEEZE_BASIN);
    }
}
