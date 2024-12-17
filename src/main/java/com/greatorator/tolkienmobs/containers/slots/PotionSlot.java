package com.greatorator.tolkienmobs.containers.slots;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class PotionSlot extends SlotItemHandler {
    public static final ResourceLocation backgroundLoc = null;

    public PotionSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, ResourceLocation backgroundLoc) {
        super(itemHandler, index, xPosition, yPosition);
        this.setBackground(InventoryMenu.BLOCK_ATLAS, backgroundLoc);
    }

    @Override
    public boolean mayPlace(ItemStack itemstack) {
        if (itemstack.getItem() instanceof PotionItem) {
            return (itemstack.getItem() instanceof PotionItem);
        }
            return false;
    }

    @Override
    public @NotNull PotionSlot setBackground(@NotNull ResourceLocation atlas, @NotNull ResourceLocation sprite) {
        super.setBackground(atlas, sprite);
        return this;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
