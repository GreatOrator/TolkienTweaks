package com.greatorator.tolkienmobs.containers.handlers;

import com.greatorator.tolkienmobs.init.TolkienTags;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class BucketItemHandler extends SlotItemHandler {
    private final IItemHandler itemHandler;
    protected final int index;
    protected boolean enabled = true;

    public BucketItemHandler(IItemHandler itemHandler, int slotIndex, int xPosition, int yPosition) {
        super(itemHandler, slotIndex, xPosition, yPosition);

        this.itemHandler = itemHandler;
        this.index = slotIndex;
    }

    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof BucketItem && this.itemHandler.isItemValid(this.index, stack);
    }

    @Override
    public boolean isActive() {
        return enabled;
    }

    public BucketItemHandler setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
