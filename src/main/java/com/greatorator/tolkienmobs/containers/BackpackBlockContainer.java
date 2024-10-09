package com.greatorator.tolkienmobs.containers;

import com.greatorator.tolkienmobs.block.custom.entity.BackpackBlockEntity;
import com.greatorator.tolkienmobs.block.custom.entity.PiggyBankBlockEntity;
import com.greatorator.tolkienmobs.containers.handlers.BackpackItemStackHandler;
import com.greatorator.tolkienmobs.containers.handlers.PiggyBankItemStackHandler;
import com.greatorator.tolkienmobs.containers.slots.OutputSlot;
import com.greatorator.tolkienmobs.containers.slots.SlotArmor;
import com.greatorator.tolkienmobs.init.TolkienContainers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;

public class BackpackBlockContainer extends TolkienContainer{
    private final BackpackBlockEntity tileEntity;
    private final Level level;
    public static final int COLUMNS = 9;
    public static final int ROWS = 8;
    public BackpackBlockContainer(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public BackpackBlockContainer(int pContainerId, Inventory inv, BlockEntity blockEntity) {
        super(TolkienContainers.BACKPACK_CONTAINER.get(), pContainerId);
        this.tileEntity = ((BackpackBlockEntity) blockEntity);
        this.level = inv.player.level();

        addContainerSlots(this.tileEntity.itemHandler, COLUMNS, ROWS);

        addPlayerInventory(inv, 65, 146);
        addPlayerHotbar(inv, 65, 146);
        addPlayerArmorInventory(inv, 23, 197);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex < ROWS * 9) {
                if (!this.moveItemStackTo(itemstack1, ROWS * 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, ROWS * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    void addContainerSlots(IItemHandler itemHandler, int cols, int rows) {
        int slot_index = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = 65 + col * 18;
                int y = 13 + row * 18;

                this.addSlot(new BackpackItemStackHandler(itemHandler, slot_index, x, y));
                slot_index++;
            }
        }
    }
}
