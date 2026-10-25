package com.greatorator.tolkienmobs.containers.handlers;

import com.greatorator.tolkienmobs.handler.capability.ComponentItemStackHandler;
import com.greatorator.tolkienmobs.handler.capability.TolkienItemStackHandler;
import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.init.TolkienTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;

public class CoinPouchItemStackHandler extends ComponentItemStackHandler {
    private static final int SLOT_LIMIT = 64;

    public CoinPouchItemStackHandler(int slots, ItemStack stack) {
        super(stack, TolkienDataComponents.ITEMSTACK_HANDLER.get(), slots);
    }

    /** Convert whole groups of 64, carrying bronze through silver and gold to mithril. */
    public boolean convertCoins() {
        Item[] tiers = {TolkienItems.ITEM_COIN_BRONZE.get(), TolkienItems.ITEM_COIN_SILVER.get(),
                TolkienItems.ITEM_COIN_GOLD.get(), TolkienItems.ITEM_COIN_MITHRIL.get()};
        // Exchange in a scratch handler first, so a pouch that cannot hold the result is left alone.
        TolkienItemStackHandler working = new TolkienItemStackHandler(size());
        for (int slot = 0; slot < size(); slot++) {
            ItemStack stack = ItemUtil.getStack(this, slot);
            if (!stack.isEmpty()) {
                working.set(slot, ItemResource.of(stack), stack.getCount());
            }
        }
        boolean changed = false;
        for (int tier = 0; tier < tiers.length - 1; tier++) {
            int count = 0;
            for (int slot = 0; slot < size(); slot++) {
                ItemStack stack = ItemUtil.getStack(working, slot);
                if (stack.is(tiers[tier])) count += stack.getCount();
            }
            int upgrades = count / 64;
            if (upgrades == 0) continue;
            int remaining = upgrades * 64;
            for (int slot = 0; slot < size() && remaining > 0; slot++) {
                ItemStack stack = ItemUtil.getStack(working, slot);
                if (!stack.is(tiers[tier])) continue;
                int removed = Math.min(remaining, stack.getCount());
                working.set(slot, ItemResource.of(stack), stack.getCount() - removed);
                remaining -= removed;
            }
            ItemStack overflow = ItemUtil.insertItemReturnRemaining(working, new ItemStack(tiers[tier + 1], upgrades), false, null);
            // Publish only a complete exchange, including for legacy overfilled stacks.
            if (!overflow.isEmpty()) return false;
            changed = true;
        }
        if (changed) {
            for (int slot = 0; slot < size(); slot++) {
                ItemStack stack = ItemUtil.getStack(working, slot);
                if (ItemStack.matches(stack, ItemUtil.getStack(this, slot))) continue;
                set(slot, stack.isEmpty() ? ItemResource.EMPTY : ItemResource.of(stack), stack.getCount());
            }
        }
        return changed;
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        if (resource.isEmpty()) return true;
        return resource.test(stack -> stack.is(TolkienTags.Items.COINS)) && super.isValid(index, resource);
    }

    @Override
    protected int getCapacity(int index, ItemResource resource) {
        return Math.min(SLOT_LIMIT, super.getCapacity(index, resource));
    }
}
