package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.world.registration.ArdaMap;
import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.List;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The captain's own fight, because the body the chapter stands him in has none.
 * <p>
 * Dugorim is the pack's dwarf, and the pack's dwarf is a villager - a {@code Villager} with a brain instead of a goal
 * list, which walks to work and panics when it is hurt and has no notion of hitting anybody. Nothing can be given to it
 * that makes it fight: the brain owns its movement, so an attack goal added beside it would be walked away from. So the
 * fight is written here instead, on the side that can win the argument - this runs after every body has ticked, so what
 * it sets is what stands.
 * <p>
 * He wakes when a player comes into the hall, turns to them, closes the distance and swings: one heavy blow a second at
 * arm's length, with the knockback a captain's weight gives it. He keeps the seat's own neighbourhood - the page's hunt
 * is a fight in a room, not a chase across the country - and he is only ever the one body, found by the tag the chapter
 * put on him.
 */
@EventBusSubscriber(modid = MODID)
public final class DugorimFight {
    /** How far from the seat a player has to be for the captain to notice them, and the most he will leave it. */
    private static final double WAKE = 24.0D;

    /** How close he has to be to swing. A little over the model's reach, since a dwarf's arms are short. */
    private static final double REACH = 2.8D;

    /** What one blow costs, and how long the swing takes to come round again: a blow a second. */
    private static final float DAMAGE = 6.0F;
    private static final int COOLDOWN = 20;

    /** How hard the blow shoves. Half of a player's own, since he is a dwarf and not a troll. */
    private static final double KNOCKBACK = 0.35D;

    /** When each captain last landed a blow, keyed on the body so a reloaded one starts fresh. */
    private static final java.util.Map<java.util.UUID, Integer> LAST_BLOW = new java.util.HashMap<>();

    /** How far away a player still sees the captain's bar: the hall and the stair down into it. */
    private static final double SHOW = 32.0D;

    /**
     * One bar per captain, keyed on the body.
     * <p>
     * The mod's own bosses each carry a {@link ServerBossEvent} as a field, which a body can do when it is its own class.
     * The captain is the pack's dwarf - a villager with no class of ours to hang a field on - so his bar is kept here,
     * beside the fight that is also written here for the same reason. It is taken down with him: a bar left on the
     * screen after the body it belonged to is gone is the one thing worse than no bar at all.
     */
    private static final java.util.Map<java.util.UUID, ServerBossEvent> BARS = new java.util.HashMap<>();

    private DugorimFight() {
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        ServerLevel level = server.getLevel(TolkienDimensions.ARDA);
        if (level == null) return;
        BlockPos seat = ArdaMap.keepThrone();
        if (!level.hasChunkAt(seat)) return;
        AABB hall = AABB.ofSize(seat.getCenter(), WAKE * 2.0D, 32.0D, WAKE * 2.0D);
        List<Entity> captains = level.getEntities((Entity) null, hall,
                entity -> entity instanceof Mob mob && mob.isAlive()
                        && mob.entityTags().contains(Campaign.DUGORIM_TAG));
        for (Entity entity : captains) {
            fight(level, (Mob) entity, seat, server.getTickCount());
            bar(level, (Mob) entity);
        }
        // A captain nobody can see any more takes his bar with him, whether he fell or the world let him go.
        BARS.entrySet().removeIf(entry -> {
            Entity body = level.getEntity(entry.getKey());
            if (body != null && body.isAlive()) return false;
            entry.getValue().removeAllPlayers();
            return true;
        });
    }

    /** The captain's bar, shown to whoever is close enough to be fighting him and filled by how he stands. */
    private static void bar(ServerLevel level, Mob captain) {
        ServerBossEvent bar = BARS.computeIfAbsent(captain.getUUID(), id -> new ServerBossEvent(id,
                Component.translatable("boss.tolkienmobs.dugorim"),
                BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10));
        bar.setProgress(Mth.clamp(captain.getHealth() / captain.getMaxHealth(), 0.0F, 1.0F));
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(captain) <= SHOW * SHOW) bar.addPlayer(player);
            else bar.removePlayer(player);
        }
    }

    /** One tick of the captain's side of the fight: wake, close, swing. */
    private static void fight(ServerLevel level, Mob captain, BlockPos seat, int now) {
        ServerPlayer nearest = null;
        double closest = WAKE * WAKE;
        for (ServerPlayer player : level.players()) {
            if (player.isCreative() || player.isSpectator() || !player.isAlive()) continue;
            double apart = player.distanceToSqr(seat.getX() + 0.5D, seat.getY(), seat.getZ() + 0.5D);
            if (apart > closest) continue;
            closest = apart;
            nearest = player;
        }
        if (nearest == null) return;
        captain.getLookControl().setLookAt(nearest, 30.0F, 30.0F);
        double reach = captain.distanceToSqr(nearest);
        if (reach > REACH * REACH) {
            // Walked rather than pathed once: the villager's brain starts its own stroll the moment this is quiet, so
            // the order is given every tick and is always the last one written.
            captain.getNavigation().moveTo(nearest, 1.0D);
            return;
        }
        captain.getNavigation().stop();
        Integer last = LAST_BLOW.put(captain.getUUID(), now);
        if (last != null && now - last < COOLDOWN) return;
        captain.swing(InteractionHand.MAIN_HAND, true);
        nearest.hurtServer(level, level.damageSources().mobAttack(captain), DAMAGE);
        nearest.knockback(KNOCKBACK, captain.getX() - nearest.getX(), captain.getZ() - nearest.getZ());
    }
}
