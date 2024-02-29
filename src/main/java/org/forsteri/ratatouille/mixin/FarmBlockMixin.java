package org.forsteri.ratatouille.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.forsteri.ratatouille.content.irrigation_tower.IrrigationTowerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FarmBlock.class)
public class FarmBlockMixin {
    @Inject(method = "isNearWater", at=@At("HEAD"), cancellable = true)
    private static void isNearWater(LevelReader pLevel, BlockPos pPos, CallbackInfoReturnable<Boolean> cir) {
        IrrigationTowerBlockEntity.isNearWater(pLevel, pPos, cir);
    }
}
