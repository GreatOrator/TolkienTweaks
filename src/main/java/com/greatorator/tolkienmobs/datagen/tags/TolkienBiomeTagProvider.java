package com.greatorator.tolkienmobs.datagen.tags;

import com.greatorator.tolkienmobs.datagen.TolkienStructureProvider;
import com.greatorator.tolkienmobs.init.TolkienBiomes;
import com.greatorator.tolkienmobs.init.TolkienTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienBiomeTagProvider extends BiomeTagsProvider {
	public TolkienBiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
		super(output, provider, MODID);
	}

	@Override
	protected void addTags(HolderLookup.@NotNull Provider provider) {
        TolkienStructureProvider.STRUCTURES.forEach(definition -> {
            var builder = tag(definition.biomeTag());
            definition.biomes().forEach(builder::add);
        });
		TolkienBiomes.BIOME_FACTORIES.keySet().stream().sorted(Comparator.comparing(key -> key.location().toString())).toList().forEach(biome -> {
			tag(TolkienTags.Biomes.OVERWORLD).add(biome);
			float temperature = provider.lookupOrThrow(Registries.BIOME).getOrThrow(biome).value().getBaseTemperature();
			if (temperature > 0.8F) tag(TolkienTags.Biomes.HOT).add(biome);
			else if (temperature < 0.5F) tag(TolkienTags.Biomes.COLD).add(biome);
			else tag(TolkienTags.Biomes.TEMPERATE).add(biome);
		});
		// BIOMES_BY_TAG holds its biomes in hash sets, so sort them or the tag files
		// come out in a different order on every data generation run.
		TolkienBiomes.BIOMES_BY_TAG.asMap().forEach((biomeTag, biomes) -> biomes.stream()
				.sorted(Comparator.comparing(biome -> biome.location().toString()))
				.forEach(biome -> tag(biomeTag).add(biome)));

		// Empty classifications (for example river/beach) need not exist yet.
		tag(BiomeTags.IS_OVERWORLD)
				.addOptionalTag(TolkienTags.Biomes.OVERWORLD.location());

		tag(Tags.Biomes.IS_HOT_OVERWORLD)
				.addOptionalTag(TolkienTags.Biomes.HOT.location());
		tag(Tags.Biomes.IS_COLD_OVERWORLD)
				.addOptionalTag(TolkienTags.Biomes.COLD.location());
		tag(Tags.Biomes.IS_TEMPERATE_OVERWORLD)
				.addOptionalTag(TolkienTags.Biomes.TEMPERATE.location());
		tag(Tags.Biomes.IS_WET_OVERWORLD)
				.addOptionalTag(TolkienTags.Biomes.WET.location());
		tag(Tags.Biomes.IS_DRY_OVERWORLD)
				.addOptionalTag(TolkienTags.Biomes.DRY.location());
		tag(Tags.Biomes.IS_SPARSE_VEGETATION_OVERWORLD)
				.addOptionalTag(TolkienTags.Biomes.SPARSE.location());
		tag(Tags.Biomes.IS_DENSE_VEGETATION_OVERWORLD)
				.addOptionalTag(TolkienTags.Biomes.DENSE.location());

		tag(Tags.Biomes.IS_PLAINS)
				.addOptionalTag(TolkienTags.Biomes.PLAINS.location());
		tag(BiomeTags.IS_FOREST)
				.addOptionalTag(TolkienTags.Biomes.FOREST.location());
		tag(BiomeTags.IS_TAIGA)
				.addOptionalTag(TolkienTags.Biomes.TAIGA.location());
		tag(Tags.Biomes.IS_DESERT)
				.addOptionalTag(TolkienTags.Biomes.DESERT.location());
		tag(BiomeTags.IS_SAVANNA)
				.addOptionalTag(TolkienTags.Biomes.SAVANNA.location());
		tag(BiomeTags.IS_JUNGLE)
				.addOptionalTag(TolkienTags.Biomes.JUNGLE.location());
		tag(BiomeTags.IS_MOUNTAIN)
				.addOptionalTag(TolkienTags.Biomes.MOUNTAIN.location());
		tag(Tags.Biomes.IS_MOUNTAIN)
				.addOptionalTag(TolkienTags.Biomes.MOUNTAIN.location());
		tag(BiomeTags.IS_BEACH)
				.addOptionalTag(TolkienTags.Biomes.BEACH.location());
		tag(BiomeTags.IS_BADLANDS)
				.addOptionalTag(TolkienTags.Biomes.BADLANDS.location());
		tag(Tags.Biomes.IS_MOUNTAIN_SLOPE)
				.addOptionalTag(TolkienTags.Biomes.SLOPE.location());
		tag(Tags.Biomes.IS_MOUNTAIN_PEAK)
				.addOptionalTag(TolkienTags.Biomes.PEAK.location());
		tag(Tags.Biomes.IS_SWAMP)
				.addOptionalTag(TolkienTags.Biomes.SWAMP.location());
		tag(Tags.Biomes.IS_SANDY)
				.addOptionalTag(TolkienTags.Biomes.SANDY.location());
		tag(Tags.Biomes.IS_WINDSWEPT)
				.addOptionalTag(TolkienTags.Biomes.WINDSWEPT.location());
		tag(Tags.Biomes.IS_SNOWY)
				.addOptionalTag(TolkienTags.Biomes.SNOWY.location());
		tag(Tags.Biomes.IS_ICY)
				.addOptionalTag(TolkienTags.Biomes.ICY.location());
		tag(Tags.Biomes.IS_FLORAL)
				.addOptionalTag(TolkienTags.Biomes.FLORAL.location());
		tag(Tags.Biomes.IS_DEAD)
				.addOptionalTag(TolkienTags.Biomes.DEAD.location());
		tag(BiomeTags.IS_OCEAN)
				.addOptionalTag(TolkienTags.Biomes.OCEAN.location());
		tag(Tags.Biomes.IS_CONIFEROUS_TREE)
				.addOptionalTag(TolkienTags.Biomes.CONIFEROUS.location());
		tag(Tags.Biomes.IS_OLD_GROWTH)
				.addOptionalTag(TolkienTags.Biomes.DARK_FOREST.location());
		tag(Tags.Biomes.IS_WASTELAND)
				.addOptionalTag(TolkienTags.Biomes.WASTELAND.location());
		tag(Tags.Biomes.IS_MAGICAL)
				.addOptionalTag(TolkienTags.Biomes.MAGICAL.location());
		tag(Tags.Biomes.IS_RIVER)
				.addOptionalTag(TolkienTags.Biomes.RIVER.location());
		tag(Tags.Biomes.IS_CAVE)
				.addOptionalTag(TolkienTags.Biomes.CAVE.location());

		tag(TolkienTags.Biomes.WET)
				.addOptionalTag(TolkienTags.Biomes.SWAMP.location())
				.addOptionalTag(TolkienTags.Biomes.RIVER.location())
				.addOptionalTag(TolkienTags.Biomes.BEACH.location());
		tag(TolkienTags.Biomes.DENSE)
				.addOptionalTag(TolkienTags.Biomes.DARK_FOREST.location())
				.addOptionalTag(TolkienTags.Biomes.FOREST.location())
				.addOptionalTag(TolkienTags.Biomes.JUNGLE.location())
				.addOptionalTag(TolkienTags.Biomes.SWAMP.location());
		tag(TolkienTags.Biomes.DRY)
				.addOptionalTag(TolkienTags.Biomes.DESERT.location());

		tag(BiomeTags.HAS_SWAMP_HUT)
				.addOptionalTag(TolkienTags.Biomes.SWAMP.location());
		tag(BiomeTags.HAS_JUNGLE_TEMPLE)
				.addOptionalTag(TolkienTags.Biomes.JUNGLE.location());
		tag(BiomeTags.HAS_RUINED_PORTAL_OCEAN)
				.addOptionalTag(TolkienTags.Biomes.OCEAN.location());
		tag(BiomeTags.HAS_SHIPWRECK)
				.addOptionalTag(TolkienTags.Biomes.OCEAN.location());
		tag(BiomeTags.HAS_OCEAN_RUIN_WARM)
				.addOptionalTag(TolkienTags.Biomes.OCEAN.location());
		tag(BiomeTags.HAS_VILLAGE_SAVANNA)
				.addOptionalTag(TolkienTags.Biomes.SAVANNA.location());
		tag(BiomeTags.HAS_PILLAGER_OUTPOST)
				.addOptionalTag(TolkienTags.Biomes.SAVANNA.location());
	}

	@Nonnull
	@Override
	public String getName() {
		return "Tolkienmobs - Biome Tags";
	}
}