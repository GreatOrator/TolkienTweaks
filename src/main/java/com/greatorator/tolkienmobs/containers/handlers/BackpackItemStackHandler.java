/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.greatorator.tolkienmobs.containers.handlers;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class BackpackItemStackHandler extends SlotItemHandler {
    private final IItemHandler itemHandler;
    protected final int index;

    public BackpackItemStackHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);

        this.itemHandler = itemHandler;
        this.index = index;
    }

    public boolean mayPlace(ItemStack stack) {
        return true;
    }
}
