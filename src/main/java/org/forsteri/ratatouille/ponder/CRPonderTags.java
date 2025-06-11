package org.forsteri.ratatouille.ponder;

import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.forsteri.ratatouille.entry.CRBlocks;
import org.forsteri.ratatouille.Ratatouille;

public class CRPonderTags {

    public static final ResourceLocation IRRIGATION_TAG_ID =
            new ResourceLocation(Ratatouille.MOD_ID, "irrigation");

    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
        helper.registerTag(IRRIGATION_TAG_ID)
                .addToIndex()
                .item(CRBlocks.IRRIGATION_TOWER_BLOCK.get())
                .title("Irrigation")
                .description("1")
                .register();

        PonderTagRegistrationHelper<RegistryEntry<?, ?>> ENTRY_HELPER =
                helper.withKeyFunction(RegistryEntry::getId);

        ENTRY_HELPER.addToTag(IRRIGATION_TAG_ID)
                .add(CRBlocks.IRRIGATION_TOWER_BLOCK);
    }
}
