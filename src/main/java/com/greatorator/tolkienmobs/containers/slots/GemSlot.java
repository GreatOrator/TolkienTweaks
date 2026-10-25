package com.greatorator.tolkienmobs.containers.slots;

import com.greatorator.tolkienmobs.init.TolkienItems;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class GemSlot extends ResourceHandlerSlot {
    public static final Identifier backgroundLoc = null;

    public GemSlot(ResourceHandler<ItemResource> itemHandler, IndexModifier<ItemResource> slotModifier, int index, int xPosition, int yPosition, Identifier backgroundLoc) {
        super(itemHandler, slotModifier, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack itemstack) {
        return itemstack.getItem() == TolkienItems.GEM_AMMOLITE.get();
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }
}
