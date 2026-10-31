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
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;
import java.util.UUID;

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
    /**
     * The tag a giver carries when the work it holds is the mainline rather than an errand. The mark
     * over its head is gold instead of blue, which is the only thing a player needs to tell the two
     * apart before they have read either.
     */
    public static final String CAMPAIGN_TAG = "tolkienmobs:campaign";

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
    /** The home area a hall giver is kept inside: the middle of the room, six blocks out. */
    private static final int HALL_RADIUS = 6;

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
        if (QuestLedger.bountiesFull(player)) return;

        if (giverNear(level, player) != null) {
            QuestLedger.setNextGiver(player, now + MIN_GAP);
            return;
        }
        RandomSource random = level.getRandom();
        if (spawnNear(level, player, random)) {
            QuestLedger.setNextGiver(player, now + MIN_GAP + Math.floorMod(random.nextLong(), MAX_GAP - MIN_GAP));
        } else {
            // No ground to stand on: try again shortly rather than writing off the whole wait.
            QuestLedger.setNextGiver(player, now + 600L);
        }
    }

    /** One giver, on solid dry ground, with work already in their hand. */
    public static boolean spawnNear(ServerLevel level, ServerPlayer player, RandomSource random) {
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
            String label = QuestPool.giverLabel(random);
            giver.setCustomName(Quest.label(label));
            giver.setCustomNameVisible(true);
            giver.addTag(GIVER_TAG);
            // The alliance is read once, and only decides the work this giver holds out: the clothes
            // belong to the giver standing in a hall, which carries an alliance tag of its own.
            Alliance employer = employer(player);
            makeSafe(giver);
            if (!level.addFreshEntity(giver)) continue;

            // The work remembers who offered it and where they stood, so the book can say both later.
            Quest work = QuestPool.offer(random, employer).withGiver(label, giver.blockPosition(), giver.getUUID());
            QuestLedger.offer(player, giver.getUUID(), work);
            player.sendSystemMessage(Component.translatable("quest.tolkienmobs.giver_arrived")
                    .withStyle(ChatFormatting.GRAY));
            return true;
        }
        return false;
    }

    /**
     * Puts a giver beside a player at once, for an operator staging a scene or trying the work out. The
     * wait between givers and the one-giver rule are the player's experience of the feature rather than
     * rules about the world, so a command skips both and asks only for ground to stand on.
     *
     * @return whether a giver found somewhere to stand
     */
    public static boolean forceSpawn(ServerPlayer player) {
        return player.level() instanceof ServerLevel level && spawnNear(level, player, level.getRandom());
    }

    /** The country a giver speaks for: the one the player is standing in, or none in the wilds. */
    public static Alliance employer(ServerPlayer player) {
        Level level = player.level();
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

        // A hall herald does not leave the campaign page lying about as an offer to think about: he hands
        // it over, because the line is not one of the errands and there is nothing to choose between.
        Alliance herald = factionOf(target);
        if (herald != null && isCampaign(player.level(), target.getUUID())
                && !holdsPageFrom(player, target.getUUID()) && !holdsCampaignPage(player)) {
            Quest page = Campaign.page(player, herald);
            if (page != null) {
                Quest handed = page.withGiver(target.getName().getString(), target.blockPosition(),
                        target.getUUID());
                // The herald speaks when handing over the Rider page, the campaign's final page.
                if (Campaign.isCampaignPage(player, handed)) {
                    player.sendSystemMessage(Component.translatable("quest.tolkienmobs.herald.rider")
                            .withStyle(ChatFormatting.GOLD));
                }
                if (QuestLedger.accept(player, handed)) Campaign.noteHanded(player, herald, handed);
                return;
            }
            // His own front is settled, so there is no page to hand over: he says where the war stands
            // rather than offering work he does not have.
            player.sendSystemMessage(Component.translatable(Campaign.done(player)
                            ? "quest.tolkienmobs.herald.settled"
                            : "quest.tolkienmobs.herald.waiting")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }

        // A player carrying as much work as the leaf has room for is not given more: what they hold is
        // theirs to finish, and one more would only be a row the page cannot show.
        if (QuestLedger.bountiesFull(player)) {
            player.sendSystemMessage(Component.translatable("quest.tolkienmobs.busy")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }

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

    /**
     * A giver holds work that is the player's to come back for, so it is made permanent and made
     * invulnerable: a mob that wanders into the scene cannot end a quest by killing the one thing that
     * can hand it in. Applied on the way into the world rather than only at spawn, so a giver already
     * standing in a saved world is protected on its next load and one tagged by hand is protected too.
     */
    private static void makeSafe(Entity giver) {
        if (!giver.isInvulnerable()) {
            giver.setInvulnerable(true);
        }
        if (giver instanceof Mob mob) {
            mob.setPersistenceRequired();
            if (factionOf(giver) != null) keepInHall(mob);
        }
    }

    /**
     * A hall giver stands in a room, so it is given the room. A herald cut into a structure carries its own
     * home area and its own brain; one saved in a world from before that was frozen and has neither, and is
     * repaired here on load rather than being left standing where it was placed. The radius is the hall
     * own - six blocks out from the middle of a room sixteen wide - and the entity pulls any walk target
     * that leaves it back inside.
     */
    private static void keepInHall(Mob herald) {
        if (!herald.hasHome()) {
            herald.setHomeTo(herald.blockPosition(), HALL_RADIUS);
        }
        if (herald.isNoAi()) {
            herald.setNoAi(false);
        }
    }

    @SubscribeEvent
    public static void onGiverJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        Entity entity = event.getEntity();
        if (isGiver(entity)) makeSafe(entity);
    }

    /**
     * Spawns a named campaign giver at the given position. This can be called by commands or
     * structure placement logic to ensure campaign NPCs are present at their designated locations.
     */
    public static void ensureCampaignGiver(ServerPlayer player, Alliance alliance) {
        if (!(player.level() instanceof ServerLevel level)) return;
        // Check if a campaign giver for this alliance already exists near the player
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof Mob mob && mob.entityTags().contains("tolkienmobs:campaign_giver")
                    && factionOf(mob) == alliance) {
                double dist = mob.position().distanceTo(player.position());
                if (dist < 100.0) return; // Already exists nearby
            }
        }
        // Spawn a new campaign giver at the player's location
        spawnCampaignGiver(level, player.blockPosition(), alliance);
    }

    /**
     * Spawns a named campaign giver at the given position.
     */
    private static void spawnCampaignGiver(ServerLevel level, BlockPos pos, Alliance alliance) {
        Entity entity = TolkienEntities.ENTITY_TTM_HUMAN.get().create(level, EntitySpawnReason.EVENT);
        if (!(entity instanceof Mob giver)) return;
        giver.snapTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
        giver.setPersistenceRequired();
        giver.addTag(GIVER_TAG);
        giver.addTag(alliance.giverTag());
        giver.addTag(CAMPAIGN_TAG);
        giver.addTag("tolkienmobs:campaign_giver");
        
        String name = switch (alliance) {
            case WEST -> "Maelgrin";
            case EAST -> "Guthlac";
            case HARAD -> "Zarabi";
        };
        giver.setCustomName(Component.literal(name));
        giver.setCustomNameVisible(true);
        
        makeSafe(giver);
        keepInHall(giver);
        
        level.addFreshEntity(giver);
    }

    /** The alliance whose work a giver carries, or null for one placed without an alliance of its own. */
    public static Alliance factionOf(Entity giver) {
        for (Alliance alliance : Alliance.values()) {
            if (giver.entityTags().contains(alliance.giverTag())) return alliance;
        }
        return null;
    }

    /**
     * The giver skin an entity wears, as 0 for anything that is not a giver and otherwise an alliance's
     * ordinal plus one. Only a body carrying an alliance tag wears one of the hall skins: a giver met
     * in the world carries no such tag and keeps the clothes of its own folk.
     */
    public static int giverSkinOf(Entity entity) {
        if (!isGiver(entity)) return 0;
        Alliance alliance = factionOf(entity);
        return alliance == null ? 0 : alliance.ordinal() + 1;
    }

    /** Whether the work a giver holds is the mainline's, which the mark over its head draws in gold. */
    public static boolean isCampaign(Level level, UUID giver) {
        Entity entity = level.getEntity(giver);
        return entity != null && entity.entityTags().contains(CAMPAIGN_TAG);
    }

    /** Whether the player is already carrying the page this giver handed them. */
    private static boolean holdsPageFrom(ServerPlayer player, UUID giver) {
        for (Quest quest : QuestLedger.active(player)) {
            if (giver.equals(quest.giverId())) return true;
        }
        return false;
    }

    /**
     * Whether the player is already carrying the campaign's own page. Every herald offers that one, so
     * without this a second hall would hand over a second copy of the same quarry, and only the copy the
     * server noted last could ever settle it.
     */
    private static boolean holdsCampaignPage(ServerPlayer player) {
        for (Quest quest : QuestLedger.active(player)) {
            if (Campaign.isCampaignPage(player, quest)) return true;
        }
        return false;
    }

    /** Whether an entity is one of the mod givers, for a command or an audit. */
    public static boolean isGiver(Entity entity) {
        return entity.entityTags().contains(GIVER_TAG);
    }

    /** The level a giver's offer is keyed on, so a pack can hand out work without an offer at all. */
    public static boolean holdsOffer(Level level, Entity giver) {
        return level.isClientSide() ? QuestLedger.clientHasOffer(giver.getUUID()) : QuestLedger.offerOf(giver.getUUID()) != null;
    }
}
