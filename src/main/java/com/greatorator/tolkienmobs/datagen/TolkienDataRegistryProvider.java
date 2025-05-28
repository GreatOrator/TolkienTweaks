package com.greatorator.tolkienmobs.datagen;

import com.greatorator.tolkienmobs.init.*;
import com.greatorator.tolkienmobs.world.TolkienBiomeModifiers;
import com.greatorator.tolkienmobs.world.components.config.TolkienConfiguredFeatures;
import com.greatorator.tolkienmobs.world.components.placements.TolkienPlacedFeatures;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienDataRegistryProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.BIOME, TolkienBiomes::bootstrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, TolkienBiomeModifiers::bootstrap)
            .add(Registries.CONFIGURED_FEATURE, TolkienConfiguredFeatures::bootstrap)
            .add(Registries.CONFIGURED_CARVER, TolkienCaveCarvers::bootstrap)


            .add(Registries.PLACED_FEATURE, TolkienPlacedFeatures::bootstrap)

            //structure
            //structure set
            //template pool

            .add(Registries.TRIM_MATERIAL, TolkienTrimMaterials::bootstrap)
            .add(Registries.ENCHANTMENT, TolkienEnchantments::bootstrap)
            .add(Registries.JUKEBOX_SONG, TolkienJukebox::bootstrap);


    public TolkienDataRegistryProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(MODID));
    }

    @Nonnull
    @Override
    public String getName() {
        return "Tolkienmobs - Biomes and Miscellaneous";
    }
}