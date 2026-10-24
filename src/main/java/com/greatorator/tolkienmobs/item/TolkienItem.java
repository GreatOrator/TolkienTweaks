package com.greatorator.tolkienmobs.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class TolkienItem extends Item {
    public boolean hasEffectOverride = false;
    private boolean canSpawnEntity = false;
    private boolean itemHasUse = false;
    private boolean hasLore = false;
    private boolean hasInstructions = false;

    public TolkienItem(Properties properties) {
        super(properties);
    }

    public TolkienItem setEffectOverride() {
        this.hasEffectOverride = true;
        return this;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return hasEffectOverride || super.isFoil(stack);
    }

    public TolkienItem setHasLore() {
        this.hasLore = true;
        return this;
    }

    public TolkienItem setInstructions() {
        this.hasInstructions = true;
        return this;
    }

    public TolkienItem setItemHasUse() {
        this.itemHasUse = true;
        return this;
    }

    public TolkienItem setSpawnInfo() {
        this.canSpawnEntity = true;
        return this;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (hasLore) {
            tooltipComponents.accept(Component.translatable(getDescriptionId() + ".lore"));
            super.appendHoverText(stack, context, display, tooltipComponents, tooltipFlag);
        }
    }
}
