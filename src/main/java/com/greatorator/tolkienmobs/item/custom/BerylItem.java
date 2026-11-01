package com.greatorator.tolkienmobs.item.custom;

import com.greatorator.tolkienmobs.faction.Campaign;
import com.greatorator.tolkienmobs.item.TolkienItem;
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

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer server) {
            Campaign.raiseStone(server);
        }
        return InteractionResult.SUCCESS;
    }
}
