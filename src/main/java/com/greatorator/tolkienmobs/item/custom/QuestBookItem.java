package com.greatorator.tolkienmobs.item.custom;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.faction.QuestLedger;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

/**
 * The book a player's quests are written in.
 * <p>
 * It holds no state: the quests are the player's, kept in world data, and this item is only the way to
 * read them. Right-clicking opens the ledger on its Quests tab, which is where both lists live - the
 * book and the alliance ledger are one screen with two pages, because a player reading "who am I with"
 * and "what am I doing" is asking two halves of the same question.
 * <p>
 * A player is given one the first time they take work on, and only ever one: the ledger is written by
 * the quest, not by the paper.
 */
public class QuestBookItem extends Item {
    public QuestBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            TolkienMobsMain.proxy.openQuestBook();
        } else if (player instanceof ServerPlayer serverPlayer) {
            // The screen draws the cache the last sync left, so it is brought up to date as it opens.
            QuestLedger.sync(serverPlayer);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("item.tolkienmobs.quest_book.hint")
                .withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, display, tooltip, flag);
    }
}
