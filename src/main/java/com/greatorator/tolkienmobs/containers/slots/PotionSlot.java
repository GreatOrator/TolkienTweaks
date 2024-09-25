package com.greatorator.tolkienmobs.containers.slots;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import javax.annotation.Nullable;

public class PotionSlot extends SlotItemHandler implements SlotWithOverlay {
    @Nullable
    private ResourceLocation foregroundSprite;

    public PotionSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack itemstack) {
        if (itemstack.getItem() instanceof PotionItem) {
            return (itemstack.getItem() instanceof PotionItem);
        }
            return false;
    }

    @Override
    @Nullable
    public ResourceLocation getForegroundSprite() {
        return foregroundSprite;
    }

    public PotionSlot setForeground(ResourceLocation sprite) {
        foregroundSprite = sprite;
        return this;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
