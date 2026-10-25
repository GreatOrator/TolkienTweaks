package com.greatorator.tolkienmobs.containers.slots;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

import javax.annotation.Nonnull;

public class KeyItemSlot extends ResourceHandlerSlot {
    protected boolean enabled = true;

    public KeyItemSlot(ResourceHandler<ItemResource> itemHandler, IndexModifier<ItemResource> slotModifier, int index, int xPosition, int yPosition) {
        super(itemHandler, slotModifier, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
        //return super.getMaxStackSize();
    }

    @Override
    public int getMaxStackSize(@Nonnull ItemStack stack) {
        return 64;
        //return super.getMaxStackSize(stack);
    }

    @Override
    public boolean isActive() {
        return enabled;
    }

    public KeyItemSlot setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
