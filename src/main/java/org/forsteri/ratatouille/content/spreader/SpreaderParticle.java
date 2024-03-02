package org.forsteri.ratatouille.content.spreader;

import com.simibubi.create.content.equipment.bell.BasicParticleData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.particle.ExplodeParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.forsteri.ratatouille.entry.CRParticleTypes;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class SpreaderParticle extends ExplodeParticle {
    public SpreaderParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pSprites);
        this.setColor(0xa1bd61);
        this.setAlpha(1);
    }

    public void setColor(int pColor) {
        float f = (float)((pColor & 16711680) >> 16) / 255.0F;
        float f1 = (float)((pColor & '\uff00') >> 8) / 255.0F;
        float f2 = (float)((pColor & 255) >> 0) / 255.0F;
        this.setColor(f * 1.0F, f1 * 1.0F, f2 * 1.0F);
    }

    public static class Provider extends BasicParticleData<SpreaderParticle> {
        @Override
        public @NotNull IBasicParticleFactory<SpreaderParticle> getBasicFactory() {
            return SpreaderParticle::new;
        }

        @Override
        public @NotNull ParticleType<?> getType() {
            return CRParticleTypes.SPREADER.get();
        }
    }
}
