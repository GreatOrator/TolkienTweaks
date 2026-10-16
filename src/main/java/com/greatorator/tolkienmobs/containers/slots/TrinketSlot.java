package com.greatorator.tolkienmobs.containers.slots;

import com.greatorator.tolkienmobs.item.custom.TrinketItem;
import com.greatorator.tolkienmobs.util.GeneralUtility;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class TrinketSlot extends SlotItemHandler {
    public static final ResourceLocation backgroundLoc = null;

    public TrinketSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, ResourceLocation backgroundLoc) {
        super(itemHandler, index, xPosition, yPosition);
        this.setBackground(InventoryMenu.BLOCK_ATLAS, backgroundLoc);
    }

    @Override
    public @NotNull TrinketSlot setBackground(@NotNull ResourceLocation atlas, @NotNull ResourceLocation sprite) {
        super.setBackground(atlas, sprite);
        return this;
    }

    @Override
    public boolean mayPlace(ItemStack itemstack) {
        // Trinkets to enchant, or a damaged tool for the table to repair.
        return itemstack.getItem() instanceof TrinketItem || GeneralUtility.isRepairTarget(itemstack);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
