package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.init.TolkienEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The folk who carry work around with them.
 * <p>
 * A giver is not a new mob: it is one of the mod's own travellers, spawned near a player, named, and
 * marked persistent so the work it is holding is still there when the player comes back. What makes it
 * a giver is a scoreboard tag and an offer in {@link QuestLedger} - so the whole feature costs no
 * entity, no model and no texture, and a giver spawned by a pack by hand works the same way.
 * <p>
 * The clock is per player, not per world: a player who is never looked at is never met, and a player
 * who walks away from one is left alone until the wait has passed. That wait is stored with their
 * quests, so logging out does not reset it.
 */
@EventBusSubscriber(modid = MODID)
public final class QuestGivers {
    /** The scoreboard tag every giver carries, and the only thing that makes an NPC one. */
    public static final String GIVER_TAG = "tolkienmobs:quest_giver";

    /** How often a player is looked at, in ticks. */
    private static final int INTERVAL = 100;
    /** The wait between one giver and the next, so an arrival is an event and not scenery. */
    private static final long MIN_GAP = 6000L;
    private static final long MAX_GAP = 12000L;
    /** How far from the player a giver arrives, so they are met rather than dropped on. */
    private static final int MIN_SPAWN = 16;
    private static final int MAX_SPAWN = 28;
    /** One giver at a time near a player: the offer is theirs, and a crowd of them reads as a bug. */
    private static final double NEAR = 64.0;

    private QuestGivers() {
    }

    public static void tick(ServerPlayer player) {
        if (player.tickCount % INTERVAL != 0) return;
        if (!(player.level() instanceof ServerLevel level)) return;
        if (!player.isAlive() || player.isSpectator()) return;

        // A fetch quest's progress is what the player is carrying, so it is read back here rather than
        // written on every pickup - once every five seconds is as often as the book needs it.
        QuestLedger.refreshFetch(player);

        long now = level.getGameTime();
        if (now < QuestLedger.nextGiver(player)) return;
        if (QuestLedger.active(player).size() >= QuestLedger.MAX_ACTIVE) return;

        if (giverNear(level, player) != null) {
            QuestLedger.setNextGiver(player, now + MIN_GAP);
            return;
        }
        RandomSource random = level.getRandom();
        if (spawn(level, player, random)) {
            QuestLedger.setNextGiver(player, now + MIN_GAP + Math.floorMod(random.nextLong(), MAX_GAP - MIN_GAP));
        } else {
            // No ground to stand on: try again shortly rather than writing off the whole wait.
            QuestLedger.setNextGiver(player, now + 600L);
        }
    }

    /** One giver, on solid dry ground, with work already in their hand. */
    private static boolean spawn(ServerLevel level, ServerPlayer player, RandomSource random) {
        BlockPos origin = player.blockPosition();
        for (int attempt = 0; attempt < 12; attempt++) {
            int dx = random.nextInt(MIN_SPAWN, MAX_SPAWN + 1) * (random.nextBoolean() ? 1 : -1);
            int dz = random.nextInt(MIN_SPAWN, MAX_SPAWN + 1) * (random.nextBoolean() ? 1 : -1);
            int x = origin.getX() + dx;
            int z = origin.getZ() + dz;
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos pos = new BlockPos(x, y, z);
            if (!level.getBlockState(pos).isAir() || !level.getBlockState(pos.above()).isAir()) continue;
            if (!level.getBlockState(pos.below()).isSolid()) continue;
            if (!level.getFluidState(pos).isEmpty()) continue;

            Entity entity = TolkienEntities.ENTITY_TTM_HUMAN.get().create(level, EntitySpawnReason.EVENT);
            if (!(entity instanceof Mob giver)) continue;
            giver.snapTo(x + 0.5D, y, z + 0.5D, random.nextFloat() * 360.0F, 0.0F);
            // A giver holds work that is the player's to come back for, so it may not despawn.
            giver.setPersistenceRequired();
            String nameKey = QuestPool.giverKey(random);
            giver.setCustomName(Component.translatable(nameKey));
            giver.setCustomNameVisible(true);
            giver.addTag(GIVER_TAG);
            if (!level.addFreshEntity(giver)) continue;

            // The work remembers who offered it and where they stood, so the book can say both later.
            Quest work = QuestPool.offer(random, employer(level, player)).withGiver(nameKey, giver.blockPosition());
            QuestLedger.offer(player, giver.getUUID(), work);
            player.sendSystemMessage(Component.translatable("quest.tolkienmobs.giver_arrived")
                    .withStyle(ChatFormatting.GRAY));
            return true;
        }
        return false;
    }

    /** The country a giver speaks for: the one the player is standing in, or none in the wilds. */
    private static Alliance employer(ServerLevel level, ServerPlayer player) {
        for (Alliance alliance : Alliance.values()) {
            if (level.getBiome(player.blockPosition()).is(alliance.countries())) return alliance;
        }
        return null;
    }

    /** The giver standing near the player, if one is. */
    private static Entity giverNear(ServerLevel level, ServerPlayer player) {
        AABB box = player.getBoundingBox().inflate(NEAR);
        List<Entity> givers = level.getEntities(player, box, entity -> entity.entityTags().contains(GIVER_TAG));
        return givers.isEmpty() ? null : givers.getFirst();
    }

    /**
     * What right-clicking a giver does, in order of what the player most wants: hand in work they have
     * finished, take the work on offer, or be told there is none.
     */
    @SubscribeEvent
    public static void onInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        Entity target = event.getTarget();
        if (!target.entityTags().contains(GIVER_TAG)) return;
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
        if (player.level().isClientSide()) return;

        if (QuestLedger.turnInFirst(player)) return;

        Quest offer = QuestLedger.offerOf(target.getUUID());
        if (offer == null) {
            player.sendSystemMessage(Component.translatable("quest.tolkienmobs.giver.idle")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }
        if (QuestLedger.accept(player, offer)) {
            QuestLedger.dropOffer(player, target.getUUID());
        }
    }

    /** Whether an entity is one of the mod's givers, for a command or an audit. */
    public static boolean isGiver(Entity entity) {
        return entity.entityTags().contains(GIVER_TAG);
    }

    /** The level a giver's offer is keyed on, so a pack can hand out work without an offer at all. */
    public static boolean holdsOffer(Level level, Entity giver) {
        return level.isClientSide() ? QuestLedger.clientHasOffer(giver.getUUID()) : QuestLedger.offerOf(giver.getUUID()) != null;
    }
}
