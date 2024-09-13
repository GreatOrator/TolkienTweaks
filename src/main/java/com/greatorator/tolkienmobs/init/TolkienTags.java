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
        public static final TagKey<Block> ROOT_TRACE_SKIP = createTag("root_trace_skip");
        public static final TagKey<Block> DECAY_GROW_BLOCK = createTag("decay_grow_block");

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(MODID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> SLEEPING_BAG = createTag("sleeping_bag");
        public static final TagKey<Item> COINS = createTag("coins");
        public static final TagKey<Item> KEYS = createTag("keys");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(MODID, name));
        }
    }

    public static class DamageTypes {
        private DamageTypes() {}

        public static final TagKey<DamageType> ILLUVATAR_DAMAGE_GROUP = tag("illuvatar_damage_group");

        private static TagKey<DamageType> tag(String name) {
            return TagKey.create(Registries.DAMAGE_TYPE, TolkienMobsMain.resLoc(name));
        }
    }
}