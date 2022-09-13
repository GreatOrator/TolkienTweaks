package com.greatorator.tolkientweaks.container.capability;

import com.greatorator.tolkientweaks.container.InventoryDynamicItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityProviderCoinPouch implements ICapabilitySerializable<INBT> {
    private final Direction NO_SPECIFIC_SIDE = null;
    private static final int MAX_NUMBER_OF_COINS_IN_BAG = 16;
    private InventoryDynamicItemStack itemStackHandlerCoinPouch;
    private final LazyOptional<IItemHandler> lazyInitializationSupplier = LazyOptional.of(this::getCachedInventory);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY == capability) return (LazyOptional<T>)(lazyInitializationSupplier);
        return LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(getCachedInventory(), NO_SPECIFIC_SIDE);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(getCachedInventory(), NO_SPECIFIC_SIDE, nbt);
    }

    private InventoryDynamicItemStack getCachedInventory() {
        if (itemStackHandlerCoinPouch == null) {
            itemStackHandlerCoinPouch = new InventoryDynamicItemStack(MAX_NUMBER_OF_COINS_IN_BAG);
        }
        return itemStackHandlerCoinPouch;
    }
}
