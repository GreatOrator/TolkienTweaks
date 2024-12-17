package com.greatorator.tolkienmobs.containers.slots;

import com.greatorator.tolkienmobs.init.TolkienItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class GemSlot extends SlotItemHandler {
    public static final ResourceLocation backgroundLoc = null;

    public GemSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, ResourceLocation backgroundLoc) {
        super(itemHandler, index, xPosition, yPosition);
        this.setBackground(InventoryMenu.BLOCK_ATLAS, backgroundLoc);
    }

    @Override
    public @NotNull GemSlot setBackground(@NotNull ResourceLocation atlas, @NotNull ResourceLocation sprite) {
        super.setBackground(atlas, sprite);
        return this;
    }

    @Override
    public boolean mayPlace(ItemStack itemstack) {
        if (itemstack.getItem() == TolkienItems.GEM_AMMOLITE.get()) {
            return (itemstack.getItem() == TolkienItems.GEM_AMMOLITE.get());
        }
            return false;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }
}
