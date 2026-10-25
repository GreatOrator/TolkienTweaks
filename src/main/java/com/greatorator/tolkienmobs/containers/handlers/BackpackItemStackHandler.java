/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.greatorator.tolkienmobs.containers.handlers;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class BackpackItemStackHandler extends ResourceHandlerSlot {
    public BackpackItemStackHandler(ResourceHandler<ItemResource> itemHandler, IndexModifier<ItemResource> slotModifier, int index, int xPosition, int yPosition) {
        super(itemHandler, slotModifier, index, xPosition, yPosition);
    }

    public boolean mayPlace(ItemStack stack) {
        return true;
    }
}
