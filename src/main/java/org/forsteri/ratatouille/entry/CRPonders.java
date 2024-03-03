package org.forsteri.ratatouille.entry;

import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.foundation.ponder.PonderTag;
import net.minecraft.resources.ResourceLocation;
import org.forsteri.ratatouille.Ratatouille;

public class CRPonders {
    public static final PonderTag RATATOUILLE = new PonderTag(new ResourceLocation(Ratatouille.MOD_ID, "electric")).item(CRBlocks.OVEN.get(), true, false)
            .defaultLang("Ratatouille", "Components in RRatatouille");
    static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(Ratatouille.MOD_ID);

    public static void register() {
//        HELPER.addStoryBoard(CRBlocks.SPREADER_BLOCK, "spreader", PonderScenes::electricMotor, CRPonders.RATATOUILLE, RATATOUILLE);
//        HELPER.addStoryBoard(CRBlocks.OVEN, "oven", PonderScenes::alternator, CRPonders.RATATOUILLE, RATATOUILLE);
//        HELPER.addStoryBoard(CRBlocks.THRESHER, "thresher", PonderScenes::rollingMill, CRPonders.RATATOUILLE);
//        HELPER.addStoryBoard(CRBlocks.IRRIGATION_TOWER_BLOCK, "irrigation_tower", PonderScenes::automateRollingMill, CRPonders.RATATOUILLE, RATATOUILLE);
//        HELPER.addStoryBoard(CRBlocks.SQUEEZE_BASIN, "squeeze_basin", PonderScenes::teslaCoil, CRPonders.RATATOUILLE, RATATOUILLE);
    }
}
