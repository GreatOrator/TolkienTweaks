package com.greatorator.tolkienmobs.datagen.loot;

import com.greatorator.tolkienmobs.init.TolkienLootTables;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TolkienLootTableProvider extends LootTableProvider {
	public TolkienLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
		super(output, TolkienLootTables.allBuiltin(), List.of(
			new LootTableProvider.SubProviderEntry(TolkienBlockLootTableProvider::new, LootContextParamSets.BLOCK),
			new LootTableProvider.SubProviderEntry(TolkienEntityLootTables::new, LootContextParamSets.ENTITY),
			new LootTableProvider.SubProviderEntry(TolkienChestLootTables::new, LootContextParamSets.CHEST)
		), provider);
	}

	@Override
	protected void validate(WritableRegistry<LootTable> writableregistry, ValidationContext validationcontext, ProblemReporter.Collector problemreporter$collector) {

	}
}
