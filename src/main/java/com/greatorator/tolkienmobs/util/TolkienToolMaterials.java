package com.greatorator.tolkienmobs.util;

import com.greatorator.tolkienmobs.init.TolkienItems;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

public class TolkienToolMaterials {
	public static final Tier MITHRIL = new SimpleTier(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 2048, 8.0F, 4, 35, () -> Ingredient.of(TolkienItems.INGOT_MITHRIL));
	public static final Tier MORGULIRON = new SimpleTier(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1536, 7.0F, 3, 20, () -> Ingredient.of(TolkienItems.INGOT_MORGULIRON));
	public static final Tier AMMOLITE = new SimpleTier(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1024, 9.0F, 5, 50, () -> Ingredient.of(TolkienItems.GEM_AMMOLITE));
}
