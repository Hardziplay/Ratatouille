package org.forsteri.ratatouille.content.chef_hat;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraftforge.client.model.BakedModelWrapper;
import org.forsteri.ratatouille.entry.CRPartialModels;

public class ChefHatModel extends BakedModelWrapper<BakedModel> {

    public ChefHatModel(BakedModel template) {
        super(template);
    }

    @Override
    public BakedModel applyTransform(ItemTransforms.TransformType cameraTransformType, PoseStack mat, boolean leftHanded) {
        if (cameraTransformType == ItemTransforms.TransformType.HEAD)
            return CRPartialModels.CHEF_HAT.get()
                    .applyTransform(cameraTransformType, mat, leftHanded);
        return super.applyTransform(cameraTransformType, mat, leftHanded);
    }

}