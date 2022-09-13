package com.greatorator.tolkientweaks.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public class TTTags {
    public static void init(){
        items.init();
        blocks.init();
        tagkeys.init();
    }

    public static class items {
        private static void init() {
        }
        public static final Tags.IOptionalNamedTag<Item> COINS = tag("coins");

        private static Tags.IOptionalNamedTag<Item> tag(String name) {
            return ItemTags.createOptional(new ResourceLocation("forge", name));
        }
    }

    public static class blocks {
        private static void init() {
        }
        private static Tags.IOptionalNamedTag<Block> tag(String name) {
            return BlockTags.createOptional(new ResourceLocation("forge", name));
        }
    }

    public static class tagkeys{
        private static void init(){}

        public static final Tags.IOptionalNamedTag<Item> BLACKLISTED_ITEMS = tag("blacklisted_items");
        public static final Tags.IOptionalNamedTag<Item> ALLOWED_FLUIDS = tag("allowed_fluids");

        private static Tags.IOptionalNamedTag<Item> tag(String name) {
            return ItemTags.createOptional(new ResourceLocation("forge", name));
        }
    }

}
