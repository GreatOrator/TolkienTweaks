package com.greatorator.tolkientweaks.container;

import com.brandon3055.brandonscore.inventory.ContainerBCore;
import com.greatorator.tolkientweaks.container.slots.SlotsCheckValid;
import com.greatorator.tolkientweaks.item.CoinPouchItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.greatorator.tolkientweaks.TolkienContent.COIN_POUCH_CONTAINER;
import static com.greatorator.tolkientweaks.TolkienTweaks.LOGGER;

public class CoinPouchContainer extends ContainerBCore<CoinPouchItem> {
    private final InventoryDynamicItemStack itemStackHandlerCoinPouch;
    private final ItemStack itemStackBeingHeld;
    private static final int MAX_EXPECTED_BAG_SLOT_COUNT = 18;
    public List<Slot> playerSlots = new ArrayList<>();
    public List<Slot> playerEquipment = new ArrayList<>();
    public List<SlotsCheckValid> mainSlots = new ArrayList<>();

    public static CoinPouchContainer createContainerServerSide(int windowID, PlayerInventory playerInventory, InventoryDynamicItemStack bagContents,
                                                               ItemStack flowerBag) {
        return new CoinPouchContainer(COIN_POUCH_CONTAINER, windowID, playerInventory, bagContents, flowerBag);
    }

    public static CoinPouchContainer createContainerClientSide(int windowID, PlayerInventory playerInventory, net.minecraft.network.PacketBuffer extraData) {
        // for this example we use extraData for the server to tell the client how many slots for flower itemstacks the flower bag contains.
        int numberOfCoinSlots = extraData.readInt();

        try {
            InventoryDynamicItemStack itemStackHandlerCoinPouch = new InventoryDynamicItemStack(numberOfCoinSlots);

            // on the client side there is no parent ItemStack to communicate with - we use a dummy inventory
            return new CoinPouchContainer(COIN_POUCH_CONTAINER, windowID, playerInventory, itemStackHandlerCoinPouch, ItemStack.EMPTY);
        } catch (IllegalArgumentException iae) {
            LOGGER.warn(iae);
        }
        return null;
    }

    private CoinPouchContainer(@Nullable ContainerType<?> type, int windowId, PlayerInventory playerInv, InventoryDynamicItemStack itemStackHandlerCoinPouch, ItemStack itemStackBeingHeld) {
        super(COIN_POUCH_CONTAINER, windowId,playerInv);
        this.itemStackHandlerCoinPouch = itemStackHandlerCoinPouch;
        this.itemStackBeingHeld = itemStackBeingHeld;

        // PLayer Inventory and Hotbar
        for (int i = 0; i < playerInv.items.size(); i++) {
            playerSlots.add(addSlot(new SlotsCheckValid.IInv(playerInv, i, 0, 0)));
        }

        //Player Armor
        for (int i = 0; i < playerInv.armor.size(); i++) {
            playerEquipment.add(addSlot(new SlotsCheckValid.IInv(playerInv, i + 36, 0, 0)));
        }

        //Player Off-hand
        playerEquipment.add(addSlot(new SlotsCheckValid.IInv(playerInv, 36 + 4, 0, 0)));

        int bagSlotCount = itemStackHandlerCoinPouch.getSlots();
        if (bagSlotCount < 1 || bagSlotCount > MAX_EXPECTED_BAG_SLOT_COUNT) {
            LOGGER.warn("Unexpected invalid slot count in Coin Pouch(" + bagSlotCount + ")");
            bagSlotCount = MathHelper.clamp(bagSlotCount, 1, MAX_EXPECTED_BAG_SLOT_COUNT);
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

    @Override
    public void broadcastChanges() {
        if (itemStackHandlerCoinPouch.isDirty()) {
            CompoundNBT nbt = itemStackBeingHeld.getOrCreateTag();
            int dirtyCounter = nbt.getInt("dirtyCounter");
            nbt.putInt("dirtyCounter", dirtyCounter + 1);
            itemStackBeingHeld.setTag(nbt);
        }
        super.broadcastChanges();
    }
}
