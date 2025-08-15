package org.forsteri.ratatouille.content.spreader;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.forsteri.ratatouille.entry.CRParticleTypes;
import org.jetbrains.annotations.NotNull;

public class SpreaderParticleData implements ParticleOptions, ICustomParticleDataWithSprite<SpreaderParticleData> {
    public static final Codec<SpreaderParticleData> CODEC = RecordCodecBuilder.create(i -> i
            .group(Codec.INT.fieldOf("color")
                    .forGetter(p -> p.color))
            .apply(i, SpreaderParticleData::new));
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
    public final int color;


    public SpreaderParticleData() {
        this(0xffffff);
    }

    public SpreaderParticleData(int color) {
        this.color = color;
    }

    @Override
    public Codec<SpreaderParticleData> getCodec(ParticleType<SpreaderParticleData> type) {
        return CODEC;
    }

    @Override
    public @NotNull Deserializer<SpreaderParticleData> getDeserializer() {
        return DESERIALIZER;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ParticleEngine.SpriteParticleRegistration<SpreaderParticleData> getMetaFactory() {
        return SpreaderParticle.Factory::new;
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


}
