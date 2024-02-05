package org.forsteri.ratatouille.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import net.minecraft.resources.ResourceLocation;
import org.forsteri.ratatouille.Ratatouille;

import javax.annotation.ParametersAreNonnullByDefault;

@JeiPlugin
@ParametersAreNonnullByDefault
public class RatatouilleJei implements IModPlugin {
    private static final ResourceLocation ID = new ResourceLocation(Ratatouille.MOD_ID, "");

    public RatatouilleJei() {}

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
//        registration.addRecipeCategories();
    }
}
