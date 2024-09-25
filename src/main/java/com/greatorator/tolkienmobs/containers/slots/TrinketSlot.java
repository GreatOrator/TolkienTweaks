package com.greatorator.tolkienmobs.containers.slots;

import com.greatorator.tolkienmobs.item.custom.TrinketItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import javax.annotation.Nullable;

public class TrinketSlot extends SlotItemHandler implements SlotWithOverlay {
    @Nullable
    private ResourceLocation foregroundSprite;

    public TrinketSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack itemstack) {
        if (itemstack.getItem() instanceof TrinketItem) {
            return (itemstack.getItem() instanceof TrinketItem);
        }
            return false;
    }

    @Override
    @Nullable
    public ResourceLocation getForegroundSprite() {
        return foregroundSprite;
    }

    public TrinketSlot setForeground(ResourceLocation sprite) {
        foregroundSprite = sprite;
        return this;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
