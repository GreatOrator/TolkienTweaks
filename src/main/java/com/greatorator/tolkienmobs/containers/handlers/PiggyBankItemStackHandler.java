/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.greatorator.tolkienmobs.containers.handlers;

import com.greatorator.tolkienmobs.init.TolkienTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class PiggyBankItemStackHandler extends SlotItemHandler {
    private final IItemHandler itemHandler;
    protected final int index;

    public PiggyBankItemStackHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);

        this.itemHandler = itemHandler;
        this.index = index;
    }

    public boolean mayPlace(ItemStack stack) {
        return !stack.is(TolkienTags.Items.COINS) ? false : this.itemHandler.isItemValid(this.index, stack);
    }
}
