package com.greatorator.tolkientweaks.container;

import com.brandon3055.brandonscore.inventory.ContainerBCore;
import com.greatorator.tolkientweaks.container.slots.SlotsCheckValid;
import com.greatorator.tolkientweaks.item.CoinPouchItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.brandon3055.brandonscore.inventory.ContainerBCTile.getClientTile;
import static com.greatorator.tolkientweaks.TolkienContent.COIN_POUCH_CONTAINER;

public class CoinPouchContainer extends ContainerBCore<CoinPouchItem> {
    private final InventoryDynamicItemStack itemStackHandlerCoinPouch;
    private final ItemStack itemStackBeingHeld;
    private static final int MAX_EXPECTED_BAG_SLOT_COUNT = 18;
    public List<Slot> playerSlots = new ArrayList<>();
    public List<Slot> playerEquipment = new ArrayList<>();
    public List<SlotsCheckValid> mainSlots = new ArrayList<>();

    public BackpackContainer(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
        this(COIN_POUCH_CONTAINER, windowId, playerInv, getClientTile(extraData));
        //^^ Don't forget this!
    }

    public CoinPouchContainer(@Nullable ContainerType<?> type, int windowId, PlayerInventory playerInv, InventoryDynamicItemStack itemStackHandlerCoinPouch, ItemStack itemStackBeingHeld) {
        super(type, windowId,playerInv);
        this.itemStackHandlerCoinPouch = itemStackHandlerCoinPouch;
        this.itemStackBeingHeld = itemStackBeingHeld;

        // PLayer Inventory and Hotbar
        for (int i = 0; i < playerInv.items.size(); i++) {
            playerSlots.add(addSlot(new SlotsCheckValid.IInv(playerInv, i, 0, 0)));
        }

        //Pouch Inventory
        for (int bagSlot = 0; bagSlot < 18; bagSlot++) {
            mainSlots.add((SlotsCheckValid) addSlot(new SlotsCheckValid(itemStackHandlerCoinPouch, bagSlot, 0, 0)));
        }
    }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity player) {
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();

        return (!main.isEmpty() && main == itemStackBeingHeld) ||
                (!off.isEmpty() && off == itemStackBeingHeld);
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int sourceSlotIndex) {
        Slot sourceSlot = getSlot(sourceSlotIndex);
        int playerSlots = 36 + 5;

        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (sourceSlotIndex >= playerSlots) {
            if (!moveItemStackTo(sourceStack, 0, playerSlots, false)) {
                return ItemStack.EMPTY; //Return if failed to merge
            }
        } else {
            //Transferring from player to tile
            if (!moveItemStackTo(sourceStack, playerSlots, playerSlots + MAX_EXPECTED_BAG_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  //Return if failed to merge
            }
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        sourceSlot.onTake(player, sourceStack);
        return copyOfSourceStack;
    }
}
