package com.greatorator.tolkienmobs.containers.handlers;

import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import com.greatorator.tolkienmobs.init.TolkienTags;
import com.greatorator.tolkienmobs.init.TolkienItems;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class CoinPouchItemStackHandler extends ComponentItemHandler {
    public CoinPouchItemStackHandler(int slots, ItemStack stack) {
        super(stack, TolkienDataComponents.ITEMSTACK_HANDLER.get(), slots);
    }

    /** Convert whole groups of 64, carrying bronze through silver and gold to mithril. */
    public boolean convertCoins() {
        Item[] tiers = {TolkienItems.ITEM_COIN_BRONZE.get(), TolkienItems.ITEM_COIN_SILVER.get(),
                TolkienItems.ITEM_COIN_GOLD.get(), TolkienItems.ITEM_COIN_MITHRIL.get()};
        ItemContainerContents contents = getContents();
        var converted = new ItemStackHandler(getSlots());
        for (int slot = 0; slot < getSlots(); slot++) {
            converted.setStackInSlot(slot, getStackFromContents(contents, slot).copy());
        }
        boolean changed = false;
        for (int tier = 0; tier < tiers.length - 1; tier++) {
            int count = 0;
            for (int slot = 0; slot < getSlots(); slot++) {
                ItemStack stack = converted.getStackInSlot(slot);
                if (stack.is(tiers[tier])) count += stack.getCount();
            }
            int upgrades = count / 64;
            if (upgrades == 0) continue;
            int remaining = upgrades * 64;
            for (int slot = 0; slot < getSlots() && remaining > 0; slot++) {
                ItemStack stack = converted.getStackInSlot(slot);
                if (!stack.is(tiers[tier])) continue;
                int removed = Math.min(remaining, stack.getCount());
                converted.setStackInSlot(slot, stack.copyWithCount(stack.getCount() - removed));
                remaining -= removed;
            }
            ItemStack overflow = ItemHandlerHelper.insertItemStacked(converted, new ItemStack(tiers[tier + 1], upgrades), false);
            // Publish only a complete exchange, including for legacy overfilled stacks.
            if (!overflow.isEmpty()) return false;
            changed = true;
        }
        if (changed) {
            var items = NonNullList.withSize(Math.max(contents.getSlots(), getSlots()), ItemStack.EMPTY);
            contents.copyInto(items);
            for (int slot = 0; slot < getSlots(); slot++) items.set(slot, converted.getStackInSlot(slot));
            parent.set(component, ItemContainerContents.fromItems(items));
        }
        return changed;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        if (stack.isEmpty()) return true;
        return stack.is(TolkienTags.Items.COINS);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }
}
