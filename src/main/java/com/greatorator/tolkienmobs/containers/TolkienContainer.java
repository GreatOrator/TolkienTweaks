package com.greatorator.tolkienmobs.containers;

import com.greatorator.tolkienmobs.containers.slots.SlotArmor;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import javax.annotation.Nullable;

public abstract class TolkienContainer extends AbstractContainerMenu {
    public ItemStackHandler handler;
    private static final EquipmentSlot[] EQUIPMENT_SLOTS = new EquipmentSlot[] {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    protected static final int ITEMBOX = 18;

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
}
