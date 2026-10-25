package com.greatorator.tolkienmobs.containers.handlers;

import com.greatorator.tolkienmobs.init.TolkienTags;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class UpgradeItemHandler extends ResourceHandlerSlot {
    protected boolean enabled = true;

    public UpgradeItemHandler(ResourceHandler<ItemResource> itemHandler, IndexModifier<ItemResource> slotModifier, int slotIndex, int xPosition, int yPosition) {
        super(itemHandler, slotModifier, slotIndex, xPosition, yPosition);
    }

    public boolean mayPlace(ItemStack stack) {
        return stack.is(TolkienTags.Items.UPGRADES)
                && getResourceHandler().isValid(getSlotIndex(), ItemResource.of(stack));
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
