package com.greatorator.tolkienmobs.containers.slots;

import com.greatorator.tolkienmobs.item.custom.TrinketItem;
import com.greatorator.tolkienmobs.util.GeneralUtility;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class TrinketSlot extends ResourceHandlerSlot {
    public static final Identifier backgroundLoc = null;

    public TrinketSlot(ResourceHandler<ItemResource> itemHandler, IndexModifier<ItemResource> slotModifier, int index, int xPosition, int yPosition, Identifier backgroundLoc) {
        super(itemHandler, slotModifier, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack itemstack) {
        // Trinkets to enchant, or a damaged tool for the table to repair.
        return itemstack.getItem() instanceof TrinketItem || GeneralUtility.isRepairTarget(itemstack);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
