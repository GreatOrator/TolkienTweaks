package com.greatorator.tolkientweaks.container.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.function.Supplier;

public class SlotsCheckValid extends SlotItemHandler {
    public Supplier<Boolean> isActive = null;

    public SlotsCheckValid(IItemHandler itemHandler, int id, int x, int y) {
        super(itemHandler, id, x, y);
    }
    @Override
    public boolean mayPlace(ItemStack stack) {
        return getItemHandler().isItemValid(getSlotIndex(), stack);
    }

    public void setIsActive(Supplier<Boolean> isActive) {
        this.isActive = isActive;
    }

    @Override
    public boolean isActive() {
        return isActive == null || isActive.get();
    }

    public static class IInv extends Slot {
        public Supplier<Boolean> isActive = null;

        public IInv(IInventory itemHandler, int id, int x, int y) {
            super(itemHandler, id, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return container.canPlaceItem(getSlotIndex(), stack);
        }

        public void setIsActive(Supplier<Boolean> isActive) {
            this.isActive = isActive;
        }

        @Override
        public boolean isActive() {
            return isActive == null || isActive.get();
        }
    }
}