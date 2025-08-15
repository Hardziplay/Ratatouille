package org.forsteri.ratatouille.content.spreader;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ExplodeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpreaderParticle extends ExplodeParticle {
    public SpreaderParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites, int color) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pSprites);
        this.setColor(color);
        this.setAlpha(1);
    }

    public void setColor(int pColor) {
        float f = (float) ((pColor & 16711680) >> 16) / 255.0F;
        float f1 = (float) ((pColor & '\uff00') >> 8) / 255.0F;
        float f2 = (float) ((pColor & 255) >> 0) / 255.0F;
        this.setColor(f * 1.0F, f1 * 1.0F, f2 * 1.0F);
    }


    public static class Factory implements ParticleProvider<SpreaderParticleData> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet animatedSprite) {
            this.spriteSet = animatedSprite;
        }

        public Particle createParticle(SpreaderParticleData data, ClientLevel worldIn, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new SpreaderParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet, data.color);
        }
    }
}
