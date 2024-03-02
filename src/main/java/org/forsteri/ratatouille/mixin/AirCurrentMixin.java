package org.forsteri.ratatouille.mixin;

import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.forsteri.ratatouille.content.spreader.SpreaderBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AirCurrent.class, remap = false)
public class AirCurrentMixin {
    @Final
    @Shadow
    public IAirCurrentSource source;

    @Inject(method = "tickAffectedEntities", at=@At("HEAD"), cancellable = true)
    private void tickAffectedEntities(Level world, Direction facing, CallbackInfo  cir) {
        if (source instanceof SpreaderBlockEntity)
            cir.cancel();
    }

    @Inject(method = "tickAffectedHandlers", at=@At("HEAD"), cancellable = true)
    private void tickAffectedHandlers(CallbackInfo cir) {
        if (source instanceof SpreaderBlockEntity)
            cir.cancel();
    }
}
