package com.greatorator.tolkienmobs.datagen;

import com.greatorator.tolkienmobs.datagen.world.TolkienWorldBiomeProvider;
import com.greatorator.tolkienmobs.datagen.world.TolkienWorldConfigurationProvider;
import com.greatorator.tolkienmobs.datagen.world.TolkienWorldPlacementProvider;
import com.greatorator.tolkienmobs.init.TolkienBiomes;
import com.greatorator.tolkienmobs.init.TolkienEnchantments;
import com.greatorator.tolkienmobs.init.TolkienJukebox;
import com.greatorator.tolkienmobs.init.TolkienTrimMaterials;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienDataRegistryProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, TolkienWorldConfigurationProvider::bootstrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, TolkienWorldBiomeProvider::bootstrap)
            .add(Registries.PLACED_FEATURE, TolkienWorldPlacementProvider::bootstrap)
            .add(Registries.BIOME, TolkienBiomes::bootstrap)
            .add(Registries.TRIM_MATERIAL, TolkienTrimMaterials::bootstrap)
            .add(Registries.JUKEBOX_SONG, TolkienJukebox::bootstrap)
            .add(Registries.ENCHANTMENT, TolkienEnchantments::bootstrap);

    public TolkienDataRegistryProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(MODID));
    }
}