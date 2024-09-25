package com.greatorator.tolkienmobs.containers.slots;

import com.greatorator.tolkienmobs.init.TolkienItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import javax.annotation.Nullable;

public class GemSlot extends SlotItemHandler implements SlotWithOverlay {
    @Nullable
    private ResourceLocation foregroundSprite;

    public GemSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack itemstack) {
        if (itemstack.getItem() == TolkienItems.GEM_AMMOLITE.get()) {
            return (itemstack.getItem() == TolkienItems.GEM_AMMOLITE.get());
        }
            return false;
    }

    @Override
    @Nullable
    public ResourceLocation getForegroundSprite() {
        return foregroundSprite;
    }

    public GemSlot setForeground(ResourceLocation sprite) {
        foregroundSprite = sprite;
        return this;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }
}
