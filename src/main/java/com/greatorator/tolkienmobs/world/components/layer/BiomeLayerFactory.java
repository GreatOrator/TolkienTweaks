package com.greatorator.tolkienmobs.world.components.layer;


import java.util.function.LongFunction;

public interface BiomeLayerFactory {
	LazyArea build(LongFunction<LazyAreaContext> contextFactory);

	BiomeLayerType getType();
}
