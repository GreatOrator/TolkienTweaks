package com.greatorator.tolkienmobs.world.components.layer;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

public interface AreaTransformer1 extends DimensionTransformer {
	default <R extends Area> R run(BigContext<R> context, R area) {
		return context.createResult((x, z) -> {
			RandomContext randomContext = new RandomContext(context.getSeed());
			randomContext.initRandom(x, z);
			return this.applyPixel(randomContext, area, x, z);
		}, area);
	}

	ResourceKey<Biome> applyPixel(RandomContext randomContext, Area layer, int x, int z);
}