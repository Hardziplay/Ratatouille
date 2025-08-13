package org.forsteri.ratatouille.entry;

import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import org.forsteri.ratatouille.Ratatouille;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CRPonderPlugin implements PonderPlugin {
    public CRPonderPlugin() {
    }

    public String getModId() {
        return Ratatouille.MOD_ID;
    }

    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        CRPonderScenes.register(helper);
    }

    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        CRPonderTags.register(helper);
    }
}
