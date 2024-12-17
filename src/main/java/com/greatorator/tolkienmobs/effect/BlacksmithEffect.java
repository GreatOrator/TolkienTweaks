package com.greatorator.tolkienmobs.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.function.Predicate;

public class BlacksmithEffect extends TolkienEffect {
    public static BlacksmithEffect instance = null;
    private static final Predicate<ItemStack> CAN_REPAIR_ITEM = stack -> !stack.isEmpty() && isRepairableDamagedItem(stack);

    public BlacksmithEffect(MobEffectCategory category, int color) {
        super(category, color);
        instance = this;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
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
        return stack.isDamageableItem() && stack.isRepairable() && stack.getDamageValue() > 0;
    }

    private static void repairAllItems(Player player, int amplifier) {
        IItemHandler itemHandler = player.getCapability(Capabilities.ItemHandler.ENTITY);
        Predicate<ItemStack> canRepairPlayerItem = CAN_REPAIR_ITEM.and(stack -> stack != player.getMainHandItem() || !player.swinging);
        repairAllItems(itemHandler, canRepairPlayerItem, amplifier);
    }

    private static boolean repairAllItems(IItemHandler inv, Predicate<ItemStack> canRepairStack, int amplifier) {
        boolean hasAction = false;
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack invStack = inv.getStackInSlot(i);
            if (canRepairStack.test(invStack)) {
                invStack.setDamageValue(invStack.getDamageValue() - (amplifier + 1));
                if (!hasAction) {
                    hasAction = true;
                }
            }
        }
        return hasAction;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
