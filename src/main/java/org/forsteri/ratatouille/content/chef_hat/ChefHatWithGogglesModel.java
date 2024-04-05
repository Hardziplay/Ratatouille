package org.forsteri.ratatouille.content.chef_hat;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.model.BakedModelWrapper;
import org.forsteri.ratatouille.entry.CRPartialModels;

public class ChefHatWithGogglesModel extends BakedModelWrapper<BakedModel> {

    public ChefHatWithGogglesModel(BakedModel template) {
        super(template);
    }

    @Override
    public BakedModel applyTransform(ItemDisplayContext cameraTransformType, PoseStack poseStack, boolean applyLeftHandTransform) {
        if (cameraTransformType == ItemDisplayContext.HEAD)
            return CRPartialModels.CHEF_HAT_WITH_GOGGLES.get()
                    .applyTransform(cameraTransformType, poseStack, applyLeftHandTransform);
        return super.applyTransform(cameraTransformType, poseStack, applyLeftHandTransform);
    }

}