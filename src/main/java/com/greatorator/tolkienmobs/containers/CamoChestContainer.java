package com.greatorator.tolkienmobs.containers;

import com.greatorator.tolkienmobs.block.custom.entity.CamoChestBlockEntity;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienContainers;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * A vanilla chest's three rows of slots, laid out for {@code inventory_medium.png} rather than for vanilla's
 * chest texture. The player's inventory and hotbar are below, as on any chest.
 */
public class CamoChestContainer extends AbstractContainerMenu {
    private static final int COLUMNS = 9;
    private static final int CONTAINER_SLOTS = CamoChestBlockEntity.SLOTS;
    private static final int PLAYER_INVENTORY_SLOTS = 27;
    private static final int HOTBAR_SLOTS = 9;
    private static final int PLAYER_SLOTS = PLAYER_INVENTORY_SLOTS + HOTBAR_SLOTS;

    private final Container container;
    private final ContainerLevelAccess access;

    /** Client-side menu: the slots hold nothing of their own and are filled by the server's sync. */
    public CamoChestContainer(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CONTAINER_SLOTS), ContainerLevelAccess.NULL);
    }

    public CamoChestContainer(int containerId, Inventory playerInventory, Container container, ContainerLevelAccess access) {
        super(TolkienContainers.CAMO_CHEST_CONTAINER.get(), containerId);
        checkContainerSize(container, CONTAINER_SLOTS);
        this.container = container;
        this.access = access;
        container.startOpen(playerInventory.player);

        for (int row = 0; row < CamoChestBlockEntity.ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                this.addSlot(new Slot(container, column + row * COLUMNS, 8 + column * 18, 13 + row * 18));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                this.addSlot(new Slot(playerInventory, column + row * COLUMNS + COLUMNS, 8 + column * 18, 84 + row * 18));
            }
        }

        for (int column = 0; column < HOTBAR_SLOTS; column++) {
            this.addSlot(new Slot(playerInventory, column, 8 + column * 18, 142));
        }
    }

    public boolean isFor(CamoChestBlockEntity blockEntity) {
        return this.container == blockEntity;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index < CONTAINER_SLOTS) {
            if (!this.moveItemStackTo(stack, CONTAINER_SLOTS, CONTAINER_SLOTS + PLAYER_SLOTS, true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(stack, 0, CONTAINER_SLOTS, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, TolkienBlocks.CAMO_CHEST_BLOCK.get());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }
}
