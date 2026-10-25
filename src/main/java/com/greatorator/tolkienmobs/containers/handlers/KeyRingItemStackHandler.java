package com.greatorator.tolkienmobs.containers.handlers;

import com.greatorator.tolkienmobs.handler.capability.ComponentItemStackHandler;
import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import com.greatorator.tolkienmobs.init.TolkienTags;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class KeyRingItemStackHandler extends ComponentItemStackHandler {
    private static final int SLOT_LIMIT = 64;

    public KeyRingItemStackHandler(int slots, ItemStack stack) {
        super(stack, TolkienDataComponents.ITEMSTACK_HANDLER.get(), slots);
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        if (resource.isEmpty()) return true;
        return resource.test(stack -> stack.is(TolkienTags.Items.KEYS)) && super.isValid(index, resource);
    }

    @Override
    protected int getCapacity(int index, ItemResource resource) {
        return Math.min(SLOT_LIMIT, super.getCapacity(index, resource));
    }
}
