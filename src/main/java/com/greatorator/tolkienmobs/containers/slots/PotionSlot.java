package com.greatorator.tolkienmobs.containers.slots;

import com.greatorator.tolkienmobs.block.custom.entity.TrinketTableBlockEntity;
import com.greatorator.tolkienmobs.util.GeneralUtility;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class PotionSlot extends ResourceHandlerSlot {
    public static final Identifier backgroundLoc = null;
    private final Level level;

    public PotionSlot(Level level, ResourceHandler<ItemResource> itemHandler, IndexModifier<ItemResource> slotModifier, int index, int xPosition, int yPosition, Identifier backgroundLoc) {
        super(itemHandler, slotModifier, index, xPosition, yPosition);
        this.level = level;
    }

    @Override
    public boolean mayPlace(ItemStack itemstack) {
        if (itemstack.getItem() instanceof PotionItem) {
            return true;
        }
        // A tool waiting in the trinket slot also takes its repair material here, such as a
        // diamond for a diamond tool or whatever a data pack names instead.
        return GeneralUtility.isRepairMaterialFor(level,
                ItemUtil.getStack(getResourceHandler(), TrinketTableBlockEntity.TRINKET_SLOT), itemstack);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
