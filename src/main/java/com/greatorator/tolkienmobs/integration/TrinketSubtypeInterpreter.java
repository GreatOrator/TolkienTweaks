package com.greatorator.tolkienmobs.integration;

import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TrinketSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
	@Override
	public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
		return List.of(
				ingredient.getOrDefault(TolkienDataComponents.POTION_CONTENTS, 0L)
		);
	}

	@Override
	public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
		return null;
	}
}
