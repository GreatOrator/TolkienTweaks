package com.greatorator.tolkienmobs.init;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;

import java.util.EnumMap;
import java.util.Map;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienArmorMaterials {
	/**
	 * 1.21.4 made armour materials plain records instead of registry entries, so the set is created here and the
	 * client-side look (the equipment asset under {@code assets/tolkienmobs/equipment/}) is named separately.
	 */
	public static final ResourceKey<EquipmentAsset> MITHRIL_ASSET = asset("mithril");
	public static final ResourceKey<EquipmentAsset> MORGULIRON_ASSET = asset("morguliron");
	public static final ResourceKey<EquipmentAsset> AMMOLITE_ASSET = asset("ammolite");

	public static final ArmorMaterial MITHRIL_ARMOR_MATERIAL = create(MITHRIL_ASSET, 41,
			defense(5, 10, 8, 5, 13), 30, 4f, 0.6f, TolkienTags.Items.MITHRIL_INGOTS);
	public static final ArmorMaterial MORGULIRON_ARMOR_MATERIAL = create(MORGULIRON_ASSET, 39,
			defense(4, 9, 7, 4, 12), 15, 3.5f, 0.3f, TolkienTags.Items.MORGULIRON_INGOTS);
	public static final ArmorMaterial AMMOLITE_ARMOR_MATERIAL = create(AMMOLITE_ASSET, 35,
			defense(6, 11, 9, 6, 14), 35, 5f, 0.6f, TolkienTags.Items.AMMOLITE_GEMS);

	private static ArmorMaterial create(ResourceKey<EquipmentAsset> asset, int durability, Map<ArmorType, Integer> defense,
			int enchantability, float toughness, float knockbackResistance, TagKey<Item> repairIngredient) {
		Holder<SoundEvent> equipSound = SoundEvents.ARMOR_EQUIP_NETHERITE;

		return new ArmorMaterial(durability, defense, enchantability, equipSound, toughness, knockbackResistance,
				repairIngredient, asset);
	}

	private static Map<ArmorType, Integer> defense(int helmet, int chestplate, int leggings, int boots, int body) {
		Map<ArmorType, Integer> defense = new EnumMap<>(ArmorType.class);
		defense.put(ArmorType.HELMET, helmet);
		defense.put(ArmorType.CHESTPLATE, chestplate);
		defense.put(ArmorType.LEGGINGS, leggings);
		defense.put(ArmorType.BOOTS, boots);
		defense.put(ArmorType.BODY, body);

		return defense;
	}

	private static ResourceKey<EquipmentAsset> asset(String name) {
		return ResourceKey.create(net.minecraft.world.item.equipment.EquipmentAssets.ROOT_ID,
				ResourceLocation.fromNamespaceAndPath(MODID, name));
	}
}
