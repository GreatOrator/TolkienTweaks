package com.greatorator.tolkienmobs.item.custom;

import com.greatorator.tolkienmobs.init.TolkienMobEffects;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import com.greatorator.tolkienmobs.item.TolkienItem;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

/**
 * The war horn of the campaign: one long note, and for as far as it carries nobody is held by fear.
 * <p>
 * The terror of the Nazgul, the Witch-king and the Watcher stones is meant to be unbearable alone -
 * that is the whole point of it - so the answer is not a weaker dose of it but a shared one: every
 * player within earshot is freed of it, the one who blew the horn included. It is deliberately not a
 * cure for anything else; poison, corrosion and the rest of the mod's harmful effects are left alone.
 */
public class WarHornItem extends TolkienItem {
    /** How far the note carries. A war horn is heard across a field, not across a room. */
    private static final double REACH = 24.0D;
    /** Long enough that the horn is a decision rather than something held down. */
    private static final int COOLDOWN_TICKS = 20 * 20;

    public WarHornItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, @Nonnull InteractionHand hand) {
        ItemStack horn = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    TolkienSounds.WAR_HORN.get(), SoundSource.PLAYERS, 4.0F, 1.0F);
            for (Player listener : level.getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(REACH))) {
                listener.removeEffect(TolkienMobEffects.PARALYSING_FEAR);
                listener.removeEffect(TolkienMobEffects.WATCHER_FEAR);
            }
            player.getCooldowns().addCooldown(horn, COOLDOWN_TICKS);
        }
        return InteractionResult.SUCCESS;
    }
}
