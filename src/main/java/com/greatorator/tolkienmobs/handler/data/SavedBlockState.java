package com.greatorator.tolkienmobs.handler.data;

import com.greatorator.tolkienmobs.TolkienStreamCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record SavedBlockState(BlockState state) {
	public static final SavedBlockState EMPTY = new SavedBlockState(Blocks.AIR.defaultBlockState());
	//@formatter:off
	public static final Codec<SavedBlockState> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					BlockState.CODEC.fieldOf("state").forGetter(SavedBlockState::state))
			.apply(instance, SavedBlockState::new));
	public static final StreamCodec<FriendlyByteBuf, SavedBlockState> STREAM_CODEC = StreamCodec.composite(
			TolkienStreamCodecs.BLOCK_STATE, SavedBlockState::state,
			SavedBlockState::new);
	//@formatter:on
}
