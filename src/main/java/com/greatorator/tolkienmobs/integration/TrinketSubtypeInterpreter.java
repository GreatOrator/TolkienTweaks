package com.greatorator.tolkienmobs.integration;

import com.greatorator.tolkienmobs.handler.data.TrinketComponent;
import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TrinketSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
	public String apply(ItemStack stack, UidContext context) {
		TrinketComponent potionContents = stack.getOrDefault(TolkienDataComponents.POTION_CONTENTS, TrinketComponent.EMPTY);
		Optional<Holder<Potion>> potionType = potionContents.potion();

		if (context != UidContext.Recipe) {
			if (!potionContents.hasEffects()) {
				return "";
			}
			MobEffectInstance mobEffectInstance = potionType.get().value().getEffects().getFirst();
			return mobEffectInstance.getEffect().getKey().location() + "@" + mobEffectInstance.getAmplifier() + "@" + mobEffectInstance.getDuration();
		}
		return "";
	}

	@Override
	public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
		String data = apply(ingredient, context);
		return data.isEmpty() ? null : data;
	}

	@Override
	public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
		return apply(ingredient, context);
	}
}
