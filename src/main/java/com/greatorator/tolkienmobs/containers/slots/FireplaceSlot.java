package com.greatorator.tolkienmobs.containers.slots;

import com.greatorator.tolkienmobs.containers.FireplaceContainer;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class FireplaceSlot extends ResourceHandlerSlot {
    public static final Identifier backgroundLoc = null;
    private final FireplaceContainer menu;

    public FireplaceSlot(ResourceHandler<ItemResource> itemHandler, IndexModifier<ItemResource> slotModifier, int index, int xPosition, int yPosition, Identifier backgroundLoc, FireplaceContainer menu) {
        super(itemHandler, slotModifier, index, xPosition, yPosition);
        this.menu = menu;
    }
}
