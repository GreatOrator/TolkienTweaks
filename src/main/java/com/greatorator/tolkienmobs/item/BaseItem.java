package com.greatorator.tolkienmobs.item;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class BaseItem extends Item {
    public boolean hasEffectOverride = false;
    private boolean canSpawnEntity = false;
    private boolean itemHasUse = false;
    private boolean hasLore = false;

    public BaseItem(Properties properties) {
        super(properties);
    }

    public BaseItem setEffectOverride() {
        this.hasEffectOverride = true;
        return this;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return hasEffectOverride || super.isFoil(stack);
    }

    public BaseItem setHasLore() {
        this.hasLore = true;
        return this;
    }

    public BaseItem setItemHasUse() {
        this.itemHasUse = true;
        return this;
    }

    public BaseItem setSpawnInfo() {
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
