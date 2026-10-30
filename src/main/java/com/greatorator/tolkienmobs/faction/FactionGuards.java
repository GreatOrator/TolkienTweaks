package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.entity.monster.FactionGuardEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * What a country does about a player it will not have.
 * <p>
 * A hostile standing raises the alliance's own soldiers: two of them muster at the hall when the
 * player walks up to its warden, and one patrol is allowed on them in the alliance's country. The
 * cap is counted on what is standing near the player, not on a timer, so a player who kills the
 * patrol is met by another one and a player who runs is simply left behind.
 */
public final class FactionGuards {
    /** How often a player is looked at, in ticks. */
    private static final int INTERVAL = 100;
    /** A muster at a hall only happens when its warden is this close. */
    private static final int HALL_REACH = 40;
    /** Soldiers already near the player: count them as the patrol and raise no more. */
    private static final int PATROL_LIMIT = 3;
    /** How far from the player a guard is placed, so it is not on top of them. */
    private static final int MIN_SPAWN = 12;
    private static final int MAX_SPAWN = 20;
    /** Where a guard is looked for before it is counted. */
    private static final double COUNT_REACH = 48.0;

    private FactionGuards() {
    }

    public static void tick(ServerPlayer player) {
        if (player.tickCount % INTERVAL != 0) return;
        if (!(player.level() instanceof ServerLevel level)) return;
        if (player.isCreative() || player.isSpectator() || !player.isAlive()) return;

        for (Alliance alliance : Alliance.values()) {
            if (!FactionStandings.isHostile(player, alliance)) continue;
            boolean atHall = wardenNear(level, player, alliance) != null;
            boolean inCountry = level.getBiome(player.blockPosition()).is(alliance.countries());
            if (!atHall && !inCountry) continue;
            if (guardsNear(level, player, alliance) >= PATROL_LIMIT) continue;
            // A hall musters two, because its warden is standing right there; open country sends one.
            int count = atHall ? 2 : 1;
            for (int i = 0; i < count; i++) {
                spawn(level, player, alliance, atHall);
            }
        }
    }

    private static void spawn(ServerLevel level, ServerPlayer player, Alliance alliance, boolean atHall) {
        BlockPos origin = player.blockPosition();
        var random = level.getRandom();
        for (int attempt = 0; attempt < 8; attempt++) {
            int dx = random.nextInt(MIN_SPAWN, MAX_SPAWN + 1) * (random.nextBoolean() ? 1 : -1);
            int dz = random.nextInt(MIN_SPAWN, MAX_SPAWN + 1) * (random.nextBoolean() ? 1 : -1);
            int x = origin.getX() + dx;
            int z = origin.getZ() + dz;
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos pos = new BlockPos(x, y, z);
            // The ground has to be somewhere to stand: two clear blocks and something solid under them.
            if (!level.getBlockState(pos).isAir() || !level.getBlockState(pos.above()).isAir()) continue;
            if (!level.getBlockState(pos.below()).isSolid()) continue;
            Entity entity = alliance.guard().create(level, EntitySpawnReason.EVENT);
            if (!(entity instanceof Mob guard)) continue;
            // Every alliance sends the same soldier, so the one it serves is set before it lands - both for
            // the skin it wears and for the patrol it is counted in.
            if (guard instanceof FactionGuardEntity factionGuard) {
                factionGuard.setAlliance(alliance);
            }
            guard.snapTo(x + 0.5D, y, z + 0.5D, random.nextFloat() * 360.0F, 0.0F);
            guard.setPersistenceRequired();
            guard.setTarget(player);
            if (level.addFreshEntity(guard)) return;
        }
    }

    /** The patrol already standing near the player. A guard of another alliance is not in it. */
    private static int guardsNear(ServerLevel level, ServerPlayer player, Alliance alliance) {
        AABB box = player.getBoundingBox().inflate(COUNT_REACH);
        List<Mob> found = level.getEntitiesOfClass(Mob.class, box, mob -> mob.getType() == alliance.guard()
                && (!(mob instanceof FactionGuardEntity guard) || guard.alliance() == alliance));
        return found.size();
    }

    /** The hall's warden, if the player is standing close enough to one. */
    private static Entity wardenNear(ServerLevel level, ServerPlayer player, Alliance alliance) {
        AABB box = player.getBoundingBox().inflate(HALL_REACH);
        List<Entity> wardens = level.getEntities(player, box,
                entity -> entity.entityTags().contains(alliance.envoyTag()));
        return wardens.isEmpty() ? null : wardens.getFirst();
    }
}
