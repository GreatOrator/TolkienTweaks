package com.greatorator.tolkienmobs.containers.slots;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;

public class TrinketResultSlot extends ResultSlot {

    public TrinketResultSlot(Player player, Container input, Container trinketResult, Container assemblyMatrix, Container result, int slotIndex, int x, int y) {
        super(player, (CraftingContainer) assemblyMatrix, result, slotIndex, x, y);

    }

    @Override
    public void onTake(Player player, ItemStack stack) {

    }
}