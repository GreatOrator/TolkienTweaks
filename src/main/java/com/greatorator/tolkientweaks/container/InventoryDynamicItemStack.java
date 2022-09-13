package com.greatorator.tolkientweaks.container;

import com.greatorator.tolkientweaks.util.TTTags;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class InventoryDynamicItemStack extends ItemStackHandler implements IItemHandler {
    public static final int MIN_COIN_SLOTS = 1;
    public static final int MAX_COIN_SLOTS = 16;
    private boolean isDirty = true;

    public InventoryDynamicItemStack(int numberOfSlots) {
        super(MathHelper.clamp(numberOfSlots, MIN_COIN_SLOTS, MAX_COIN_SLOTS));
        if (numberOfSlots < MIN_COIN_SLOTS || numberOfSlots > MAX_COIN_SLOTS) {
            throw new IllegalArgumentException("Invalid number of Coin slots:" + numberOfSlots);
        }
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        if (slot < 0 || slot >= MAX_COIN_SLOTS) {
            throw new IllegalArgumentException("Invalid slot number:" + slot);
        }
        if (stack.isEmpty()) return false;
        Item item = stack.getItem();
        return item.getItem().is(TTTags.items.COINS);
    }

    public int getNumberOfEmptySlots() {
        final int NUMBER_OF_SLOTS = getSlots();

        int emptySlotCount = 0;
        for (int i = 0; i < NUMBER_OF_SLOTS; ++i) {
            if (getStackInSlot(i) == ItemStack.EMPTY) {
                ++emptySlotCount;
            }
        }
        return emptySlotCount;
    }

    public boolean isDirty() {
        boolean currentState = isDirty;
        isDirty = false;
        return currentState;
    }

    protected void onContentsChanged(int slot) {
        isDirty = true;
    }
}