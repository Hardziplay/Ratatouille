package org.forsteri.ratatouille.content.spreader;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.forsteri.ratatouille.entry.CRParticleTypes;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class SpreaderParticle extends ExplodeParticle {
    public static final MapCodec<SpreaderParticleData> CODEC = RecordCodecBuilder.mapCodec(i -> i
            .group(Codec.INT.fieldOf("color")
                    .forGetter(p -> p.color))
            .apply(i, SpreaderParticleData::new));
    public static final StreamCodec<ByteBuf, SpreaderParticleData> STREAM_CODEC = ByteBufCodecs.INT.map(
            SpreaderParticleData::new, p -> p.color);

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

    public static class SpreaderParticleData implements ParticleOptions, ICustomParticleDataWithSprite<SpreaderParticleData> {
        private final int color;

        public SpreaderParticleData() {
            this(0xffffff);
        }

        public SpreaderParticleData(int color) {
            this.color = color;
        }

        @Override
        public MapCodec<SpreaderParticleData> getCodec(ParticleType<SpreaderParticleData> type) {
            return CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, SpreaderParticleData> getStreamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public @NotNull ParticleType<?> getType() {
            return CRParticleTypes.SPREADER.get();
        }

        @Override
        public ParticleEngine.SpriteParticleRegistration<SpreaderParticleData> getMetaFactory() {
            return SpreaderParticle.Factory::new;
        }

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
