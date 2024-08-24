package com.greatorator.tolkienmobs.containers.handlers;

import com.greatorator.tolkienmobs.handler.TolkienDataComponents;
import com.greatorator.tolkienmobs.item.TolkienCoinItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ComponentItemHandler;
import org.jetbrains.annotations.NotNull;

public class CoinPouchItemStackHandler extends ComponentItemHandler {
    public CoinPouchItemStackHandler(int slots, ItemStack stack) {
        super(stack, TolkienDataComponents.ITEMSTACK_HANDLER.get(), slots);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        if (stack.isEmpty()) return true;
        return (stack.getItem() instanceof TolkienCoinItem);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }
}
