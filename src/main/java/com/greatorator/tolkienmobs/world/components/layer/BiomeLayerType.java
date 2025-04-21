package com.greatorator.tolkienmobs.world.components.layer;

import com.mojang.serialization.MapCodec;

@FunctionalInterface
public interface BiomeLayerType {
	MapCodec<? extends BiomeLayerFactory> getCodec();
}
