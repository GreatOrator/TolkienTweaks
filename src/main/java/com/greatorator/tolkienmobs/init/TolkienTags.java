package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class TolkienTags {
    public static class Blocks {
        public static final TagKey<Block> ROOT_TRACE_SKIP = blockTag("tolkienmobs:root_trace_skip");
        public static final TagKey<Block> DECAY_GROW_BLOCK = blockTag("tolkienmobs:decay_grow_block");
        public static final TagKey<Block> BLACKLIST_HARVEST = blockTag("tolkienmobs:blacklist_harvest");
        public static final TagKey<Block> PORTAL_FRAME_BLOCKS = blockTag("tolkienmobs:portal_frame_blocks");
        public static final TagKey<Block> PLANTS_HANG_ON = blockTag("tolkienmobs:plants_hang_on");
        public static final TagKey<Block> TICK_SPEED_DENY = blockTag("tolkienmobs:tick_speed_deny");
        public static final TagKey<Block> TILLABLES = blockTag("tolkienmobs:farming");
        public static final TagKey<Block> CARVER_REPLACEABLES = blockTag("tolkienmobs:carver_replaceables");
        public static final TagKey<Block> MALLORN_LOGS = blockTag("tolkienmobs:mallorn_logs");
        public static final TagKey<Block> MIRKWOOD_LOGS = blockTag("tolkienmobs:mirkwood_logs");
        public static final TagKey<Block> CULUMALDA_LOGS = blockTag("tolkienmobs:culumalda_logs");
        public static final TagKey<Block> LEBETHRON_LOGS = blockTag("tolkienmobs:lebethron_logs");
        public static final TagKey<Block> FANGORNOAK_LOGS = blockTag("tolkienmobs:fangornoak_logs");
        public static final TagKey<Block> DEADWOOD_LOGS = blockTag("tolkienmobs:deadwood_logs");
        public static final TagKey<Block> DWARVEN_MAPLE_LOGS = blockTag("tolkienmobs:dwarven_maple_logs");
        public static final TagKey<Block> MITHRIL_ORES = blockTag("tolkienmobs:mithril_ores");
        public static final TagKey<Block> MORGULIRON_ORES = blockTag("tolkienmobs:morguliron_ores");
        public static final TagKey<Block> AMMOLITE_ORES = blockTag("tolkienmobs:ammolite_ores");

        private static TagKey<Block> blockTag(String name) {
            return net.minecraft.tags.TagKey.create(Registries.BLOCK, ResourceLocation.parse(name));
        }
    }

    public static class Items {
        public static final TagKey<Item> SLEEPING_BAG = createTag("tolkienmobs:sleeping_bag");
        public static final TagKey<Item> COINS = createTag("tolkienmobs:coins");
        public static final TagKey<Item> KEYS = createTag("tolkienmobs:keys");
        public static final TagKey<Item> TRINKET = createTag("tolkienmobs:trinket");
        public static final TagKey<Item> INSECTS = createTag("tolkienmobs:insects");
        public static final TagKey<Item> ACORNS = createTag("tolkienmobs:acorns");
        public static final TagKey<Item> UPGRADES = createTag("tolkienmobs:upgrades");
        public static final TagKey<Item> CANNOT_AUTO_CONSUME = createTag("tolkienmobs:eating");
        public static final TagKey<Item> CURIOS_RING = createTag("curios:ring");
        public static final TagKey<Item> CURIOS_CHARM = createTag("curios:charm");
        public static final TagKey<Item> CURIOS_BELT = createTag("curios:belt");
        public static final TagKey<Item> CURIOS_AMULET = createTag("curios:amulet");
        public static final TagKey<Item> CURIOS_HANDS = createTag("curios:hands");
        public static final TagKey<Item> CURIOS_BODY = createTag("curios:body");
        public static final TagKey<Item> CURIOS_HEAD = createTag("curios:head");

        private static TagKey<Item> createTag(String name) {
            return TagKey.create(Registries.ITEM, ResourceLocation.parse(name));
        }
    }

    public static class Biomes {
        public static final TagKey<Biome> IS_ARDA = createTag("is_arda");

        public static final TagKey<Biome> OVERWORLD = createTag("overworld");
        public static final TagKey<Biome> HOT = createTag("climate/hot");
        public static final TagKey<Biome> TEMPERATE = createTag("climate/temperate");
        public static final TagKey<Biome> COLD = createTag("climate/cold");
        public static final TagKey<Biome> WET = createTag("climate/wet");
        public static final TagKey<Biome> DRY = createTag("climate/dry");
        public static final TagKey<Biome> SPARSE = createTag("density/sparse");
        public static final TagKey<Biome> DENSE = createTag("density/dense");
        public static final TagKey<Biome> PLAINS = createTag("plains");
        public static final TagKey<Biome> FOREST = createTag("forest");
        public static final TagKey<Biome> TAIGA = createTag("taiga");
        public static final TagKey<Biome> DESERT = createTag("desert");
        public static final TagKey<Biome> SAVANNA = createTag("savanna");
        public static final TagKey<Biome> JUNGLE = createTag("jungle");
        public static final TagKey<Biome> BEACH = createTag("beach");
        public static final TagKey<Biome> SWAMP = createTag("swamp");
        public static final TagKey<Biome> SLOPE = createTag("slope");
        public static final TagKey<Biome> PEAK = createTag("peak");
        public static final TagKey<Biome> MOUNTAIN = createTag("mountain");
        public static final TagKey<Biome> SNOWY = createTag("snowy");
        public static final TagKey<Biome> ICY = createTag("icy");
        public static final TagKey<Biome> BADLANDS = createTag("badlands");
        public static final TagKey<Biome> SANDY = createTag("sandy");
        public static final TagKey<Biome> FLORAL = createTag("floral");
        public static final TagKey<Biome> CONIFEROUS = createTag("coniferous");
        public static final TagKey<Biome> DEAD = createTag("dead");
        public static final TagKey<Biome> WASTELAND = createTag("wasteland");
        public static final TagKey<Biome> MAGICAL = createTag("magical");
        public static final TagKey<Biome> WINDSWEPT = createTag("windswept");
        public static final TagKey<Biome> OCEAN = createTag("ocean");
        public static final TagKey<Biome> DARK_FOREST = createTag("dark_forest");
        public static final TagKey<Biome> RIVER = createTag("river");
        public static final TagKey<Biome> CAVE = createTag("cave");

        private static TagKey<Biome> createTag(String name) {
            return TagKey.create(Registries.BIOME, TolkienMobsMain.resLoc(name));
        }

    }

    public static class DamageTypes {

        public static final TagKey<DamageType> FELL_BEAST_DAMAGE = tag("tolkienmobs:fell_beast_damage");
        public static final TagKey<DamageType> HALLOWED_DAMAGE = tag("tolkienmobs:hallowed_damage");

        private static TagKey<DamageType> tag(String name) {
            return TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(name));
        }
    }

    public static class Entities {

        public static final TagKey<EntityType<?>> BOSSES = createTag("tolkienmobs:bosses");

        private static TagKey<EntityType<?>> createTag(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse(name));
        }
    }
}