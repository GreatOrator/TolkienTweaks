package com.greatorator.tolkienmobs.world.registration;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Adds a Tolkien region alongside the vanilla region, retaining vanilla climate and terrain shapes. */
public class TolkienOverworldRegion extends Region {
    /**
     * TerraBlender weighs the vanilla overworld region at 50 by default, so the mod's share of the
     * world is {@code weight / (weight + 50)}: 2 gave about 4%, and 7 gives about 12%.
     */
    private static final int WEIGHT = 7;

    public TolkienOverworldRegion() {
        super(TolkienMobsMain.resLoc("overworld"), RegionType.OVERWORLD, WEIGHT);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        List<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> own = new ArrayList<>();
        addModifiedVanillaOverworldBiomes(mapper, builder -> {
            TolkienClimate.SLOT_REPLACEMENTS.forEach(builder::replaceBiome);
            // The renamed slots only inherit a vanilla slot's size, so the biomes that inherited a
            // small one also take cells of the large ones. They go in after the modified list.
            own.addAll(TolkienClimate.carve(builder));
        });
        own.forEach(mapper);
    }
}
