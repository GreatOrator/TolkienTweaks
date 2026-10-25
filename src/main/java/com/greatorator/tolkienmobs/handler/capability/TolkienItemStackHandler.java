package com.greatorator.tolkienmobs.handler.capability;

import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

/**
 * A slot list on 26.1's transfer API. 26.1 makes {@link net.neoforged.neoforge.transfer.ResourceHandler}
 * the shape a capability hands out, so the storage itself is a {@code ResourceHandler<ItemResource>};
 * the menu slots still speak {@link IItemHandler}, which NeoForge keeps as a view over a resource
 * handler ({@link IItemHandler#of}), so this class offers that view from the same object rather than
 * converting back and forth. That view only reads and inserts, while a slot also assigns a stack
 * outright, so the handler answers that one call itself as an {@link IItemHandlerModifiable}.
 * <p>
 * Every slot holds the old stack limit of 64, and the {@code Items} list an older save wrote into the
 * same tag is read when the new {@code stacks} key is absent.
 */
public class TolkienItemStackHandler extends ItemStacksResourceHandler implements IItemHandlerModifiable {
    private static final int SLOT_LIMIT = 64;

    private final IItemHandler legacy = IItemHandler.of(this);

    public TolkienItemStackHandler(int slots) {
        super(slots);
    }

    public TolkienItemStackHandler(NonNullList<ItemStack> stacks) {
        super(stacks);
    }

    /** What the old {@code getStackLimit} did: 64 at most, and never more than the item stacks to. */
    @Override
    protected int getCapacity(int index, ItemResource resource) {
        return Math.min(SLOT_LIMIT, super.getCapacity(index, resource));
    }

    @Override
    public void deserialize(ValueInput input) {
        super.deserialize(input);
        if (!ResourceHandlerUtil.isEmpty(this)) {
            return;
        }
        NonNullList<ItemStack> legacyStacks = NonNullList.withSize(size(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, legacyStacks);
        for (int i = 0; i < legacyStacks.size(); i++) {
            ItemStack stack = legacyStacks.get(i);
            if (!stack.isEmpty()) {
                set(i, ItemResource.of(stack), stack.getCount());
            }
        }
    }

    @Override
    public int getSlots() {
        return legacy.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return legacy.getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return legacy.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return legacy.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return legacy.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return legacy.isItemValid(slot, stack);
    }

    /**
     * What a menu slot writes through. 26.1's {@code SlotItemHandler#set} casts its handler to
     * {@link IItemHandlerModifiable} for this one call, so a handler that only spoke
     * {@link IItemHandler} threw the first time a screen filled its slots from the server - which is
     * what closed the world the moment a backpack was opened. The write goes into the same storage
     * the slots read from, so nothing is kept twice.
     */
    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        set(slot, stack.isEmpty() ? ItemResource.EMPTY : ItemResource.of(stack), stack.getCount());
    }
}
