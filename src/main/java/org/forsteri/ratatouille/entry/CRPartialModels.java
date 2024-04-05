package org.forsteri.ratatouille.entry;

import com.jozufozu.flywheel.core.PartialModel;
import net.minecraft.resources.ResourceLocation;
import org.forsteri.ratatouille.Ratatouille;

public class CRPartialModels {

    public static final PartialModel THRESHER_BLADE = of("block/thresher/partial");
    public static final PartialModel OVEN_FAN_BLADE = of("block/oven_fan/partial");
    public static final PartialModel SQUEEZE_BASIN_COVER = of("block/squeeze_basin/partial");
    public static final PartialModel MECHANICAL_DEMOLDER_HEAD = of("block/mechanical_demolder/head");
    public static final PartialModel CHEF_HAT = of("block/chef_hat");
    public static final PartialModel CHEF_HAT_WITH_GOGGLES = of("block/chef_hat_with_goggles");


    private static PartialModel of(@SuppressWarnings("SameParameterValue") String path) {
        return new PartialModel(new ResourceLocation(Ratatouille.MOD_ID, path));
    }

    public CRPartialModels() {}
    public static void register() {}
}
