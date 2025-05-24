package com.greatorator.tolkienmobs.datagen.tags;

import com.greatorator.tolkienmobs.init.TolkienBiomes;
import com.greatorator.tolkienmobs.init.TolkienTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienBiomeTagProvider extends BiomeTagsProvider {
	public TolkienBiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper helper) {
		super(output, provider, MODID, helper);
	}

	@Override
	protected void addTags(HolderLookup.@NotNull Provider provider) {
		TolkienBiomes.BIOME_FACTORIES.keySet().stream().sorted().toList().forEach(biome -> {
			tag(TolkienTags.Biomes.OVERWORLD).add(biome);
			float temperature = provider.asGetterLookup().lookupOrThrow(Registries.BIOME).getOrThrow(biome).value().getBaseTemperature();
			if (temperature > 0.8F) tag(TolkienTags.Biomes.HOT).add(biome);
			else if (temperature < 0.5F) tag(TolkienTags.Biomes.COLD).add(biome);
			else tag(TolkienTags.Biomes.TEMPERATE).add(biome);
		});
		TolkienBiomes.BIOMES_BY_TAG.forEach((tag, biome) -> tag(tag).add(biome));

		tag(BiomeTags.IS_OVERWORLD)
				.addTag(TolkienTags.Biomes.OVERWORLD);

		tag(Tags.Biomes.IS_HOT_OVERWORLD)
				.addTag(TolkienTags.Biomes.HOT);
		tag(Tags.Biomes.IS_COLD_OVERWORLD)
				.addTag(TolkienTags.Biomes.COLD);
		tag(Tags.Biomes.IS_TEMPERATE_OVERWORLD)
				.addTag(TolkienTags.Biomes.TEMPERATE);
		tag(Tags.Biomes.IS_WET_OVERWORLD)
				.addTag(TolkienTags.Biomes.WET);
		tag(Tags.Biomes.IS_DRY_OVERWORLD)
				.addTag(TolkienTags.Biomes.DRY);
		tag(Tags.Biomes.IS_SPARSE_VEGETATION_OVERWORLD)
				.addTag(TolkienTags.Biomes.SPARSE);
		tag(Tags.Biomes.IS_DENSE_VEGETATION_OVERWORLD)
				.addTag(TolkienTags.Biomes.DENSE);

		tag(Tags.Biomes.IS_PLAINS)
				.addTag(TolkienTags.Biomes.PLAINS);
		tag(BiomeTags.IS_FOREST)
				.addTag(TolkienTags.Biomes.FOREST);
		tag(BiomeTags.IS_TAIGA)
				.addTag(TolkienTags.Biomes.TAIGA);
		tag(Tags.Biomes.IS_DESERT)
				.addTag(TolkienTags.Biomes.DESERT);
		tag(BiomeTags.IS_SAVANNA)
				.addTag(TolkienTags.Biomes.SAVANNA);
		tag(BiomeTags.IS_JUNGLE)
				.addTag(TolkienTags.Biomes.JUNGLE);
		tag(BiomeTags.IS_MOUNTAIN)
				.addTag(TolkienTags.Biomes.MOUNTAIN);
		tag(Tags.Biomes.IS_MOUNTAIN)
				.addTag(TolkienTags.Biomes.MOUNTAIN);
		tag(BiomeTags.IS_BEACH)
				.addTag(TolkienTags.Biomes.BEACH);
		tag(BiomeTags.IS_BADLANDS)
				.addTag(TolkienTags.Biomes.BADLANDS);
		tag(Tags.Biomes.IS_MOUNTAIN_SLOPE)
				.addTag(TolkienTags.Biomes.SLOPE);
		tag(Tags.Biomes.IS_MOUNTAIN_PEAK)
				.addTag(TolkienTags.Biomes.PEAK);
		tag(Tags.Biomes.IS_SWAMP)
				.addTag(TolkienTags.Biomes.SWAMP);
		tag(Tags.Biomes.IS_SANDY)
				.addTag(TolkienTags.Biomes.SANDY);
		tag(Tags.Biomes.IS_WINDSWEPT)
				.addTag(TolkienTags.Biomes.WINDSWEPT);
		tag(Tags.Biomes.IS_SNOWY)
				.addTag(TolkienTags.Biomes.SNOWY);
		tag(Tags.Biomes.IS_ICY)
				.addTag(TolkienTags.Biomes.ICY);
		tag(Tags.Biomes.IS_FLORAL)
				.addTag(TolkienTags.Biomes.FLORAL);
		tag(Tags.Biomes.IS_DEAD)
				.addTag(TolkienTags.Biomes.DEAD);
		tag(BiomeTags.IS_OCEAN)
				.addTag(TolkienTags.Biomes.OCEAN);
		tag(Tags.Biomes.IS_CONIFEROUS_TREE)
				.addTag(TolkienTags.Biomes.CONIFEROUS);
		tag(Tags.Biomes.IS_OLD_GROWTH)
				.addTag(TolkienTags.Biomes.DARK_FOREST);
		tag(Tags.Biomes.IS_WASTELAND)
				.addTag(TolkienTags.Biomes.WASTELAND);
		tag(Tags.Biomes.IS_MAGICAL)
				.addTag(TolkienTags.Biomes.MAGICAL);
		tag(Tags.Biomes.IS_RIVER)
				.addTag(TolkienTags.Biomes.RIVER);
		tag(Tags.Biomes.IS_CAVE)
				.addTag(TolkienTags.Biomes.CAVE);

		tag(TolkienTags.Biomes.WET)
				.addTag(TolkienTags.Biomes.SWAMP)
				.addTag(TolkienTags.Biomes.RIVER)
				.addTag(TolkienTags.Biomes.BEACH);
		tag(TolkienTags.Biomes.DENSE)
				.addTag(TolkienTags.Biomes.DARK_FOREST)
				.addTag(TolkienTags.Biomes.FOREST)
				.addTag(TolkienTags.Biomes.JUNGLE)
				.addTag(TolkienTags.Biomes.SWAMP);
		tag(TolkienTags.Biomes.DRY)
				.addTag(TolkienTags.Biomes.DESERT);

		tag(BiomeTags.HAS_SWAMP_HUT)
				.addTag(TolkienTags.Biomes.SWAMP);
		tag(BiomeTags.HAS_JUNGLE_TEMPLE)
				.addTag(TolkienTags.Biomes.JUNGLE);
		tag(BiomeTags.HAS_RUINED_PORTAL_OCEAN)
				.addTag(TolkienTags.Biomes.OCEAN);
		tag(BiomeTags.HAS_SHIPWRECK)
				.addTag(TolkienTags.Biomes.OCEAN);
		tag(BiomeTags.HAS_OCEAN_RUIN_WARM)
				.addTag(TolkienTags.Biomes.OCEAN);
		tag(BiomeTags.HAS_VILLAGE_SAVANNA)
				.addTag(TolkienTags.Biomes.SAVANNA);
		tag(BiomeTags.HAS_PILLAGER_OUTPOST)
				.addTag(TolkienTags.Biomes.SAVANNA);
	}

	@Nonnull
	@Override
	public String getName() {
		return "Tolkienmobs - Biome Tags";
	}
}