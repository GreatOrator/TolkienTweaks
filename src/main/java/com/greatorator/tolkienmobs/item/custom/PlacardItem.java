package com.greatorator.tolkienmobs.item.custom;

import com.greatorator.tolkienmobs.block.custom.PlacardBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

/**
 * 26.1 moved a block's tooltip onto its item - {@code Block#appendHoverText} does not exist - so the
 * placard's lore line lives here. The flag still sits on the block and is read from it, which keeps
 * one source of truth for which placards show a line.
 */
public class PlacardItem extends BlockItem {
    public PlacardItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (this.getBlock() instanceof PlacardBlock placard && placard.hasLore()) {
            tooltipComponents.accept(Component.translatable(getDescriptionId() + ".lore").withStyle(ChatFormatting.GOLD));
        }
        super.appendHoverText(stack, context, display, tooltipComponents, tooltipFlag);
    }
}
