package com.greatorator.tolkienmobs.datagen.tags;

import com.greatorator.tolkienmobs.datagen.TolkienVillageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.concurrent.CompletableFuture;

public final class TolkienStructureTagProvider extends TagsProvider<Structure> {
    public TolkienStructureTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Registries.STRUCTURE, registries, "tolkienmobs", null);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        TolkienVillageProvider.VILLAGES_BY_STYLE.forEach(village -> tag(StructureTags.VILLAGE).add(village.structure()));
    }
}
