package com.greatorator.tolkienmobs.item.custom;

import com.greatorator.tolkienmobs.faction.Contract;
import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

/**
 * The paper a contract is written on. The item carries no state of its own: the terms are the component,
 * so a blank contract is one with nothing on it, and a contract is only ever as good as what it says.
 */
public class MercenaryContractItem extends Item {
    public MercenaryContractItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        Contract contract = stack.get(TolkienDataComponents.CONTRACT.get());
        if (contract == null) {
            tooltip.accept(Component.translatable("item.tolkienmobs.mercenary_contract.blank")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }

        tooltip.accept(Component.translatable("contract.tolkienmobs.employer",
                        contract.employer().displayName().copy().withStyle(contract.employer().chat()))
                .withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("contract.tolkienmobs.objective",
                        Component.literal(String.valueOf(contract.goal())),
                        contract.target().displayName().copy().withStyle(contract.target().chat()))
                .withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("contract.tolkienmobs.progress",
                        Component.literal(String.valueOf(contract.progress())),
                        Component.literal(String.valueOf(contract.goal())))
                .withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("contract.tolkienmobs.reward",
                        Component.literal(String.valueOf(contract.reward())))
                .withStyle(ChatFormatting.GOLD));
        if (contract.complete()) {
            tooltip.accept(Component.translatable("contract.tolkienmobs.complete")
                    .withStyle(ChatFormatting.GREEN));
        }

        super.appendHoverText(stack, context, display, tooltip, flag);
    }
}
