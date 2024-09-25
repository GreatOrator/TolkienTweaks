package com.greatorator.tolkienmobs.block;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

public abstract class TolkienBlock extends Block {
    public boolean hasEffectOverride = false;
    private boolean canSpawnEntity = false;
    private boolean itemHasUse = false;
    private boolean hasLore = false;

    public TolkienBlock(Properties properties) {
        super(properties);
    }

    public TolkienBlock setEffectOverride() {
        this.hasEffectOverride = true;
        return this;
    }

    public TolkienBlock setHasLore() {
        this.hasLore = true;
        return this;
    }

    public TolkienBlock setItemHasUse() {
        this.itemHasUse = true;
        return this;
    }

    public TolkienBlock setSpawnInfo() {
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
