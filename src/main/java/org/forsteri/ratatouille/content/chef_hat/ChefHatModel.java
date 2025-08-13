package org.forsteri.ratatouille.content.chef_hat;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import org.forsteri.ratatouille.entry.CRPartialModels;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ChefHatModel extends BakedModelWrapper<BakedModel> {

    public ChefHatModel(BakedModel template) {
        super(template);
    }

    @Override
    public BakedModel applyTransform(ItemDisplayContext cameraTransformType, PoseStack poseStack, boolean applyLeftHandTransform) {
        if (cameraTransformType == ItemDisplayContext.HEAD)
            return CRPartialModels.CHEF_HAT.get()
                    .applyTransform(cameraTransformType, poseStack, applyLeftHandTransform);
        return super.applyTransform(cameraTransformType, poseStack, applyLeftHandTransform);
    }

}