package org.forsteri.ratatouille.mixin;

import com.simibubi.create.content.kinetics.fan.AirFlowParticle;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import org.forsteri.ratatouille.content.spreader.SpreaderBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AirFlowParticle.class, remap = false)
public class AirFlowParticleMixin extends SimpleAnimatedParticle {
    @Final
    @Shadow
    private IAirCurrentSource source;

    protected AirFlowParticleMixin(ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet pSprites, float pGravity) {
        super(pLevel, pX, pY, pZ, pSprites, pGravity);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/fan/AirFlowParticle;setAlpha(F)V", shift = At.Shift.AFTER), remap = true)
    private void tick(CallbackInfo ci) {
        if (source instanceof SpreaderBlockEntity be) {
            setColor(be.getParticleColor());
            setAlpha(1);
        }
    }
}
