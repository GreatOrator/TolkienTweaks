package com.greatorator.tolkienmobs.containers;

import com.greatorator.tolkienmobs.containers.slots.SlotArmor;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import javax.annotation.Nullable;
import javax.swing.plaf.basic.BasicComboBoxUI;

public abstract class TolkienContainer extends AbstractContainerMenu {
    public ItemStackHandler handler;

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

    protected void addPlayerArmorInventory(Inventory inventory, int offsetX, int offsetY) {
        this.addSlot(new SlotArmor(inventory, (4 * 9), offsetX + 18, offsetY, inventory.player, EquipmentSlot.CHEST));
        this.addSlot(new SlotArmor(inventory, (4 * 9) + 1, offsetX, offsetY, inventory.player, EquipmentSlot.HEAD));
        this.addSlot(new SlotArmor(inventory, (4 * 9) + 2, offsetX, offsetY + 18, inventory.player, EquipmentSlot.LEGS));
        this.addSlot(new SlotArmor(inventory, (4 * 9) + 3, offsetX + 18, offsetY + 18, inventory.player, EquipmentSlot.FEET));

        this.addSlot(new Slot(inventory, (4 * 9) + 4, offsetX + 9, offsetY + 36) {
            public void setByPlayer(ItemStack stack, ItemStack type) {
                inventory.player.onEquipItem(EquipmentSlot.OFFHAND, type, stack);
                super.setByPlayer(stack, type);
            }

            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
            }
        });
    }
}
