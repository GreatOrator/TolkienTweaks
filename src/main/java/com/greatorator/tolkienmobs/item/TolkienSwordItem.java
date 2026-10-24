package com.greatorator.tolkienmobs.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

/**
 * 26.1 dropped the sword class: the damage, speed and the sword's components are put on the
 * properties by {@code sword}, so the item only has to add its own enchantment glint and lore.
 */
public class TolkienSwordItem extends Item {
    public boolean hasEffectOverride = false;
    private boolean hasLore = false;

    public TolkienSwordItem(ToolMaterial material, float attackDamage, float attackSpeed, Properties properties) {
        super(properties.sword(material, attackDamage, attackSpeed));
    }

    public TolkienSwordItem setEffectOverride() {
        this.hasEffectOverride = true;
        return this;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return hasEffectOverride || super.isFoil(stack);
    }

    public TolkienSwordItem setHasLore() {
        this.hasLore = true;
        return this;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (hasLore) {
            tooltipComponents.accept(Component.translatable(getDescriptionId() + ".lore").withStyle(ChatFormatting.GOLD));
            super.appendHoverText(stack, context, display, tooltipComponents, tooltipFlag);
        }
    }
}
