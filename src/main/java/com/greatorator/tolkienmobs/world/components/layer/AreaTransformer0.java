package com.greatorator.tolkienmobs.world.components.layer;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

public interface AreaTransformer0 {
	default <R extends Area> R run(BigContext<R> context) {
		return context.createResult((x, z) -> {
			RandomContext randomContext = new RandomContext(context.getSeed());
			randomContext.initRandom(x, z);
			return this.applyPixel(randomContext, x, z);
		});
	}

	ResourceKey<Biome> applyPixel(RandomContext randomContext, int x, int z);
}
