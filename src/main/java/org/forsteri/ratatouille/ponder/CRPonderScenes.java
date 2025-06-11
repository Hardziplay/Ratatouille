package org.forsteri.ratatouille.ponder;

import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.forsteri.ratatouille.entry.CRBlocks;
import org.forsteri.ratatouille.ponder.scene.IrrigationTowerScene;

public class CRPonderScenes {

    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<RegistryEntry<?, ?>> ENTRY_HELPER =
                helper.withKeyFunction(RegistryEntry::getId);

        ENTRY_HELPER.forComponents(CRBlocks.IRRIGATION_TOWER_BLOCK)
                .addStoryBoard("irrigation_tower", IrrigationTowerScene::irrigationTower, CRPonderTags.IRRIGATION_TAG_ID);
    }
}
