package com.greatorator.tolkienmobs.item.custom;

import com.greatorator.tolkienmobs.TolkienMobsConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

/**
 * A magnet upgrade, whose lore states the reach the tier is configured to give.
 *
 * <p>The number comes from the config rather than the language file, so a pack that changes a tier's
 * reach does not leave the item telling players the old one. The item is registered without
 * {@code setHasLore}, because this is the line that stands in its place.
 */
public class MagnetUpgradeItem extends UpgradeItem {
    private final int tier;

    /** @param tier 1 iron, 2 gold, 3 mithril: which of the three reach settings the lore reads. */
    public MagnetUpgradeItem(Item.Properties properties, int tier) {
        super(properties);
        this.tier = tier;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable(getDescriptionId() + ".lore", TolkienMobsConfig.magnetRange(tier)));
        super.appendHoverText(stack, context, display, tooltip, flag);
    }
}
