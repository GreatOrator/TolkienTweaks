package com.greatorator.tolkienmobs.item.custom;

import com.greatorator.tolkienmobs.item.TolkienItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class SleepingBagItem extends BedItem {   private boolean hasLore = false;

    public SleepingBagItem(Block block, Properties properties)
    {
        super(block, properties);
    }

    public SleepingBagItem setHasLore() {
        this.hasLore = true;
        return this;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (hasLore) {
            tooltipComponents.add(Component.translatable(getDescriptionId() + ".lore").withStyle(ChatFormatting.GOLD));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        }
    }
}