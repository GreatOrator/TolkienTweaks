package com.greatorator.tolkienmobs.item.custom;

import com.greatorator.tolkienmobs.faction.Campaign;
import com.greatorator.tolkienmobs.item.TolkienItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Glorfindel's beryl: the one thing in a pack whose business is the ground under the player's feet.
 * <p>
 * It was carried for a whole campaign and asked nothing, because having it was the whole of the test.
 * Now it is held up, and what it answers is the place it is held over - so the item does nothing by
 * itself that the world is not asked about first: the campaign decides what ground it is over, what
 * that ground has to say, and whether it has been asked here already.
 * <p>
 * It is held in the hand rather than swung, so it is a use rather than an attack, and it answers on
 * both sides so the animation plays for the player holding it.
 */
public class BerylItem extends TolkienItem {
    public BerylItem(Properties properties) {
        super(properties);
    }

    /**
     * Says what the stone is for, where a player looks before deciding an item has no use at all.
     * <p>
     * The whole of the beryl is a right click over open air, and a stone carried for a season before it
     * answers anything reads as decoration. The line is grey and sits above the lore, so the stone still
     * reads as a thing lent rather than as a tool with a manual.
     */
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable(getDescriptionId() + ".use").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, display, tooltip, flag);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer server) {
            Campaign.raiseStone(server);
        }
        return InteractionResult.SUCCESS;
    }
}
