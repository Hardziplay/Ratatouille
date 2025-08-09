package org.forsteri.ratatouille.content.spreader;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.equipment.bell.BasicParticleData;
import com.simibubi.create.content.kinetics.steamEngine.SteamJetParticle;
import com.simibubi.create.content.kinetics.steamEngine.SteamJetParticleData;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.particle.ExplodeParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import org.forsteri.ratatouille.entry.CRParticleTypes;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class SpreaderParticle extends ExplodeParticle {
    public SpreaderParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites, int color) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pSprites);
        this.setColor(color);
        this.setAlpha(1);
    }

    public void setColor(int pColor) {
        float f = (float)((pColor & 16711680) >> 16) / 255.0F;
        float f1 = (float)((pColor & '\uff00') >> 8) / 255.0F;
        float f2 = (float)((pColor & 255) >> 0) / 255.0F;
        this.setColor(f * 1.0F, f1 * 1.0F, f2 * 1.0F);
    }

    public static final ParticleOptions.Deserializer<SpreaderParticleData> DESERIALIZER =
            new ParticleOptions.Deserializer<>() {
                @Override
                public @NotNull SpreaderParticleData fromCommand(
                        @NotNull ParticleType<SpreaderParticleData> type,
                        StringReader reader) throws CommandSyntaxException {
                    reader.expect(' ');
                    int color = reader.readInt();
                    return new SpreaderParticleData(color);
                }

                @Override
                public @NotNull SpreaderParticleData fromNetwork(
                        @NotNull ParticleType<SpreaderParticleData> type,
                        FriendlyByteBuf buffer) {
                    int color = buffer.readInt();
                    return new SpreaderParticleData(color);
                }
            };

    public static final Codec<SpreaderParticleData> CODEC = RecordCodecBuilder.create(i -> i
            .group(Codec.INT.fieldOf("color")
                    .forGetter(p -> p.color))
            .apply(i, SpreaderParticleData::new));


    public static class SpreaderParticleData implements ParticleOptions, ICustomParticleDataWithSprite<SpreaderParticleData> {
        private final int color;

        @Override
        public Codec<SpreaderParticleData> getCodec(ParticleType<SpreaderParticleData> type) {
            return CODEC;
        }

        public SpreaderParticleData() {
            this(0xffffff);
        }

        public SpreaderParticleData(int color) {
            this.color = color;
        }

        @Override
        public @NotNull ParticleType<?> getType() {
            return CRParticleTypes.SPREADER.get();
        }

        @Override
        public void writeToNetwork(@NotNull FriendlyByteBuf buffer) {
            buffer.writeInt(this.color);
        }

        @Override
        public @NotNull String writeToString() {
            return String.format("%s %d",
                    net.minecraftforge.registries.ForgeRegistries.PARTICLE_TYPES.getKey(getType()),
                    this.color);
        }

        @Override
        public @NotNull Deserializer<SpreaderParticleData> getDeserializer() {
            return DESERIALIZER;
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
