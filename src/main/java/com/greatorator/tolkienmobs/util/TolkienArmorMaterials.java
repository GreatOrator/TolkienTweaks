package com.greatorator.tolkienmobs.util;

import com.greatorator.tolkienmobs.init.TolkienItems;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienArmorMaterials {
	public static final Holder<ArmorMaterial> MITHRIL_ARMOR_MATERIAL = register("mithril",
			Util.make(new EnumMap<>(ArmorItem.Type.class), attribute -> {
				attribute.put(ArmorItem.Type.BOOTS, 5);
				attribute.put(ArmorItem.Type.LEGGINGS, 8);
				attribute.put(ArmorItem.Type.CHESTPLATE, 10);
				attribute.put(ArmorItem.Type.HELMET, 5);
				attribute.put(ArmorItem.Type.BODY, 13);
			}), 30, 4f, 0.6f, () -> TolkienItems.INGOT_MITHRIL.get());
	public static final Holder<ArmorMaterial> MORGULIRON_ARMOR_MATERIAL = register("morguliron",
			Util.make(new EnumMap<>(ArmorItem.Type.class), attribute -> {
				attribute.put(ArmorItem.Type.BOOTS, 4);
				attribute.put(ArmorItem.Type.LEGGINGS, 7);
				attribute.put(ArmorItem.Type.CHESTPLATE, 9);
				attribute.put(ArmorItem.Type.HELMET, 4);
				attribute.put(ArmorItem.Type.BODY, 12);
			}), 15, 3.5f, 0.3f, () -> TolkienItems.INGOT_MORGULIRON.get());
	public static final Holder<ArmorMaterial> AMMOLITE_ARMOR_MATERIAL = register("ammolite",
			Util.make(new EnumMap<>(ArmorItem.Type.class), attribute -> {
				attribute.put(ArmorItem.Type.BOOTS, 6);
				attribute.put(ArmorItem.Type.LEGGINGS, 9);
				attribute.put(ArmorItem.Type.CHESTPLATE, 11);
				attribute.put(ArmorItem.Type.HELMET, 6);
				attribute.put(ArmorItem.Type.BODY, 14);
			}), 35, 5f, 0.6f, () -> TolkienItems.GEM_AMMOLITE.get());


	private static Holder<ArmorMaterial> register(String name, EnumMap<ArmorItem.Type, Integer> typeProtection, int enchantability, float toughness, float knockbackResistance, Supplier<Item> ingredientItem) {
		ResourceLocation location = ResourceLocation.fromNamespaceAndPath(MODID, name);
		Holder<SoundEvent> equipSound = SoundEvents.ARMOR_EQUIP_NETHERITE;
		Supplier<Ingredient> ingredient = () -> Ingredient.of(ingredientItem.get());
		List<ArmorMaterial.Layer> layers = List.of(new ArmorMaterial.Layer(location));

		EnumMap<ArmorItem.Type, Integer> typeMap = new EnumMap<>(ArmorItem.Type.class);
		for (ArmorItem.Type type : ArmorItem.Type.values()) {
			typeMap.put(type, typeProtection.get(type));
		}

		return Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL, location,
				new ArmorMaterial(typeProtection, enchantability, equipSound, ingredient, layers, toughness, knockbackResistance));
	}
}
