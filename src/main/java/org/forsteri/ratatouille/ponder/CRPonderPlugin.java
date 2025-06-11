package org.forsteri.ratatouille.ponder;

import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.forsteri.ratatouille.Ratatouille;

public class CRPonderPlugin implements PonderPlugin {

    @Override
    public @NotNull String getModId() {
        return Ratatouille.MOD_ID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        CRPonderScenes.register(helper);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        CRPonderTags.register(helper);
    }
}
