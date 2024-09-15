package com.greatorator.tolkienmobs.particle.data;

import com.greatorator.tolkienmobs.init.TolkienParticleTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nonnull;

public record LeafParticleData(int r, int g, int b) implements ParticleOptions {
	public static MapCodec<LeafParticleData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.INT.fieldOf("r").forGetter((obj) -> obj.r),
		Codec.INT.fieldOf("g").forGetter((obj) -> obj.g),
		Codec.INT.fieldOf("b").forGetter((obj) -> obj.b)
	).apply(instance, LeafParticleData::new));

	public static StreamCodec<? super RegistryFriendlyByteBuf, LeafParticleData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, p -> p.r,
		ByteBufCodecs.VAR_INT, p -> p.g,
		ByteBufCodecs.VAR_INT, p -> p.b,
		LeafParticleData::new
	);

	@Nonnull
	@Override
	public ParticleType<?> getType() {
		return TolkienParticleTypes.FALLING_LEAVES.get();
	}
}
