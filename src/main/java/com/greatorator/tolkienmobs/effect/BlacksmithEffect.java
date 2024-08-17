package com.greatorator.tolkienmobs.effect;

import com.google.common.collect.Lists;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BlacksmithEffect extends TolkienEffect {
    public static BlacksmithEffect instance = null;
    public static int damageTime = 20;

    public BlacksmithEffect(MobEffectCategory category, int color) {
        super(category, color);
        instance = this;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        List<ItemStack> equipment;
        if (entity instanceof Player) {
            equipment = new ArrayList<>();
            Player player = (Player) entity;
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                equipment.add(player.getInventory().getItem(i));
            }
        } else {
            equipment = Lists.newArrayList(entity.getAllSlots());
        }

        equipment.removeIf(stack -> stack.isEmpty() || !stack.isDamaged());

        if (equipment.isEmpty())
            return false;

        ItemStack stack = equipment.get(entity.level().random.nextInt(equipment.size()));

        if (stack.getDamageValue() - (amplifier + 1) < 0) {
            stack.setDamageValue(0);
        } else {
            stack.setDamageValue(stack.getDamageValue() - (amplifier + 1));
        }
        return false;
    }
}
