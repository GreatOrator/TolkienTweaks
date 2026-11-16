package com.greatorator.tolkienmobs.client;

import com.greatorator.tolkienmobs.faction.Contract;
import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * What the notice boards are saying to the player who is looking at them.
 * <p>
 * A board has nothing of its own to say, because the paper is in the player's own pack: the question
 * "is there work here" is a question about an inventory, and every player gets their own answer without
 * a packet being sent. Carry no contract and the board will post one; carry a finished contract and it
 * will pay; carry work still in hand and it will only repeat that it is carried, which is nothing worth
 * a mark.
 * <p>
 * The pack is read the way the board reads it - the first contract found is the answer, since the board
 * itself refuses a second one.
 */
public final class BoardMarks {

    private BoardMarks() {
    }

    /** The mark a board wears for this player, or null when there is nothing to say. */
    public static @Nullable Component mark() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return null;
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            Contract contract = player.getInventory().getItem(slot).get(TolkienDataComponents.CONTRACT.get());
            if (contract == null) continue;
            return contract.complete() ? QuestMarkRenderer.icon(QuestMarkRenderer.BOARD_TURN_IN) : null;
        }
        return QuestMarkRenderer.icon(QuestMarkRenderer.BOARD_AVAILABLE);
    }
}
