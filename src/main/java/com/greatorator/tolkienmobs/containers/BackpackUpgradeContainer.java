package com.greatorator.tolkienmobs.containers;

import com.greatorator.tolkienmobs.block.custom.entity.BackpackBlockEntity;
import com.greatorator.tolkienmobs.containers.handlers.BackpackItemStackHandler;
import com.greatorator.tolkienmobs.containers.handlers.UpgradeItemHandler;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienContainers;
import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.items.IItemHandler;

public class BackpackUpgradeContainer extends TolkienContainer{
    public final BackpackBlockEntity tileEntity;
    private final Player player;
    private final Level level;
    public ItemStack upgradeHolder;
    public static final int COLUMNS = 3;
    public static final int ROWS = 3;
    public static final int SLOTS = 9;
    public ComponentItemHandler upgradeHandler;

    public BackpackUpgradeContainer(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public BackpackUpgradeContainer(int pContainerId, Inventory inv, BlockEntity blockEntity) {
        super(TolkienContainers.BACKPACK_UPGRADE_CONTAINER.get(), pContainerId);
        this.tileEntity = ((BackpackBlockEntity) blockEntity);
        this.level = inv.player.level();
        this.player = inv.player;

        addContainerSlots(this.tileEntity.itemHandler, COLUMNS, ROWS);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            if (index < SLOTS) {
                if (playerIn.getInventory().getFreeSlot() != -1) {
                    this.moveItemStackTo(stack, SLOTS, 36 + SLOTS, true);
                    slot.setByPlayer(stack);
                } else {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, itemstack);
            } else {
                if (!this.moveItemStackTo(stack, 0, SLOTS, false)) {
                    return ItemStack.EMPTY;
                }
            }

            slot.onTake(playerIn, stack);
            if (stack.getCount() < itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
        }

        return itemstack;
    }

    @Override
    void addContainerSlots(IItemHandler itemHandler, int cols, int rows) {
        int slot_index = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = (-6) + col * 18;
                int y = 17 + row * 18;

                this.addSlot(new UpgradeItemHandler(itemHandler, slot_index, x, y));
                slot_index++;
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, tileEntity.getBlockPos()),
                player, TolkienBlocks.BACKPACK.get());
    }
}
