package org.forsteri.ratatouille.entry;

import com.jozufozu.flywheel.core.PartialModel;
import net.minecraft.resources.ResourceLocation;
import org.forsteri.ratatouille.Ratatouille;

public class CRPartialModels {

    public static final PartialModel THRESHER_BLADE = of("block/thresher/partial");
    public static final PartialModel OVEN_FAN_BLADE = of("block/oven_fan/partial");
    private static PartialModel of(@SuppressWarnings("SameParameterValue") String path) {
        return new PartialModel(new ResourceLocation(Ratatouille.MOD_ID, path));
    }
    public CRPartialModels() {}
    public static void register() {}
}
