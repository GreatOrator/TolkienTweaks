package com.greatorator.tolkienmobs.containers;

import com.greatorator.tolkienmobs.block.custom.entity.LockableTreasureChestBlockEntity;
import com.greatorator.tolkienmobs.containers.slots.KeyCodeSlot;
import com.greatorator.tolkienmobs.init.TolkienContainers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ComponentItemHandler;

public class LockableTreasureChestContainer extends AbstractContainerMenu {
    public final LockableTreasureChestBlockEntity tileEntity;
    private final Level level;
    public String keyCode = null;
    public Player playerEntity;
    public BlockPos sourceContainer = BlockPos.ZERO;
    public ComponentItemHandler keyCodeHandler;

    public LockableTreasureChestContainer(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public LockableTreasureChestContainer(int pContainerId, Inventory inv, BlockEntity blockEntity) {
        super(TolkienContainers.LOCKABLE_TREASURE_CHEST_CONTAINER.get(), pContainerId);
        this.level = inv.player.level();
        this.tileEntity = ((LockableTreasureChestBlockEntity) blockEntity);
        this.keyCode = tileEntity.getKeyCode();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
        super.clicked(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return true;
    }

    @Override
    protected boolean moveItemStackTo(ItemStack itemStack, int fromSlot, int toSlot, boolean pIndex) {
        boolean flag = false;
        int i = fromSlot;
        if (pIndex) {
            i = toSlot - 1;
        }

        while (!itemStack.isEmpty()) {
            if (pIndex) {
                if (i < fromSlot) {
                    break;
                }
            } else if (i >= toSlot) {
                break;
            }

            Slot slot = this.slots.get(i);
            ItemStack itemstack = slot.getItem();
            if (!itemstack.isEmpty() && ItemStack.isSameItemSameComponents(itemStack, itemstack)) {
                int j = itemstack.getCount() + itemStack.getCount();
                int maxSize = Math.min(slot.getMaxStackSize(), slot.getMaxStackSize(itemStack));
                if (j <= maxSize) {
                    itemStack.setCount(0);
                    itemstack.setCount(j);
                    slot.setByPlayer(itemstack);
                    slot.setChanged();
                    flag = true;
                } else if (itemstack.getCount() < maxSize) {
                    itemStack.shrink(maxSize - itemstack.getCount());
                    itemstack.setCount(maxSize);
                    slot.setByPlayer(itemstack);
                    slot.setChanged();
                    flag = true;
                }
            }

            if (pIndex) {
                --i;
            } else {
                ++i;
            }
        }

        if (!itemStack.isEmpty()) {
            if (pIndex) {
                i = toSlot - 1;
            } else {
                i = fromSlot;
            }

            while (true) {
                if (pIndex) {
                    if (i < fromSlot) {
                        break;
                    }
                } else if (i >= toSlot) {
                    break;
                }

                Slot slot1 = this.slots.get(i);
                ItemStack itemstack1 = slot1.getItem();
                if (itemstack1.isEmpty() && slot1.mayPlace(itemStack) && slot1.getItem().getCount() < slot1.getMaxStackSize(itemStack)) {
                    if (itemStack.getCount() > slot1.getMaxStackSize()) {
                        slot1.setByPlayer(itemStack.split(slot1.getMaxStackSize()));
                    } else {
                        slot1.setByPlayer(itemStack.split(slot1.getMaxStackSize(itemStack)));
                    }

                    slot1.setChanged();
                    flag = true;
                    break;
                }

                if (pIndex) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        return flag;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack itemStack, Slot slot) {
        return !(slot instanceof KeyCodeSlot);
    }

    @Override
    public void removed(Player playerIn) {
        super.removed(playerIn);
    }
}
