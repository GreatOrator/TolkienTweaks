package com.greatorator.tolkienmobs.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;

/**
 * 26.1 dropped the animal-armour class: {@code horseArmor} puts the body attributes, the equip sound and
 * the horses-only rule on the properties, so nothing is left for the item itself to add.
 */
public class TolkienAnimalArmorItem extends Item {
    public TolkienAnimalArmorItem(ArmorMaterial armorMaterial, Properties properties) {
        super(properties.horseArmor(armorMaterial));
    }
}
