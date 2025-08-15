package org.forsteri.ratatouille.content.spreader;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.forsteri.ratatouille.entry.CRParticleTypes;
import org.jetbrains.annotations.NotNull;

public class SpreaderParticleData implements ParticleOptions, ICustomParticleDataWithSprite<SpreaderParticleData> {
    public static final MapCodec<SpreaderParticleData> CODEC = RecordCodecBuilder.mapCodec(i -> i
            .group(Codec.INT.fieldOf("color")
                    .forGetter(p -> p.color))
            .apply(i, SpreaderParticleData::new));
    public static final StreamCodec<ByteBuf, SpreaderParticleData> STREAM_CODEC = ByteBufCodecs.INT.map(
            SpreaderParticleData::new, p -> p.color);

    
    public final int color;

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