package com.greatorator.tolkienmobs.containers.slots;

import com.greatorator.tolkienmobs.containers.FireplaceContainer;
import com.greatorator.tolkienmobs.init.TolkienItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class FuelSlot extends SlotItemHandler {
    public static final ResourceLocation backgroundLoc = null;
    private final FireplaceContainer menu;

    public FuelSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, ResourceLocation backgroundLoc, FireplaceContainer menu) {
        super(itemHandler, index, xPosition, yPosition);
        this.menu = menu;
        this.setBackground(InventoryMenu.BLOCK_ATLAS, backgroundLoc);
    }

    @Override
    public @NotNull FuelSlot setBackground(@NotNull ResourceLocation atlas, @NotNull ResourceLocation sprite) {
        super.setBackground(atlas, sprite);
        return this;
    }
}
