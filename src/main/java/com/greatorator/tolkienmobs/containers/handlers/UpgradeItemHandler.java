package com.greatorator.tolkienmobs.containers.handlers;

import com.greatorator.tolkienmobs.init.TolkienTags;
import com.greatorator.tolkienmobs.item.custom.UpgradeItem;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class UpgradeItemHandler extends SlotItemHandler {
    private final IItemHandler itemHandler;
    protected final int index;
    protected boolean enabled = true;

    public UpgradeItemHandler(IItemHandler itemHandler, int slotIndex, int xPosition, int yPosition) {
        super(itemHandler, slotIndex, xPosition, yPosition);

        this.itemHandler = itemHandler;
        this.index = slotIndex;
    }

    public boolean mayPlace(ItemStack stack) {
        return stack.is(TolkienTags.Items.UPGRADES) && this.itemHandler.isItemValid(this.index, stack);
    }

    @Override
    public boolean isActive() {
        return enabled;
    }

    public UpgradeItemHandler setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
