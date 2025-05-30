package com.greatorator.tolkienmobs.containers;

import com.greatorator.tolkienmobs.block.custom.entity.TolkienBlockEntity;
import com.greatorator.tolkienmobs.containers.slots.SlotArmor;
import com.greatorator.tolkienmobs.network.PacketHandler;
import com.greatorator.tolkienmobs.network.packet.PacketCustom;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import javax.annotation.Nullable;

public abstract class TolkienContainer<T extends TolkienBlockEntity> extends AbstractContainerMenu {
    public ItemStackHandler handler;
    private static final EquipmentSlot[] EQUIPMENT_SLOTS = new EquipmentSlot[] {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    protected static final int ITEMBOX = 18;
    public T tile;

    public TolkienContainer(@Nullable MenuType<?> p_i50105_1_, int p_i50105_2_) {
        super(p_i50105_1_, p_i50105_2_);
    }

    void addPlayerInventory(Inventory playerInventory, int inX, int inY) {
        for (int row = 1; row < 4; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = inX + col * 18;
                int y = row * 18 + (inY + 10);
                addSlot(new Slot(playerInventory, col + row * 9, x, y));
            }
        }
    }

    void addPlayerHotbar(Inventory playerInventory, int inX, int inY) {
        for (int row = 0; row < 9; ++row) {
            int x = inX + row * 18;
            int y = inY + 86;
            addSlot(new Slot(playerInventory, row, x, y));
        }
    }

    void addContainerSlots(IItemHandler itemHandler, int cols, int rows) {
        int slot_index = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = -15 + col * 18;
                int y = 13 + row * 18;

                this.addSlot(new SlotItemHandler(itemHandler, slot_index, x, y));
                slot_index++;
            }
        }
    }

    void addUpgradeSlots(IItemHandler itemHandler, int cols) {
        int slot_index = 0;

        for (int col = 0; col < cols; col++) {
            this.addSlot(new SlotItemHandler(itemHandler, slot_index, 0, 0));
            slot_index++;
        }
    }

    protected void addPlayerArmorInventory(Inventory inventory) {
        int slot_index = 0;

        for (int k = 0; k < 2; ++k) {
            for (int l = 0; l < 2; l++) {
                int x = 23 + (k * ITEMBOX);
                int y = 197 + (l * ITEMBOX);
                EquipmentSlot equipmentSlot = EQUIPMENT_SLOTS[slot_index];

                this.addSlot(new SlotArmor(inventory, 4 * 9 + (3 - slot_index), x, y, inventory.player, equipmentSlot));
                slot_index++;
            }
        }

        this.addSlot(new Slot(inventory, (4 * 9) + 4, 23 + 9, 197 + 36) {
            public void setByPlayer(ItemStack stack, ItemStack type) {
                inventory.player.onEquipItem(EquipmentSlot.OFFHAND, type, stack);
                super.setByPlayer(stack, type);
            }

            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
            }
        });
    }

    protected int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    private static final int TE_INVENTORY_SLOT_COUNT = 2;  // must be the number of slots you have!
    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    public PacketCustom createServerBoundPacket(int packetId) {
        PacketCustom packet = new PacketCustom(PacketHandler.NET_CHANNEL, PacketHandler.S_CONTAINER_MESSAGE, tile.getLevel().registryAccess());
        packet.writeInt(containerId);
        packet.writeByte(packetId);
        return packet;
    }

    public void handleContainerMessage(PacketCustom packet, ServerPlayer player) {
        int containerId = packet.readInt();
        if (containerId != this.containerId) return;
        int packetID = packet.readByte();
        tile.receivePacketFromClient(packet, player, packetID);
    }
}
