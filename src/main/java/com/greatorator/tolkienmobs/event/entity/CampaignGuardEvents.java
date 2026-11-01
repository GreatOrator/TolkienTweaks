package com.greatorator.tolkienmobs.event.entity;

import com.greatorator.tolkienmobs.entity.boss.WitchKingEntity;
import com.greatorator.tolkienmobs.faction.Campaign;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The crown, unhurtable in both the places the campaign puts him.
 * <p>
 * The campaign's last act was never meant to be one body. He is met twice: sat in his own house, where the
 * page that raised the castle sends the player and where he names the sand, and again on that sand behind
 * three of the Nine, two of whom the player has met before and one they have not. Neither meeting is a
 * fight the campaign hands over early - the seated one is a man in a chair who cannot be killed at his own
 * door, and the one in the ring cannot be touched while a keeper still stands - so the page that names him
 * asks for the whole ring and not for the crown alone.
 * <p>
 * Above all, this sits in an event rather than inside {@link WitchKingEntity}: it is a fact about the
 * campaign's own Ring-wraith and not about the boss, and a Witch-king an operator spawns, or one a pack
 * adds, keeps fighting exactly as it always did.
 */
@EventBusSubscriber(modid = MODID)
public final class CampaignGuardEvents {
    private CampaignGuardEvents() {
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof WitchKingEntity king)) return;
        if (Campaign.guarded(king)) {
            event.setCanceled(true);
            if (event.getSource().getEntity() instanceof ServerPlayer attacker) {
                Campaign.tellGuarded(king, attacker);
            }
            return;
        }
        if (Campaign.seated(king)) {
            event.setCanceled(true);
            if (event.getSource().getEntity() instanceof ServerPlayer attacker) {
                Campaign.tellSeated(king, attacker);
            }
        }
    }
}
