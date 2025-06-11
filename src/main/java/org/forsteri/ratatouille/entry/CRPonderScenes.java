package org.forsteri.ratatouille.entry;

import org.forsteri.ratatouille.ponder.IrrigationTowerScene;
import org.forsteri.ratatouille.ponder.OvenScene;
import org.forsteri.ratatouille.ponder.SpreaderScene;
import org.forsteri.ratatouille.ponder.SqueezeBasinScene;
import org.forsteri.ratatouille.ponder.ThresherScene;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class CRPonderScenes {
    public CRPonderScenes() {
    }

    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<ItemProviderEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);
        HELPER.addStoryBoard(CRBlocks.SPREADER_BLOCK, "spreader", SpreaderScene::spreader, new ResourceLocation[0]);
        HELPER.addStoryBoard(CRBlocks.SPREADER_BLOCK, "spreader", SpreaderScene::spreader, new ResourceLocation[0]);
        HELPER.addStoryBoard(CRBlocks.OVEN, "oven", OvenScene::oven, new ResourceLocation[0]);
        HELPER.addStoryBoard(CRBlocks.OVEN_FAN, "oven", OvenScene::oven, new ResourceLocation[0]);
        HELPER.addStoryBoard(CRBlocks.THRESHER, "thresher", ThresherScene::thresher, new ResourceLocation[0]);
        HELPER.addStoryBoard(CRBlocks.IRRIGATION_TOWER_BLOCK, "irrigation_tower", IrrigationTowerScene::irrigationTower, new ResourceLocation[0]);
        HELPER.addStoryBoard(CRBlocks.SQUEEZE_BASIN, "squeeze_basin", SqueezeBasinScene::squeezeBasin, new ResourceLocation[0]);
    }
}
