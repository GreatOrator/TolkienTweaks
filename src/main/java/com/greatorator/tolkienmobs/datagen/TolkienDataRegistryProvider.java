package com.greatorator.tolkienmobs.datagen;

import com.greatorator.tolkienmobs.init.TolkienEnchantments;
import com.greatorator.tolkienmobs.init.TolkienJukebox;
import com.greatorator.tolkienmobs.world.TolkienBiomeModifiers;
import com.greatorator.tolkienmobs.world.TolkienConfiguredFeatures;
import com.greatorator.tolkienmobs.world.TolkienPlacedFeatures;
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
            .add(Registries.CONFIGURED_FEATURE, TolkienConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, TolkienPlacedFeatures::bootstrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, TolkienBiomeModifiers::bootstrap)
            .add(Registries.JUKEBOX_SONG, TolkienJukebox::bootstrap)
            .add(Registries.ENCHANTMENT, TolkienEnchantments::bootstrap);

    public TolkienDataRegistryProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(MODID));
    }
}