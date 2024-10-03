package com.greatorator.tolkienmobs.containers.handlers;

import com.greatorator.tolkienmobs.handler.data.TolkienDataComponents;
import com.greatorator.tolkienmobs.init.TolkienTags;
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
        return stack.is(TolkienTags.Items.KEYS);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }
}
