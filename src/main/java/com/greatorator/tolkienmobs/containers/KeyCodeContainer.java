package com.greatorator.tolkienmobs.containers;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.containers.slots.KeyCodeSlot;
import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import com.greatorator.tolkienmobs.init.TolkienContainers;
import com.greatorator.tolkienmobs.item.custom.KeyItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.registries.DeferredHolder;

public class KeyCodeContainer extends AbstractContainerMenu {
    public static ItemStack keyStack;
    private final String keyCode;
    public Player playerEntity;
    private final IItemHandler playerInventory;
    public BlockPos sourceContainer = BlockPos.ZERO;
    public ComponentItemHandler keyCodeHandler;

    public KeyCodeContainer(int windowId, Inventory playerInventory, Player player, RegistryFriendlyByteBuf extraData) {
        this(windowId, playerInventory, player, ItemStack.OPTIONAL_STREAM_CODEC.decode(extraData));
    }

    public KeyCodeContainer(int windowId, Inventory playerInventory, Player player, ItemStack keyStack) {
        super(TolkienContainers.KEY_CODE_CONTAINER.get(), windowId);
        playerEntity = player;
        this.keyCodeHandler = new ComponentItemHandler(keyStack, TolkienDataComponents.ITEMSTACK_HANDLER.get(), 1);
        this.playerInventory = new InvWrapper(playerInventory);
        KeyCodeContainer.keyStack = keyStack;
        this.keyCode = KeyItem.getKeyCode(this.keyStack);
//        if (keyCodeHandler != null) {
//            addSlotBox(keyCodeHandler, 0, 44, 17, 5, 18, 3, 18);
//        }
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
//        return playerIn.getMainHandItem().getComponents().has(TolkienDataComponents.KEY_CODE.get()) || playerIn.getOffhandItem().getComponents().has(TolkienDataComponents.KEY_CODE.get());
    }

    public static ItemStack setCode(DeferredHolder<DataComponentType<?>, DataComponentType<String>> keyCode, String code) {
        TolkienMobsMain.LOGGER.warn(keyCode + code);
        keyStack.set(keyCode, code);

        return keyStack;
    }

    @Override
    protected boolean moveItemStackTo(ItemStack itemStack, int fromSlot, int toSlot, boolean p_38907_) {
        boolean flag = false;
        int i = fromSlot;
        if (p_38907_) {
            i = toSlot - 1;
        }

        while (!itemStack.isEmpty()) {
            if (p_38907_) {
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

            if (p_38907_) {
                --i;
            } else {
                ++i;
            }
        }

        if (!itemStack.isEmpty()) {
            if (p_38907_) {
                i = toSlot - 1;
            } else {
                i = fromSlot;
            }

            while (true) {
                if (p_38907_) {
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

                if (p_38907_) {
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

//    @Override
//    public ItemStack quickMoveStack(Player playerIn, int index) {
//        ItemStack itemstack = ItemStack.EMPTY;
//        Slot slot = this.slots.get(index);
//        if (slot.hasItem()) {
//            ItemStack stack = slot.getItem();
//            //If its one of the 15 slots at the top try to move it into your inventory
//            if (index < SLOTS) {
//                if (playerIn.getInventory().getFreeSlot() != -1) {
//                    this.moveItemStackTo(stack, SLOTS, 36 + SLOTS, true);
//                    slot.setByPlayer(stack);
//                } else {
//                    return ItemStack.EMPTY;
//                }
//                slot.onQuickCraft(stack, itemstack);
//            } else {
//                if (!this.moveItemStackTo(stack, 0, SLOTS, false)) {
//                    return ItemStack.EMPTY;
//                }
//            }
//
//            slot.onTake(playerIn, stack);
//            if (stack.getCount() < itemstack.getCount()) {
//                return ItemStack.EMPTY;
//            }
//        }
//
//        return itemstack;
//    }
//
//    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
//        for (int i = 0; i < amount; i++) {
//            if ((handler.getSlots() == SLOTS))
//                addSlot(new KeyCodeSlot(handler, index, x, y));
//            else
//                addSlot(new SlotItemHandler(handler, index, x, y));
//            x += dx;
//            index++;
//        }
//        return index;
//    }
//
//    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
//        for (int j = 0; j < verAmount; j++) {
//            index = addSlotRange(handler, index, x, y, horAmount, dx);
//            y += dy;
//        }
//        return index;
//    }
//
//    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
//        // Player inventory
//        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);
//
//        // Hotbar
//        topRow += 58;
//        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
//    }

    @Override
    public void removed(Player playerIn) {
        super.removed(playerIn);
    }
}
