package com.greatorator.tolkienmobs.containers.slots;

import com.greatorator.tolkienmobs.block.custom.entity.TrinketTableBlockEntity;
import com.greatorator.tolkienmobs.util.GeneralUtility;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class PotionSlot extends SlotItemHandler {
    public static final ResourceLocation backgroundLoc = null;
    private final Level level;

    public PotionSlot(Level level, IItemHandler itemHandler, int index, int xPosition, int yPosition, ResourceLocation backgroundLoc) {
        super(itemHandler, index, xPosition, yPosition);
        this.level = level;
        this.setBackground(InventoryMenu.BLOCK_ATLAS, backgroundLoc);
    }

    @Override
    public boolean mayPlace(ItemStack itemstack) {
        if (itemstack.getItem() instanceof PotionItem) {
            return true;
        }
        // A tool waiting in the trinket slot also takes its repair material here, such as a
        // diamond for a diamond tool or whatever a data pack names instead.
        return GeneralUtility.isRepairMaterialFor(level,
                getItemHandler().getStackInSlot(TrinketTableBlockEntity.TRINKET_SLOT), itemstack);
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
