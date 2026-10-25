package com.greatorator.tolkienmobs.handler.capability;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ItemAccessResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.Nullable;

/**
 * A slot list held in an item's {@link ItemContainerContents} component, on 26.1's transfer API.
 * NeoForge's {@code ItemAccessItemHandler} is the same shape but only inserts and extracts, while a
 * menu slot also replaces a slot outright, so this class adds {@link #set} - the
 * {@link IndexModifier} a {@code ResourceHandlerSlot} writes through.
 * <p>
 * The write goes through {@link ItemAccess#exchange} inside a root transaction, which is what copies
 * the updated component patch back onto the pouch, ring or key the handler was opened from.
 */
public class ComponentItemStackHandler extends ItemAccessResourceHandler<ItemResource> implements IndexModifier<ItemResource> {
    private final Item validItem;
    private final DataComponentType<ItemContainerContents> component;

    public ComponentItemStackHandler(ItemStack stack, DataComponentType<ItemContainerContents> component, int slots) {
        super(ItemAccess.forStack(stack), slots);
        this.validItem = stack.getItem();
        this.component = component;
    }

    private ItemContainerContents getContents(ItemResource accessResource) {
        return accessResource.getOrDefault(component, ItemContainerContents.EMPTY);
    }

    private ItemStack getStackFromContents(ItemContainerContents contents, int slot) {
        return slot < contents.getSlots() ? contents.getStackInSlot(slot) : ItemStack.EMPTY;
    }

    @Override
    protected ItemResource getResourceFrom(ItemResource accessResource, int index) {
        if (accessResource.is(validItem)) {
            return ItemResource.of(getStackFromContents(getContents(accessResource), index));
        }
        return ItemResource.EMPTY;
    }

    @Override
    protected int getAmountFrom(ItemResource accessResource, int index) {
        if (accessResource.is(validItem)) {
            return getStackFromContents(getContents(accessResource), index).getCount();
        }
        return 0;
    }

    @Override
    @Nullable
    protected ItemResource update(ItemResource accessResource, int index, ItemResource newResource, int newAmount) {
        ItemContainerContents contents = getContents(accessResource);
        NonNullList<ItemStack> list = NonNullList.withSize(Math.max(contents.getSlots(), size()), ItemStack.EMPTY);
        contents.copyInto(list);
        list.set(index, newResource.toStack(newAmount));
        return accessResource.with(this.component, ItemContainerContents.fromItems(list));
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        // Any resource is valid, but the item the handler was opened from must still be the same item.
        return itemAccess.getResource().is(validItem);
    }

    @Override
    protected int getCapacity(int index, ItemResource resource) {
        return resource.isEmpty() ? Item.ABSOLUTE_MAX_STACK_SIZE : Math.min(resource.getMaxStackSize(), Item.ABSOLUTE_MAX_STACK_SIZE);
    }

    /** What a menu slot writes through: the whole slot replaced in one exchange. */
    @Override
    public void set(int index, ItemResource resource, int amount) {
        TransferPreconditions.checkNonNegative(amount);
        if (!resource.isEmpty() && amount == 0) {
            // Writing empties the slot, so the resource no longer matters.
            resource = ItemResource.EMPTY;
        }
        ItemResource accessResource = itemAccess.getResource();
        int accessAmount = itemAccess.getAmount();
        if (accessAmount == 0) {
            return;
        }
        ItemResource replacement = update(accessResource, index, resource.isEmpty() ? accessResource : resource, amount);
        if (replacement == null || replacement.isEmpty() || replacement.equals(accessResource)) {
            return;
        }
        itemAccess.exchange(replacement, accessAmount, null);
    }
}
