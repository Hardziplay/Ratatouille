package org.forsteri.ratatouille.entry;

import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class CRPonderPlugin implements PonderPlugin {
    public CRPonderPlugin() {
    }

    public String getModId() {
        return "ratatouille";
    }

    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        CRPonderScenes.register(helper);
    }

    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        CRPonderTags.register(helper);
    }
}
