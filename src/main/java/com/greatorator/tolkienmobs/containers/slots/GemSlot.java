package com.greatorator.tolkienmobs.containers.slots;

import com.greatorator.tolkienmobs.init.TolkienItems;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class GemSlot extends SlotItemHandler {
    public static final Identifier backgroundLoc = null;

    public GemSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Identifier backgroundLoc) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack itemstack) {
        if (itemstack.getItem() == TolkienItems.GEM_AMMOLITE.get()) {
            return (itemstack.getItem() == TolkienItems.GEM_AMMOLITE.get());
        }
            return false;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }
}
