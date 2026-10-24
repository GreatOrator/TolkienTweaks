package com.greatorator.tolkienmobs.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class CorrosionEffect extends TolkienEffect {
    public static CorrosionEffect instance = null;
    private static final Predicate<ItemStack> CAN_REPAIR_ITEM = stack -> !stack.isEmpty() && isRepairableDamagedItem(stack);
    public static int damageTime = 10;

    public CorrosionEffect(MobEffectCategory category, int color) {
        super(category, color);
        instance = this;
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
        boolean hasAction = false;

        if (entity instanceof Player) {
            repairAllItems((Player) entity, amplifier);
            if (!hasAction) {
                hasAction = true;
            }
        }
        return hasAction;
    }

    public static boolean isRepairableDamagedItem(ItemStack stack) {
        // isRepairable() is gone: an item that can be repaired by combining two of itself answers
        // isCombineRepairable(), which already covers being damageable.
        return stack.isCombineRepairable() && stack.getDamageValue() > 0;
    }

    private static void repairAllItems(Player player, int amplifier) {
        Predicate<ItemStack> canRepairPlayerItem = CAN_REPAIR_ITEM.and(stack -> stack != player.getMainHandItem() || !player.swinging);
        repairAllItems(player.getInventory(), canRepairPlayerItem, amplifier);
    }

    private static boolean repairAllItems(Inventory inv, Predicate<ItemStack> canRepairStack, int amplifier) {
        boolean hasAction = false;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack invStack = inv.getItem(i);
            if (canRepairStack.test(invStack) && invStack.isCombineRepairable()) {
                invStack.setDamageValue(invStack.getDamageValue() + (amplifier + 1));
                if (!hasAction) {
                    hasAction = true;
                }
            }
        }
        return hasAction;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % damageTime == 0;
    }
}
