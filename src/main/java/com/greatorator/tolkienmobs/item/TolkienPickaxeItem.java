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
 * 26.1 dropped the pickaxe class: the mining tag, damage and speed are put on the properties by
 * {@code pickaxe}, so the item only has to add its own enchantment glint and lore.
 */
public class TolkienPickaxeItem extends Item {
    public boolean hasEffectOverride = false;
    private boolean hasLore = false;

    public TolkienPickaxeItem(ToolMaterial material, float attackDamage, float attackSpeed, Properties properties) {
        super(properties.pickaxe(material, attackDamage, attackSpeed));
    }

    public TolkienPickaxeItem setEffectOverride() {
        this.hasEffectOverride = true;
        return this;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return hasEffectOverride || super.isFoil(stack);
    }

    public TolkienPickaxeItem setHasLore() {
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
