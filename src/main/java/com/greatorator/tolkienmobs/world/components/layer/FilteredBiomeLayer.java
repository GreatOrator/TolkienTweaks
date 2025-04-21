package com.greatorator.tolkienmobs.world.components.layer;

import com.greatorator.tolkienmobs.init.TolkienLayerStack;
import com.greatorator.tolkienmobs.init.TolkienLayerTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.function.LongFunction;

public record FilteredBiomeLayer(ResourceKey<Biome> biomeFirst) implements AreaTransformer2, DimensionOffset0Transformer {
	@Override
	public ResourceKey<Biome> applyPixel(RandomContext randomContext, Area area1, Area area2, int x, int z) {
		ResourceKey<Biome> riverInputs = area2.getBiome(this.getParentX(x), this.getParentY(z));

		if (riverInputs == this.biomeFirst) {
			return riverInputs;
		} else {
			return area1.getBiome(this.getParentX(x), this.getParentY(z));
		}
	}

	public static final class Factory implements BiomeLayerFactory {
		public static final MapCodec<Factory> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
				Codec.LONG.fieldOf("salt").forGetter(Factory::salt),
				ResourceKey.codec(Registries.BIOME).fieldOf("keep_biome").forGetter(Factory::filterKeep),
				TolkienLayerStack.HOLDER_CODEC.fieldOf("filtered_layer").forGetter(Factory::filteredLayer),
				TolkienLayerStack.HOLDER_CODEC.fieldOf("fallback_layer").forGetter(Factory::fallbackLayer)
		).apply(inst, Factory::new));

		private final long salt;
		private final ResourceKey<Biome> filterKeep;
		private final Holder<BiomeLayerFactory> filteredLayer;
		private final Holder<BiomeLayerFactory> fallbackLayer;

		private final FilteredBiomeLayer filteredBiomeLayer;

		public Factory(long salt, ResourceKey<Biome> filterKeep, Holder<BiomeLayerFactory> filteredLayer, Holder<BiomeLayerFactory> fallbackLayer) {
			this.salt = salt;
			this.filterKeep = filterKeep;
			this.filteredLayer = filteredLayer;
			this.fallbackLayer = fallbackLayer;

			this.filteredBiomeLayer = new FilteredBiomeLayer(this.filterKeep);
		}

		@Override
		public LazyArea build(LongFunction<LazyAreaContext> contextFactory) {
			return this.filteredBiomeLayer.run(contextFactory.apply(this.salt), this.fallbackLayer.value().build(contextFactory), this.filteredLayer.value().build(contextFactory));
		}

		@Override
		public BiomeLayerType getType() {
			return TolkienLayerTypes.FILTERED.get();
		}

		public long salt() {
			return this.salt;
		}

		public ResourceKey<Biome> filterKeep() {
			return this.filterKeep;
		}

		public Holder<BiomeLayerFactory> fallbackLayer() {
			return this.fallbackLayer;
		}

		public Holder<BiomeLayerFactory> filteredLayer() {
			return this.filteredLayer;
		}
	}

}
