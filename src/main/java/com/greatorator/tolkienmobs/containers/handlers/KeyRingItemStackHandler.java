package com.greatorator.tolkienmobs.containers.handlers;

import com.greatorator.tolkienmobs.handler.TolkienDataComponents;
import com.greatorator.tolkienmobs.item.TolkienCoinItem;
import com.greatorator.tolkienmobs.item.TolkienKeyItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ComponentItemHandler;
import org.jetbrains.annotations.NotNull;

public class KeyRingItemStackHandler extends ComponentItemHandler {
    public KeyRingItemStackHandler(int slots, ItemStack stack) {
        super(stack, TolkienDataComponents.ITEMSTACK_HANDLER.get(), slots);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        if (stack.isEmpty()) return true;
        return (stack.getItem() instanceof TolkienKeyItem);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }
}
