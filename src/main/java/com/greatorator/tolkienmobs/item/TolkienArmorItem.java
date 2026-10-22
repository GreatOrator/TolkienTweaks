package com.greatorator.tolkienmobs.item;

import com.greatorator.tolkienmobs.init.TolkienArmorMaterials;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;

public class TolkienArmorItem extends ArmorItem {
    private static final Map<ResourceKey<EquipmentAsset>, List<MobEffectInstance>> MATERIAL_TO_EFFECT_MAP = Map.of(
            TolkienArmorMaterials.MITHRIL_ASSET, List.of(new MobEffectInstance(MobEffects.REGENERATION, 200, 2, false, false), new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 1, false, false)),
            TolkienArmorMaterials.MORGULIRON_ASSET, List.of(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 4, false, false), new MobEffectInstance(MobEffects.ABSORPTION, 200, 1, false, false)),
            TolkienArmorMaterials.AMMOLITE_ASSET, List.of(new MobEffectInstance(MobEffects.DIG_SPEED, 200, 4, false, false), new MobEffectInstance(MobEffects.ABSORPTION, 200, 1, false, false), new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 2, false, false)));

    public TolkienArmorItem(ArmorMaterial material, ArmorType type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if(entity instanceof Player player && !level.isClientSide() && hasFullSuitOfArmorOn(player)) {
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
     * 1.21.4 dropped ArmorItem#getMaterial; the worn set is identified by the equipment asset each piece renders with.
     */
    private boolean hasPlayerCorrectArmorOn(ResourceKey<EquipmentAsset> mapArmorMaterial, Player player) {
        for(ItemStack armorStack : player.getArmorSlots()) {
            Equippable equippable = armorStack.get(DataComponents.EQUIPPABLE);

            if (equippable == null || equippable.assetId().isEmpty() || !equippable.assetId().get().equals(mapArmorMaterial)) {
                return false;
            }
        }

        return true;
    }

    private boolean hasFullSuitOfArmorOn(Player player) {
        ItemStack boots = player.getInventory().getArmor(0);
        ItemStack leggings = player.getInventory().getArmor(1);
        ItemStack chestplate = player.getInventory().getArmor(2);
        ItemStack helmet = player.getInventory().getArmor(3);

        return !boots.isEmpty() && !leggings.isEmpty() && !chestplate.isEmpty() && !helmet.isEmpty();
    }
}
