package com.greatorator.tolkienmobs.containers.slots;

import com.greatorator.tolkienmobs.containers.FireplaceContainer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class FireplaceSlot extends SlotItemHandler {
    public static final ResourceLocation backgroundLoc = null;
    private final FireplaceContainer menu;

    public FireplaceSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, ResourceLocation backgroundLoc, FireplaceContainer menu) {
        super(itemHandler, index, xPosition, yPosition);
        this.menu = menu;
    }
}
