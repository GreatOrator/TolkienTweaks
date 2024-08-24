package com.greatorator.tolkienmobs.containers.handlers;

import com.greatorator.tolkienmobs.item.TolkienKeyItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class KeyRingSlot extends SlotItemHandler {
    protected boolean enabled = true;

    public KeyRingSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return (stack.getItem() instanceof TolkienKeyItem);
    }

    @Override
    public int getMaxStackSize() {
        return 64;
        //return super.getMaxStackSize();
    }

    @Override
    public int getMaxStackSize(@Nonnull ItemStack stack) {
        return 64;
        //return super.getMaxStackSize(stack);
    }

    @Override
    public boolean isActive() {
        return enabled;
    }

    public KeyRingSlot setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
