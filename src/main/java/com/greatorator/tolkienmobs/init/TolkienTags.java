package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienTags {
    public static class Blocks {
        public static final TagKey<Block> ROOT_TRACE_SKIP = blockTag("tolkienmobs:root_trace_skip");
        public static final TagKey<Block> DECAY_GROW_BLOCK = blockTag("tolkienmobs:decay_grow_block");
        public static final TagKey<Block> BLACKLIST_HARVEST = blockTag("tolkienmobs:blacklist_harvest");
        public static final TagKey<Block> PORTAL_FRAME_BLOCKS = blockTag("tolkienmobs:portal_frame_blocks");
        public static final TagKey<Block> PLANTS_HANG_ON = blockTag("tolkienmobs:plants_hang_on");
        public static final TagKey<Block> TICK_SPEED_DENY = blockTag("tolkienmobs:tick_speed_deny");

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
        public static final TagKey<Item> CURIOS_RING = createTag("curios:ring");
        public static final TagKey<Item> CURIOS_CHARM = createTag("curios:charm");
        public static final TagKey<Item> CURIOS_BELT = createTag("curios:belt");
        public static final TagKey<Item> CURIOS_AMULET = createTag("curios:amulet");
        public static final TagKey<Item> CURIOS_HANDS = createTag("curios:hands");
        public static final TagKey<Item> CURIOS_BODY = createTag("curios:body");
        public static final TagKey<Item> CURIOS_HEAD = createTag("curios:head");

        private static TagKey<Item> createTag(String name) {
            return net.minecraft.tags.TagKey.create(Registries.ITEM, ResourceLocation.parse(name));
        }
    }

    public static class DamageTypes {
        private DamageTypes() {}

        public static final TagKey<DamageType> ILLUVATAR_DAMAGE_GROUP = tag("tolkienmobs:illuvatar_damage_group");

        private static TagKey<DamageType> tag(String name) {
            return net.minecraft.tags.TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(name));
        }
    }
}