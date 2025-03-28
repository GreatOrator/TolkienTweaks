package com.greatorator.tolkienmobs.world.components.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/**
 * If a hollow version of the log you want doesn't exist, set it to Blocks.AIR and the feature will fix it up for ya
 */
public record HollowLogConfig(BlockState normal, BlockState hollow) implements FeatureConfiguration {

	public static final Codec<HollowLogConfig> CODEC =
		RecordCodecBuilder.create((p_67632_) -> p_67632_.group(
				BlockState.CODEC.fieldOf("normal").forGetter((p_160757_) -> p_160757_.normal),
				BlockState.CODEC.fieldOf("hollow").forGetter((p_160751_) -> p_160751_.hollow))
			.apply(p_67632_, HollowLogConfig::new));
}
