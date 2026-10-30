package com.greatorator.tolkienmobs.faction;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The three ways a standing moves: killing a soldier of one alliance, handing tokens to a hall's
 * warden, and logging in to be told where you stand. It is also where the guard patrols are woken.
 */
@EventBusSubscriber(modid = MODID)
public final class FactionEvents {

    private FactionEvents() {
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            FactionStandings.sync(player);
        }
    }

    /**
     * A soldier's death moves the two alliances that are not his own upward and his own downward.
     * The downward step stops at the bottom of the rank the player holds, so a kill cannot cost a
     * rank that was already earned - only a hand-in at a rival's hall can.
     */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        Entity victim = event.getEntity();
        for (Alliance alliance : Alliance.values()) {
            if (!victim.getType().builtInRegistryHolder().is(alliance.soldiers())) continue;
            FactionStandings.adjust(player, alliance, -FactionRank.KILL_VALUE, true);
            for (Alliance rival : alliance.rivals()) {
                FactionStandings.adjust(player, rival, FactionRank.KILL_VALUE, true);
            }
            return;
        }
    }

    /**
     * Right-clicking a hall's warden with that alliance's tokens hands over every one of them. The
     * warden is an ordinary NPC carrying the two scoreboard tags the hall's template was placed with.
     */
    @SubscribeEvent
    public static void onEnvoyInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        Entity target = event.getTarget();
        if (!(target instanceof LivingEntity living) || !living.entityTags().contains(Alliance.ENVOY_TAG)) return;

        Alliance alliance = allianceOf(living);
        if (alliance == null) return;
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
        if (player.level().isClientSide()) return;

        int taken = FactionStandings.handIn(player, alliance);
        if (taken == 0) {
            player.sendSystemMessage(Component.translatable("faction.tolkienmobs.no_tokens", alliance.displayName())
                    .withStyle(ChatFormatting.GRAY));
            return;
        }
        FactionRank rank = FactionStandings.rank(player, alliance);
        player.sendSystemMessage(Component.translatable("faction.tolkienmobs.hand_in",
                        taken, alliance.displayName(), FactionStandings.standing(player, alliance), rankName(rank))
                .withStyle(ChatFormatting.WHITE));
        player.level().playSound(null, player.blockPosition(), SoundEvents.VILLAGER_YES, SoundSource.PLAYERS, 0.6F, 1.0F);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            FactionGuards.tick(player);
        }
    }

    /** The alliance whose tag this warden carries, or null if it carries none of them. */
    public static Alliance allianceOf(Entity warden) {
        for (Alliance alliance : Alliance.values()) {
            if (warden.entityTags().contains(alliance.envoyTag())) return alliance;
        }
        return null;
    }

    public static Component rankName(FactionRank rank) {
        return Component.translatable("faction.tolkienmobs.rank." + rank.id()).withStyle(rank.chat());
    }

}
