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
		TolkienBiomes.BIOME_FACTORIES.keySet().stream().sorted(Comparator.comparing(key -> key.identifier().toString())).toList().forEach(biome -> {
			tag(TolkienTags.Biomes.OVERWORLD).add(biome);
			float temperature = provider.lookupOrThrow(Registries.BIOME).getOrThrow(biome).value().getBaseTemperature();
			if (temperature > 0.8F) tag(TolkienTags.Biomes.HOT).add(biome);
			else if (temperature < 0.5F) tag(TolkienTags.Biomes.COLD).add(biome);
			else tag(TolkienTags.Biomes.TEMPERATE).add(biome);
		});
		// BIOMES_BY_TAG holds its biomes in hash sets, so sort them or the tag files
		// come out in a different order on every data generation run.
		TolkienBiomes.BIOMES_BY_TAG.asMap().forEach((biomeTag, biomes) -> biomes.stream()
				.sorted(Comparator.comparing(biome -> biome.identifier().toString()))
				.forEach(biome -> tag(biomeTag).add(biome)));

		// Empty classifications (for example river/beach) need not exist yet.
		tag(BiomeTags.IS_OVERWORLD)
				.addOptionalTag(TolkienTags.Biomes.OVERWORLD);

		tag(Tags.Biomes.IS_HOT_OVERWORLD)
				.addOptionalTag(TolkienTags.Biomes.HOT);
		tag(Tags.Biomes.IS_COLD_OVERWORLD)
				.addOptionalTag(TolkienTags.Biomes.COLD);
		tag(Tags.Biomes.IS_TEMPERATE_OVERWORLD)
				.addOptionalTag(TolkienTags.Biomes.TEMPERATE);
		tag(Tags.Biomes.IS_WET_OVERWORLD)
				.addOptionalTag(TolkienTags.Biomes.WET);
		tag(Tags.Biomes.IS_DRY_OVERWORLD)
				.addOptionalTag(TolkienTags.Biomes.DRY);
		tag(Tags.Biomes.IS_SPARSE_VEGETATION_OVERWORLD)
				.addOptionalTag(TolkienTags.Biomes.SPARSE);
		tag(Tags.Biomes.IS_DENSE_VEGETATION_OVERWORLD)
				.addOptionalTag(TolkienTags.Biomes.DENSE);

		tag(Tags.Biomes.IS_PLAINS)
				.addOptionalTag(TolkienTags.Biomes.PLAINS);
		tag(BiomeTags.IS_FOREST)
				.addOptionalTag(TolkienTags.Biomes.FOREST);
		tag(BiomeTags.IS_TAIGA)
				.addOptionalTag(TolkienTags.Biomes.TAIGA);
		tag(Tags.Biomes.IS_DESERT)
				.addOptionalTag(TolkienTags.Biomes.DESERT);
		tag(BiomeTags.IS_SAVANNA)
				.addOptionalTag(TolkienTags.Biomes.SAVANNA);
		tag(BiomeTags.IS_JUNGLE)
				.addOptionalTag(TolkienTags.Biomes.JUNGLE);
		tag(BiomeTags.IS_MOUNTAIN)
				.addOptionalTag(TolkienTags.Biomes.MOUNTAIN);
		tag(Tags.Biomes.IS_MOUNTAIN)
				.addOptionalTag(TolkienTags.Biomes.MOUNTAIN);
		tag(BiomeTags.IS_BEACH)
				.addOptionalTag(TolkienTags.Biomes.BEACH);
		tag(BiomeTags.IS_BADLANDS)
				.addOptionalTag(TolkienTags.Biomes.BADLANDS);
		tag(Tags.Biomes.IS_MOUNTAIN_SLOPE)
				.addOptionalTag(TolkienTags.Biomes.SLOPE);
		tag(Tags.Biomes.IS_MOUNTAIN_PEAK)
				.addOptionalTag(TolkienTags.Biomes.PEAK);
		tag(Tags.Biomes.IS_SWAMP)
				.addOptionalTag(TolkienTags.Biomes.SWAMP);
		tag(Tags.Biomes.IS_SANDY)
				.addOptionalTag(TolkienTags.Biomes.SANDY);
		tag(Tags.Biomes.IS_WINDSWEPT)
				.addOptionalTag(TolkienTags.Biomes.WINDSWEPT);
		tag(Tags.Biomes.IS_SNOWY)
				.addOptionalTag(TolkienTags.Biomes.SNOWY);
		tag(Tags.Biomes.IS_ICY)
				.addOptionalTag(TolkienTags.Biomes.ICY);
		tag(Tags.Biomes.IS_FLORAL)
				.addOptionalTag(TolkienTags.Biomes.FLORAL);
		tag(Tags.Biomes.IS_DEAD)
				.addOptionalTag(TolkienTags.Biomes.DEAD);
		tag(BiomeTags.IS_OCEAN)
				.addOptionalTag(TolkienTags.Biomes.OCEAN);
		tag(Tags.Biomes.IS_CONIFEROUS_TREE)
				.addOptionalTag(TolkienTags.Biomes.CONIFEROUS);
		tag(Tags.Biomes.IS_OLD_GROWTH)
				.addOptionalTag(TolkienTags.Biomes.DARK_FOREST);
		tag(Tags.Biomes.IS_WASTELAND)
				.addOptionalTag(TolkienTags.Biomes.WASTELAND);
		tag(Tags.Biomes.IS_MAGICAL)
				.addOptionalTag(TolkienTags.Biomes.MAGICAL);
		tag(Tags.Biomes.IS_RIVER)
				.addOptionalTag(TolkienTags.Biomes.RIVER);
		tag(Tags.Biomes.IS_CAVE)
				.addOptionalTag(TolkienTags.Biomes.CAVE);

		tag(TolkienTags.Biomes.WET)
				.addOptionalTag(TolkienTags.Biomes.SWAMP)
				.addOptionalTag(TolkienTags.Biomes.RIVER)
				.addOptionalTag(TolkienTags.Biomes.BEACH);
		tag(TolkienTags.Biomes.DENSE)
				.addOptionalTag(TolkienTags.Biomes.DARK_FOREST)
				.addOptionalTag(TolkienTags.Biomes.FOREST)
				.addOptionalTag(TolkienTags.Biomes.JUNGLE)
				.addOptionalTag(TolkienTags.Biomes.SWAMP);
		tag(TolkienTags.Biomes.DRY)
				.addOptionalTag(TolkienTags.Biomes.DESERT);

		tag(BiomeTags.HAS_SWAMP_HUT)
				.addOptionalTag(TolkienTags.Biomes.SWAMP);
		tag(BiomeTags.HAS_JUNGLE_TEMPLE)
				.addOptionalTag(TolkienTags.Biomes.JUNGLE);
		tag(BiomeTags.HAS_RUINED_PORTAL_OCEAN)
				.addOptionalTag(TolkienTags.Biomes.OCEAN);
		tag(BiomeTags.HAS_SHIPWRECK)
				.addOptionalTag(TolkienTags.Biomes.OCEAN);
		tag(BiomeTags.HAS_OCEAN_RUIN_WARM)
				.addOptionalTag(TolkienTags.Biomes.OCEAN);
		tag(BiomeTags.HAS_VILLAGE_SAVANNA)
				.addOptionalTag(TolkienTags.Biomes.SAVANNA);
		tag(BiomeTags.HAS_PILLAGER_OUTPOST)
				.addOptionalTag(TolkienTags.Biomes.SAVANNA);
	}

	@Nonnull
	@Override
	public String getName() {
		return "Tolkienmobs - Biome Tags";
	}
}