package com.greatorator.tolkienmobs.containers.handlers;

import com.greatorator.tolkienmobs.item.TolkienCoinItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class CoinPouchSlot extends SlotItemHandler {
    protected boolean enabled = true;

    public CoinPouchSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return (stack.getItem() instanceof TolkienCoinItem);
    }

    @Override
    public int getMaxStackSize() {
        return 1024;
        //return super.getMaxStackSize();
    }

    @Override
    public int getMaxStackSize(@Nonnull ItemStack stack) {
        return 1024;
        //return super.getMaxStackSize(stack);
    }

    @Override
    public boolean isActive() {
        return enabled;
    }

    public CoinPouchSlot setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
