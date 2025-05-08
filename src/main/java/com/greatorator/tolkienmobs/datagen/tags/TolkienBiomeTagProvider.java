package com.greatorator.tolkienmobs.datagen.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienBiomeTagProvider extends BiomeTagsProvider {
	public TolkienBiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper helper) {
		super(output, provider, MODID, helper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
//		this.tag(TolkienTags.Biomes.IS_MOUNTAIN)
//				.add(TolkienBiomes.IRON_HILLS)
//				.add(TolkienBiomes.HITHAEGLIR)
//				.add(TolkienBiomes.ASH_MOUNTAINS);
	}

	@Nonnull
	@Override
	public String getName() {
		return "Tolkienmobs - Biome Tags";
	}
}