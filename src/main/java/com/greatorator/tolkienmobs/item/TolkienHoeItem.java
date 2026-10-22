package com.greatorator.tolkienmobs.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class TolkienHoeItem extends HoeItem {
    public boolean hasEffectOverride = false;
    private boolean hasLore = false;

    public TolkienHoeItem(ToolMaterial material, float attackDamage, float attackSpeed, Properties properties) {
        super(material, attackDamage, attackSpeed, properties);
    }

    public TolkienHoeItem setEffectOverride() {
        this.hasEffectOverride = true;
        return this;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return hasEffectOverride || super.isFoil(stack);
    }

    public TolkienHoeItem setHasLore() {
        this.hasLore = true;
        return this;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (hasLore) {
            tooltipComponents.add(Component.translatable(getDescriptionId() + ".lore").withStyle(ChatFormatting.GOLD));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        }
    }
}
