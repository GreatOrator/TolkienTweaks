package com.greatorator.tolkienmobs.block;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class BaseBlock extends Block {
    public boolean hasEffectOverride = false;
    private boolean canSpawnEntity = false;
    private boolean itemHasUse = false;
    private boolean hasLore = false;

    public BaseBlock(Properties properties) {
        super(properties);
    }

    public BaseBlock setEffectOverride() {
        this.hasEffectOverride = true;
        return this;
    }

    public BaseBlock setHasLore() {
        this.hasLore = true;
        return this;
    }

    public BaseBlock setItemHasUse() {
        this.itemHasUse = true;
        return this;
    }

    public BaseBlock setSpawnInfo() {
        this.canSpawnEntity = true;
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
