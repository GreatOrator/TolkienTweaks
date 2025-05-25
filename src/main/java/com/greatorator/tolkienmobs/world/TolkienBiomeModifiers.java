package com.greatorator.tolkienmobs.world;

import com.greatorator.tolkienmobs.init.TolkienBiomes;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;
import static com.greatorator.tolkienmobs.world.TolkienPlacedFeatures.*;

public class TolkienBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ADD_TREE_MALLORN = registerBiomeKey("tree/add_tree_mallorn");
    public static final ResourceKey<BiomeModifier> ADD_TREE_MIRKWOOD = registerBiomeKey("tree/add_tree_mirkwood");
    public static final ResourceKey<BiomeModifier> ADD_TREE_CULUMALDA = registerBiomeKey("tree/add_tree_culumalda");
    public static final ResourceKey<BiomeModifier> ADD_TREE_LEBETHRON = registerBiomeKey("tree/add_tree_lebethron");
    public static final ResourceKey<BiomeModifier> ADD_TREE_FANGORNOAK = registerBiomeKey("tree/add_tree_fangornoak");
    public static final ResourceKey<BiomeModifier> ADD_TREE_DEADWOOD = registerBiomeKey("tree/add_tree_deadwood");
    public static final ResourceKey<BiomeModifier> ADD_TREE_DWARVEN = registerBiomeKey("tree/add_tree_dwarven");
    public static final ResourceKey<BiomeModifier> ADD_TREE_OLDFORESTOAK = registerBiomeKey("tree/add_tree_oldforestoak");

    public static final ResourceKey<BiomeModifier> ADD_FLOWER_SIMBELMYNE = registerBiomeKey("plants/add_flower_simbelmyne");
    public static final ResourceKey<BiomeModifier> ADD_FLOWER_MIRKWOOD = registerBiomeKey("plants/add_flower_mirkwood");
    public static final ResourceKey<BiomeModifier> ADD_FLOWER_ALFIRIN = registerBiomeKey("plants/add_flower_alfirin");
    public static final ResourceKey<BiomeModifier> ADD_FLOWER_ATHELAS = registerBiomeKey("plants/add_flower_athelas");
    public static final ResourceKey<BiomeModifier> ADD_FLOWER_NIPHREDIL = registerBiomeKey("plants/add_flower_niphredil");
    public static final ResourceKey<BiomeModifier> ADD_FLOWER_SWAMPMILKWEED = registerBiomeKey("plants/add_flower_swamp_milkweed");
    public static final ResourceKey<BiomeModifier> ADD_FLOWER_LILLYOFTHEVALLEY = registerBiomeKey("plants/add_flower_valley_lilly");
    public static final ResourceKey<BiomeModifier> ADD_FLOWER_ELANOR = registerBiomeKey("plants/add_flower_elanor");
    public static final ResourceKey<BiomeModifier> ADD_FLOWER_AEGLOS = registerBiomeKey("plants/add_flower_aeglos");
    public static final ResourceKey<BiomeModifier> ADD_FLOWER_LISSUIN = registerBiomeKey("plants/add_flower_lissuin");
    public static final ResourceKey<BiomeModifier> ADD_FLOWER_MALLOS = registerBiomeKey("plants/add_flower_mallos");
    public static final ResourceKey<BiomeModifier> ADD_FLOWER_BRAMBLES = registerBiomeKey("plants/add_flower_brambles");

    public static final ResourceKey<BiomeModifier> ADD_MITHRIL_ORE = registerBiomeKey("ore/add_mithril_ore");
    public static final ResourceKey<BiomeModifier> ADD_NETHER_MITHRIL_ORE = registerBiomeKey("ore/add_nether_mithril_ore");
    public static final ResourceKey<BiomeModifier> ADD_END_MITHRIL_ORE = registerBiomeKey("ore/add_end_mithril_ore");
    public static final ResourceKey<BiomeModifier> ADD_MORGULIRON_ORE = registerBiomeKey("ore/add_morguliron_ore");
    public static final ResourceKey<BiomeModifier> ADD_NETHER_MORGULIRON_ORE = registerBiomeKey("ore/add_nether_morguliron_ore");
    public static final ResourceKey<BiomeModifier> ADD_END_MORGULIRON_ORE = registerBiomeKey("ore/add_end_morguliron_ore");
    public static final ResourceKey<BiomeModifier> ADD_AMMOLITE_ORE = registerBiomeKey("ore/add_ammolite_ore");
    public static final ResourceKey<BiomeModifier> ADD_NETHER_AMMOLITE_ORE = registerBiomeKey("ore/add_nether_ammolite_ore");
    public static final ResourceKey<BiomeModifier> ADD_END_AMMOLITE_ORE = registerBiomeKey("ore/add_end_ammolite_ore");

    public static final ResourceKey<BiomeModifier> SPAWN_GECKO = registerBiomeKey("mobs/spawn_gecko");
    public static final ResourceKey<BiomeModifier> SPAWN_RAT = registerBiomeKey("mobs/spawn_rat");
    public static final ResourceKey<BiomeModifier> SPAWN_SQUIRREL = registerBiomeKey("mobs/spawn_squirrel");
    public static final ResourceKey<BiomeModifier> SPAWN_FROG = registerBiomeKey("mobs/spawn_frog");

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        context.register(ADD_TREE_MALLORN, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(TolkienBiomes.LORINAND)),
                HolderSet.direct(placedFeatures.getOrThrow(HARDENED_MALLORN_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_TREE_MIRKWOOD, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(TolkienBiomes.MIRKWOOD), biomes.getOrThrow(TolkienBiomes.MIRKWOOD_SPOOKY)),
                HolderSet.direct(placedFeatures.getOrThrow(HARDENED_MIRKWOOD_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_TREE_CULUMALDA, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(TolkienBiomes.SHIRE), biomes.getOrThrow(TolkienBiomes.FIRIEN)),
                HolderSet.direct(placedFeatures.getOrThrow(HARDENED_CULUMALDA_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_TREE_LEBETHRON, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(TolkienBiomes.SHIRE), biomes.getOrThrow(TolkienBiomes.FIRIEN)),
                HolderSet.direct(placedFeatures.getOrThrow(HARDENED_LEBETHRON_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_TREE_FANGORNOAK, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(TolkienBiomes.FANGORN)),
                HolderSet.direct(placedFeatures.getOrThrow(HARDENED_FANGORNOAK_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_TREE_DEADWOOD, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(TolkienBiomes.MORDOR), biomes.getOrThrow(TolkienBiomes.BARROW_DOWNS)),
                HolderSet.direct(placedFeatures.getOrThrow(DEADWOOD_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_TREE_OLDFORESTOAK, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(TolkienBiomes.OLD_FOREST)),
                HolderSet.direct(placedFeatures.getOrThrow(OLDFORESTOAK_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_TREE_DWARVEN, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(TolkienBiomes.IRON_HILLS), biomes.getOrThrow(TolkienBiomes.HITHAEGLIR)),
                HolderSet.direct(placedFeatures.getOrThrow(HARDENED_DWARVEN_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(ADD_MITHRIL_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(TolkienBiomes.MORDOR)),
                HolderSet.direct(placedFeatures.getOrThrow(MITHRIL_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
        context.register(ADD_NETHER_MITHRIL_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_NETHER),
                HolderSet.direct(placedFeatures.getOrThrow(TolkienPlacedFeatures.NETHER_MITHRIL_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
        context.register(ADD_END_MITHRIL_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_END),
                HolderSet.direct(placedFeatures.getOrThrow(TolkienPlacedFeatures.END_MITHRIL_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
        context.register(ADD_MORGULIRON_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.BADLANDS)),
                HolderSet.direct(placedFeatures.getOrThrow(MORGULIRON_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
        context.register(ADD_NETHER_MORGULIRON_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_NETHER),
                HolderSet.direct(placedFeatures.getOrThrow(TolkienPlacedFeatures.NETHER_MORGULIRON_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
        context.register(ADD_END_MORGULIRON_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_END),
                HolderSet.direct(placedFeatures.getOrThrow(TolkienPlacedFeatures.END_MORGULIRON_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
        context.register(ADD_AMMOLITE_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.DARK_FOREST)),
                HolderSet.direct(placedFeatures.getOrThrow(AMMOLITE_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
        context.register(ADD_NETHER_AMMOLITE_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_NETHER),
                HolderSet.direct(placedFeatures.getOrThrow(TolkienPlacedFeatures.NETHER_AMMOLITE_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
        context.register(ADD_END_AMMOLITE_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_END),
                HolderSet.direct(placedFeatures.getOrThrow(TolkienPlacedFeatures.END_AMMOLITE_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_FLOWER_SIMBELMYNE, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS)),
                HolderSet.direct(placedFeatures.getOrThrow(FLOWER_SIMBELMYNE_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_FLOWER_MIRKWOOD, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS)),
                HolderSet.direct(placedFeatures.getOrThrow(FLOWER_MIRKWOOD_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_FLOWER_ALFIRIN, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS)),
                HolderSet.direct(placedFeatures.getOrThrow(FLOWER_ALFIRIN_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_FLOWER_ATHELAS, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS)),
                HolderSet.direct(placedFeatures.getOrThrow(FLOWER_ATHELAS_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_FLOWER_NIPHREDIL, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS)),
                HolderSet.direct(placedFeatures.getOrThrow(FLOWER_NIPHREDIL_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_FLOWER_SWAMPMILKWEED, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS)),
                HolderSet.direct(placedFeatures.getOrThrow(FLOWER_SWAMPMILKWEED_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_FLOWER_LILLYOFTHEVALLEY, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS)),
                HolderSet.direct(placedFeatures.getOrThrow(FLOWER_LILLYOFTHEVALLEY_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_FLOWER_ELANOR, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS)),
                HolderSet.direct(placedFeatures.getOrThrow(FLOWER_ELANOR_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_FLOWER_AEGLOS, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS)),
                HolderSet.direct(placedFeatures.getOrThrow(FLOWER_AEGLOS_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_FLOWER_LISSUIN, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS)),
                HolderSet.direct(placedFeatures.getOrThrow(FLOWER_LISSUIN_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_FLOWER_MALLOS, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS)),
                HolderSet.direct(placedFeatures.getOrThrow(FLOWER_MALLOS_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_FLOWER_BRAMBLES, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS)),
                HolderSet.direct(placedFeatures.getOrThrow(FLOWER_BRAMBLES_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(SPAWN_GECKO, new BiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS)),
                List.of(new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_GECKO.get(), 20, 2, 4))));
        context.register(SPAWN_RAT, new BiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS)),
                List.of(new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_RAT.get(), 20, 2, 4))));
        context.register(SPAWN_SQUIRREL, new BiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS)),
                List.of(new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), 20, 2, 4))));
        context.register(SPAWN_FROG, new BiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS)),
                List.of(new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_FROG.get(), 20, 2, 4))));
    }

    private static ResourceKey<BiomeModifier> registerBiomeKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(MODID, name));
    }
}