package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.network.manager.ConversationManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
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
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
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

    /**
     * The tag a campaign giver carries: the fixed NPC who hands a front's pages out rather than an
     * errand. It is what tells the herald in a hall from a traveller met on a road, since a giver
     * carrying it hands over a page instead of an offer.
     */
    public static final String CAMPAIGN_GIVER_TAG = "tolkienmobs:campaign_giver";

    /** How far a campaign giver is looked for before another is stood up in its place. */
    private static final double CAMPAIGN_NEAR = 100.0D;
    /** How far from the operator a campaign giver is stood, so it lands beside them and not inside them. */
    private static final int CAMPAIGN_SPAWN = 6;

    /** What standing a front's giver beside a player came to, so a caller can say which. */
    public enum Placement {
        /** A giver was put on the ground beside them. */
        STOOD,
        /** One of that alliance's was already within reach, so nothing was done. */
        ALREADY_THERE,
        /** Nowhere within reach had dry, solid ground to stand on. */
        NO_GROUND
    }

    /** How often a player is looked at, in ticks. */
    private static final int INTERVAL = 100;
    /** The wait between one giver and the next, so an arrival is an event and not scenery. */
    private static final long MIN_GAP = 6000L;
    private static final long MAX_GAP = 12000L;
    /** How far off a page's named recipient is looked for, which is as far as a nameplate reads. */
    private static final double TURN_IN_REACH = 64.0D;
    /** How far from the player a giver arrives, so they are met rather than dropped on. */
    private static final int MIN_SPAWN = 16;
    private static final int MAX_SPAWN = 28;
    /** One giver at a time near a player: the offer is theirs, and a crowd of them reads as a bug. */
    private static final double NEAR = 64.0;
    /** The home area a hall giver is kept inside: the middle of the room, six blocks out. */
    private static final int HALL_RADIUS = 6;
    /** How far a planted giver looks for someone to watch, which is the room and the step outside it. */
    private static final double WATCH = 12.0D;
    /** How fast that gaze turns, in degrees a tick, so it swings round rather than snapping. */
    private static final float WATCH_STEP = 6.0F;
    /** How often the gaze is turned, in ticks: every tenth of a second is as smooth as a body needs. */
    private static final int WATCH_INTERVAL = 2;

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
            Quest work = QuestPool.offer(player, random, employer).withGiver(label, giver.blockPosition(), giver.getUUID());
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

        String name = target.getName().getString();
        if (QuestLedger.turnInFirst(player, name)) return;
        // A page given as words is finished by hearing them out, and is paid where they were said.
        QuestLedger.spokeTo(player, name);
        // A page owed to somebody else is not this giver's to take, and saying so is kinder than
        // the silence of a giver who then has nothing to offer.
        String owed = QuestLedger.owedTo(player, name);
        if (owed != null) {
            player.sendSystemMessage(Component.translatable("quest.tolkienmobs.turn_in_elsewhere",
                    Component.literal(owed)).withStyle(ChatFormatting.GRAY));
            return;
        }
        // Work that is finished by listening is finished by coming back and asking for the rest of it:
        // the telling is the errand, so it is not spent in the same breath as the offer.
        String tale = QuestLedger.carriedTale(player);
        if (tale != null) {
            PacketDistributor.sendToPlayer(player,
                    new ConversationManager(name, tale, ConversationManager.Mode.OPEN));
            return;
        }

        // A hall herald does not leave the campaign page lying about as an offer to think about: he hands
        // it over, because the line is not one of the errands and there is nothing to choose between.
        Alliance herald = factionOf(target);
        if (herald != null && isCampaign(player.level(), target.getUUID())) {
            // A page already in the book is not written a second time - not by this herald, and not by
            // another of the same front. Two copies of one step would both have to be paid before the
            // front moved on, and only the copy the slot noted last could ever be paid at all.
            if (holdsPageFrom(player, target.getUUID()) || holdsCampaignPage(player)
                    || Campaign.holdsFrontPage(player, herald)) {
                player.sendSystemMessage(Component.translatable("quest.tolkienmobs.herald.carrying")
                        .withStyle(ChatFormatting.GRAY));
                // Opened answered: the page is in the book and has been read, so it is opened at its
                // last line rather than at the question the player has already answered.
                talk(player, target, herald, ConversationManager.Mode.ANSWERED);
                return;
            }
            // The line opens on a rank rather than on a walk through the door: a country does not trust its
            // war to a face it has never seen. The plate asks the same question of the same number, so a
            // mark can no longer advertise work the body refuses to hand over - and turn-in runs above this,
            // so a page already in hand is still paid for whoever carries it.
            if (FactionStandings.standing(player, herald) < FactionRank.STRANGER.threshold()) {
                player.sendSystemMessage(Component.translatable("quest.tolkienmobs.herald.stranger")
                        .withStyle(ChatFormatting.GRAY));
                speak(player, target, true);
                // And he says it to the player's face: the refusal is a leaf of its own, headed with
                // his answer, so being turned away is a thing that happens rather than a line in chat.
                PacketDistributor.sendToPlayer(player, new ConversationManager(
                        target.getName().getString(), "campaign.tolkienmobs.talk.refused",
                        ConversationManager.Mode.REFUSED));
                return;
            }
            Quest page = Campaign.page(player, herald);
            // A walk to ground this world has never had named is a page nobody can finish, so it
            // waits at the herald rather than sitting in a book as a dead end.
            if (page != null && !groundNamed(player, page)) {
                player.sendSystemMessage(Component.translatable("quest.tolkienmobs.herald.unnamed",
                        page.targetName()).withStyle(ChatFormatting.GRAY));
                return;
            }
            if (page != null) {
                Quest handed = page.withGiver(target.getName().getString(), target.blockPosition(),
                        target.getUUID());
                // The herald speaks when handing over the Rider page, the campaign's final page.
                if (Campaign.isCampaignPage(player, handed)) {
                    player.sendSystemMessage(Component.translatable("quest.tolkienmobs.herald.rider")
                            .withStyle(ChatFormatting.GOLD));
                }
                if (QuestLedger.accept(player, handed)) {
                    Campaign.noteHanded(player, herald, handed);
                    speak(player, target, false);
                    talk(player, target, herald, ConversationManager.Mode.OPEN);
                }
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
     * Whether a page that asks to be walked somewhere has somewhere in this world to be walked. Ground
     * the catalogue names but this world has never placed is named here, from the map, rather than
     * refused: the refusal was written to keep a page nobody can finish out of a book, but it left the
     * front stopped at that page for good, waiting on an operator who was never told to stand anywhere.
     * A place an operator has placed is left exactly where they put it.
     */
    private static boolean groundNamed(ServerPlayer player, Quest page) {
        Quest.Spot spot = page.spot();
        if (spot == null || spot.place() == null) return true;
        return Places.ensure(player, spot.place(), spot.radius()) != null;
    }

    /**
     * A giver holds work that is the player's to come back for, so it is made permanent and made
     * invulnerable: a mob that wanders into the scene cannot end a quest by killing the one thing that
     * can hand it in. Applied on the way into the world rather than only at spawn, so a giver already
     * standing in a saved world is protected on its next load and one tagged by hand is protected too.
     */
    /**
     * Plays a herald's recorded line at the body that said it, so the words come from where he stands
     * rather than from the player's own feet. Both lines are his voice whatever front he serves, because
     * a herald is one office wearing three countries' colours.
     */
    private static void speak(ServerPlayer player, Entity herald, boolean refusal) {
        var sound = (refusal
                ? com.greatorator.tolkienmobs.init.TolkienSounds.HERALD_REFUSE
                : com.greatorator.tolkienmobs.init.TolkienSounds.HERALD_OFFER).get();
        player.level().playSound(null, herald.blockPosition(), sound,
                net.minecraft.sounds.SoundSource.NEUTRAL, 0.85F, 1.0F);
    }
    /**
     * Opens a page's own words for the player, where that page has been written out.
     * <p>
     * A page in the book is a name, a place and a count, and the script behind it is a conversation:
     * what the speaker says, and the two things a player can say back to him. Nothing carried that, so
     * the page was handed over as one chat line and the best writing in the campaign stayed in a
     * document. A page's talk keys hang off the page's own name, and a page with none written is handed
     * over exactly as it was - so writing one is naming it in {@link Campaign#spokenKey} and adding the
     * words, not building a second feature.
     */
    private static void talk(ServerPlayer player, Entity herald, Alliance alliance,
                             ConversationManager.Mode mode) {
        String page = Campaign.spokenKey(player, alliance);
        if (page == null) return;
        PacketDistributor.sendToPlayer(player, new ConversationManager(herald.getName().getString(), page, mode));
    }

    private static void makeSafe(Entity giver) {
        if (!giver.isInvulnerable()) {
            giver.setInvulnerable(true);
        }
        if (giver instanceof Mob mob) {
            mob.setPersistenceRequired();
            Alliance alliance = factionOf(giver);
            if (alliance != null) {
                keepInHall(mob);
                // A hall cut into a world before the named givers existed still has its herald standing
                // in it, carrying the campaign tag but not this one. He is marked here so the hall and
                // the operator command agree on which body is the front's own, and an old world does not
                // gain a second giver standing beside the one it already had.
                if (mob.entityTags().contains(CAMPAIGN_TAG)) {
                    mob.addTag(CAMPAIGN_GIVER_TAG);
                    // A hall cut into a world before the givers were named still has "Herald of the
                    // West" at its bench. He is the same body, so he is given the name the book now
                    // writes down - but only while he still carries the old one, so a pack that named
                    // him itself keeps the name it chose. A herald already wearing the right name is
                    // only recoloured: the halls wrote it as the plain white text a template holds, and
                    // a front's giver is named in the gold the mark over his head is drawn in.
                    if (retiredName(mob) || campaignName(alliance).equals(nameOf(mob))) {
                        mob.setCustomName(goldName(alliance));
                        mob.setCustomNameVisible(true);
                    }
                }
            }
        }
    }

    /** Whether a herald still carries the plain name the halls used before their givers were named. */
    private static boolean retiredName(Mob herald) {
        return nameOf(herald).startsWith("Herald of");
    }

    /** The name a giver is wearing as plain text, or an empty string for one wearing none. */
    private static String nameOf(Mob giver) {
        Component name = giver.getCustomName();
        return name == null ? "" : name.getString();
    }

    /** The gold a front's giver is named in, which is the ink his mark is drawn with. */
    private static Component goldName(Alliance alliance) {
        return Component.literal(campaignName(alliance)).withStyle(ChatFormatting.GOLD);
    }

    /**
     * A hall's staff are the hall's furniture, not its patrol. The warden and the envoy already stand at
     * their benches with their brains off, and a herald left with his on walks out of the door and is next
     * seen somewhere in the country; he is planted the same way, so the room keeps a body at the bench
     * instead of an empty hall and a giver wandering the fields. He is not a statue - {@link #watch} turns
     * him to whoever comes in - and he keeps the spot the hall stood him on, so a hall is staffed however
     * many times a player leaves it.
     * <p>
     * The home area is set as well, for a world holding one of these saved from before he was planted: the
     * radius is the hall's own - six blocks out from the middle of a room sixteen wide.
     */
    private static void keepInHall(Mob herald) {
        if (!herald.hasHome()) {
            herald.setHomeTo(herald.blockPosition(), HALL_RADIUS);
        }
        if (!herald.isNoAi()) {
            herald.setNoAi(true);
        }
    }

    @SubscribeEvent
    public static void onGiverJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        Entity entity = event.getEntity();
        if (isGiver(entity)) makeSafe(entity);
    }

    /**
     * Turns a hall's givers toward whoever is nearest, which is the only moving a planted giver does. Read
     * from the player's own tick rather than from each entity's: a gaze only matters when there is somebody
     * in the room to see it, and the player tick is already the clock the rest of this class runs on.
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.tickCount % WATCH_INTERVAL != 0) return;
        if (!(player.level() instanceof ServerLevel level)) return;
        List<Entity> staff = level.getEntities(player, player.getBoundingBox().inflate(WATCH),
                entity -> entity instanceof Mob mob && mob.isNoAi()
                        && factionOf(entity) != null && isGiver(entity));
        for (Entity entity : staff) {
            watch((Mob) entity, player);
        }
    }

    /** A slow turn toward the player, so a staffed hall reads as watching rather than as three statues. */
    private static void watch(Mob giver, ServerPlayer player) {
        double dx = player.getX() - giver.getX();
        double dz = player.getZ() - giver.getZ();
        if (dx * dx + dz * dz < 0.25D) return;
        float wanted = (float) (Mth.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;
        float step = Mth.clamp(Mth.wrapDegrees(wanted - giver.getYRot()), -WATCH_STEP, WATCH_STEP);
        float yaw = giver.getYRot() + step;
        giver.setYRot(yaw);
        giver.setYHeadRot(yaw);
        giver.yBodyRot = yaw;
    }

    /**
     * Stands this alliance's named campaign giver beside the player, unless one of that alliance already
     * stands within {@link #CAMPAIGN_NEAR} blocks. This is what a hall will call once its template
     * carries a giver, and what the operator command calls today, so the two cannot come to disagree
     * about which body is a front's own.
     *
     * @return whether a giver was actually stood up
     */
    public static Placement ensureCampaignGiver(ServerPlayer player, Alliance alliance) {
        if (!(player.level() instanceof ServerLevel level)) return Placement.NO_GROUND;
        if (campaignGiverNear(level, player, alliance) != null) return Placement.ALREADY_THERE;
        return spawnCampaignGiver(level, player, alliance);
    }

    /** This alliance's campaign giver already standing near the player, if one is. */
    @Nullable
    private static Mob campaignGiverNear(ServerLevel level, ServerPlayer player, Alliance alliance) {
        AABB box = player.getBoundingBox().inflate(CAMPAIGN_NEAR);
        List<Entity> found = level.getEntities(player, box, entity -> entity instanceof Mob
                && entity.entityTags().contains(CAMPAIGN_GIVER_TAG) && factionOf(entity) == alliance);
        return found.isEmpty() ? null : (Mob) found.getFirst();
    }

    /**
     * Stands a named campaign giver on ground near the player, turned to face them. A giver is put beside
     * the player rather than on them: standing one in the block they occupy raises it inside their own
     * body, and that block may as well be a wall, a river or the middle of the air.
     */
    private static Placement spawnCampaignGiver(ServerLevel level, ServerPlayer player, Alliance alliance) {
        RandomSource random = level.getRandom();
        for (int attempt = 0; attempt < 12; attempt++) {
            BlockPos pos = groundNear(level, player, random);
            if (pos == null) continue;
            Entity entity = TolkienEntities.ENTITY_TTM_HUMAN.get().create(level, EntitySpawnReason.EVENT);
            if (!(entity instanceof Mob giver)) return Placement.NO_GROUND;
            giver.snapTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D,
                    player.getYRot() + 180.0F, 0.0F);
            giver.setPersistenceRequired();
            giver.addTag(GIVER_TAG);
            giver.addTag(alliance.giverTag());
            giver.addTag(CAMPAIGN_TAG);
            giver.addTag(CAMPAIGN_GIVER_TAG);

            giver.setCustomName(goldName(alliance));
            giver.setCustomNameVisible(true);

            // makeSafe already plants an alliance-tagged giver and gives it a home area, so neither is
            // done a second time here.
            makeSafe(giver);
            if (level.addFreshEntity(giver)) return Placement.STOOD;
        }
        return Placement.NO_GROUND;
    }

    /** The name a front's hall gives its giver, which is the name the book writes down. */
    public static String campaignName(Alliance alliance) {
        return switch (alliance) {
            case WEST -> "Maelgrin";
            case EAST -> "Guthlac";
            case HARAD -> "Zarabi";
        };
    }

    /** Solid dry ground a few blocks off the player, with room to stand in, or null if there is none. */
    @Nullable
    private static BlockPos groundNear(ServerLevel level, ServerPlayer player, RandomSource random) {
        BlockPos origin = player.blockPosition();
        int dx = random.nextInt(3, CAMPAIGN_SPAWN + 1) * (random.nextBoolean() ? 1 : -1);
        int dz = random.nextInt(3, CAMPAIGN_SPAWN + 1) * (random.nextBoolean() ? 1 : -1);
        int x = origin.getX() + dx;
        int z = origin.getZ() + dz;
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        BlockPos pos = new BlockPos(x, y, z);
        if (!level.getBlockState(pos).isAir() || !level.getBlockState(pos.above()).isAir()) return null;
        if (!level.getBlockState(pos.below()).isSolid()) return null;
        if (!level.getFluidState(pos).isEmpty()) return null;
        return pos;
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

    /** The name a giver is wearing, or an empty string for a body this world no longer has. */
    public static String giverName(Level level, UUID giver) {
        Entity entity = level.getEntity(giver);
        return entity == null ? "" : entity.getName().getString();
    }

    /**
     * The givers near a player wearing a given name, which is what a page's turn-in is matched to.
     * Only as far as a nameplate can be read: a body in another country is not waiting in front of
     * anybody, so it is not marked.
     */
    public static List<UUID> named(Level level, ServerPlayer player, String name) {
        return level.getEntities(player, player.getBoundingBox().inflate(TURN_IN_REACH),
                        entity -> entity.entityTags().contains(GIVER_TAG)
                                && name.equalsIgnoreCase(entity.getName().getString()))
                .stream().map(Entity::getUUID).toList();
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
