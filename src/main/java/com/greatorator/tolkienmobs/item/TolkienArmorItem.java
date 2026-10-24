package com.greatorator.tolkienmobs.item;

import com.greatorator.tolkienmobs.init.TolkienArmorMaterials;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * 26.1 dropped the armour class: the durability, attributes, equip sound and repair ingredient are put
 * on the properties by {@code humanoidArmor}, so all that is left here is the worn-set effect.
 */
public class TolkienArmorItem extends Item {
    private static final Map<ResourceKey<EquipmentAsset>, List<MobEffectInstance>> MATERIAL_TO_EFFECT_MAP = Map.of(
            TolkienArmorMaterials.MITHRIL_ASSET, List.of(new MobEffectInstance(MobEffects.REGENERATION, 200, 2, false, false), new MobEffectInstance(MobEffects.SPEED, 200, 1, false, false)),
            TolkienArmorMaterials.MORGULIRON_ASSET, List.of(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 4, false, false), new MobEffectInstance(MobEffects.ABSORPTION, 200, 1, false, false)),
            TolkienArmorMaterials.AMMOLITE_ASSET, List.of(new MobEffectInstance(MobEffects.HASTE, 200, 4, false, false), new MobEffectInstance(MobEffects.ABSORPTION, 200, 1, false, false), new MobEffectInstance(MobEffects.STRENGTH, 200, 2, false, false)));

    public TolkienArmorItem(ArmorMaterial material, ArmorType type, Properties properties) {
        super(properties.humanoidArmor(material, type));
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        if(entity instanceof Player player && hasFullSuitOfArmorOn(player)) {
            evaluateArmorEffects(player);
        }
    }

    private void evaluateArmorEffects(Player player) {
        for(Map.Entry<ResourceKey<EquipmentAsset>, List<MobEffectInstance>> entry : MATERIAL_TO_EFFECT_MAP.entrySet()) {
            if(hasPlayerCorrectArmorOn(entry.getKey(), player)) {
                addEffectToPlayer(player, entry.getValue());
            }
        }
    }

    private void addEffectToPlayer(Player player, List<MobEffectInstance> mapEffect) {
        boolean hasPlayerEffect = mapEffect.stream().allMatch(effect -> player.hasEffect(effect.getEffect()));

        if(!hasPlayerEffect) {
            for (MobEffectInstance effect : mapEffect) {
                player.addEffect(new MobEffectInstance(effect.getEffect(),
                        effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.isVisible()));
            }
        }
    }

    /**
     * The worn set is identified by the equipment asset each piece renders with, which is the only thing
     * 26.1 keeps of the material a piece was made from.
     */
    private boolean hasPlayerCorrectArmorOn(ResourceKey<EquipmentAsset> mapArmorMaterial, Player player) {
        for(EquipmentSlot armorSlot : EquipmentSlotGroup.ARMOR) {
            Equippable equippable = player.getItemBySlot(armorSlot).get(DataComponents.EQUIPPABLE);

            if (equippable == null || equippable.assetId().isEmpty() || !equippable.assetId().get().equals(mapArmorMaterial)) {
                return false;
            }
        }

        return true;
    }

    private boolean hasFullSuitOfArmorOn(Player player) {
        for(EquipmentSlot armorSlot : EquipmentSlotGroup.ARMOR) {
            if (player.getItemBySlot(armorSlot).isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
