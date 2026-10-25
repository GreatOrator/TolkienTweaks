package com.greatorator.tolkienmobs.handler.capability;

import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

/**
 * A slot list on 26.1's transfer API. 26.1 makes
 * {@link net.neoforged.neoforge.transfer.ResourceHandler} the shape a capability hands out and a
 * menu slot reads, so the storage is a {@code ResourceHandler<ItemResource>} and nothing else: the
 * old {@code IItemHandler} view and the adapter that answered {@code SlotItemHandler#set} are gone,
 * because {@link net.neoforged.neoforge.transfer.item.ResourceHandlerSlot} writes through
 * {@link #set} on this class instead.
 * <p>
 * Every slot holds the old stack limit of 64, and the {@code Items} list an older save wrote into the
 * same tag is read when the new {@code stacks} key is absent.
 */
public class TolkienItemStackHandler extends ItemStacksResourceHandler {
    private static final int SLOT_LIMIT = 64;

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
}
