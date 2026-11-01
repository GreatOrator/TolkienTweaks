package com.greatorator.tolkienmobs.event.entity;

import com.greatorator.tolkienmobs.entity.boss.WitchKingEntity;
import com.greatorator.tolkienmobs.faction.Campaign;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The three keepers round the crown.
 * <p>
 * The campaign's last act was never meant to be one body. Three of the Nine stand on the sand at the edge
 * of the ring, two the player has met before and one they have not, and while one of them still does,
 * nothing lands on the Witch-king at all - so the page that names him asks for the whole ring and not for
 * the crown alone. Above all, this sits in an event rather than inside {@link WitchKingEntity}: it is a
 * fact about the campaign's own Ring-wraith and not about the boss, and a Witch-king an operator spawns,
 * or one a pack adds, keeps fighting exactly as it always did.
 */
@EventBusSubscriber(modid = MODID)
public final class CampaignGuardEvents {
    private CampaignGuardEvents() {
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof WitchKingEntity king)) return;
        if (!Campaign.guarded(king)) return;
        event.setCanceled(true);
        if (event.getSource().getEntity() instanceof ServerPlayer attacker) {
            Campaign.tellGuarded(king, attacker);
        }
    }
}
