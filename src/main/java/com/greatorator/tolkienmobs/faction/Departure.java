package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.entity.TolkienVillagerEntity;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.init.TolkienItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The chapter-one seam: Elrond's summons, and the company leaving the courtyard.
 * <p>
 * The page that ends chapter one - {@code Hasten Their Departure}, settled in Elrond's library - does not
 * hand the next page over. It leaves a note instead, and the next page stays shut until the note has been
 * answered: the player is asked to come to the courtyard and see the Company off, and only when they have
 * watched it go does the road north open and page 26 fall into their book.
 * <p>
 * That is the old pack's own order of events, recovered from its customNPCs scenes. There the whole thing
 * was one NPC scene - ten bodies spawned, lines spoken on a tick, the eight walk scenes started, and a
 * second scene for Elrond's last words - so this class is the same shape: a tick-driven beat list, the
 * cast placed on the ground the plaza's own marks give, and the lines spoken in order.
 * <p>
 * When the last line of the script is said the courtyard empties: the nine who go walk off it in lanes of
 * their own on the bearing the ring itself gives - the open side, away from Elrond - and Elrond and Bilbo
 * stay where they were stood until the nine are off the plaza, then go with the scene. Where the road runs past the plaza's rim
 * is not this class's business yet: the walk ends where the courtyard does. The old script's walk mark and
 * its spawn layout are in {@code docs/departure-scene.md}.
 * <p>
 * Two player tags are the whole of the state, so the seam survives a reload: the summons, and having seen
 * the scene. A world that had the note and logged out comes back to the same courtyard and the same scene.
 */
public final class Departure {

    /** The note Elrond leaves: it is carried until the courtyard has been walked to. */
    private static final String SUMMONS = "tolkienmobs:summons_courtyard";
    /** The scene has been watched, so the note is answered and page 26 may be handed over. */
    private static final String SEEN = "tolkienmobs:departure_seen";

    /**
     * Where the company gathers, if the owner has marked it: the courtyard, as a pin of its own beside the
     * city's. Unpinned, the scene gathers at Elrond's own stand in the valley, which is where a player who
     * has just been told to find him is already walking.
     */
    public static final String GATHER_PIN = "rivendell.scene";
    /** How near the mark a player has to come for the scene to begin, in blocks. */
    private static final double GATHER_REACH = 24.0D;
    /** How far they may wander once it has begun before it lets them go, in blocks. */
    private static final double STAGE_REACH = 96.0D;
    /** Ticks between two lines: three seconds, which is slow enough to read and quick enough to hold. */
    private static final int GAP = 60;
    /** Ticks kept after the last line before the company is taken away and the page is handed over. */
    private static final int TAIL = 60;
    /** How far past the gathering mark a leaver walks before it is out of the courtyard, in blocks. */
    private static final double GONE = 24.0D;
    /** How far a body steps in one tick: a walking player's pace, so the legs keep their own rhythm. */
    private static final double WALK_STEP = 0.2D;
    /** Ticks after which a walk that has not finished is given up on and the bodies are taken away. */
    private static final long WALK_LIMIT = 600L;

    /** The two messages the note speaks, and the note's own two lines. */
    private static final String NOTE_NAME = "campaign.tolkienmobs.scene.note";
    private static final String NOTE_LORE = "campaign.tolkienmobs.scene.note.lore";
    private static final String SUMMON_SAID = "campaign.tolkienmobs.scene.summon";
    /** The line keys the scene speaks, numbered as the beat list reads them. */
    private static final String LINE = "campaign.tolkienmobs.scene.departure.";

    /** The shows running, by player. A scene is one player's, since it is spoken to the one who came. */
    private static final Map<UUID, Show> SHOWS = new HashMap<>();

    // ---- the cast ----

    /** Which of the mod's folk a body is drawn on. The four hobbits are hobbits, not scaled men. */
    private enum Kind { MAN, HOBBIT, DWARF }

    /** One body: the name it wears, the folk it stands on, and where it stands from the gathering mark. */
    private record Cast(String name, String slug, Kind kind, int dx, int dz) {
    }

    /**
     * The eleven who wait in the courtyard, placed as the old pack placed them: Elrond at the middle, the
     * others in the loose half-ring in front of him, the hobbits low and the tall folk behind.
     * <p>
     * Elrond is a body of the scene's own, not the campaign's keeper. The keeper stays at his post in the
     * valley for every page that names him - the audience, the tidings carried back - and this one is
     * stood for the departure and taken away with the rest when the last line is said. Two Elronds can
     * therefore be in the valley at once, one at the library and one at the plaza, which is the price of
     * a cutscene that does not move the campaign's own about.
     */
    private static final List<Cast> CAST = List.of(
            new Cast("Elrond", "elrond", Kind.MAN, -1, 0),
            new Cast("Gandalf", "gandalf", Kind.MAN, -2, 6),
            new Cast("Bilbo", "bilbo", Kind.HOBBIT, 0, 5),
            new Cast("Aragorn", "aragorn", Kind.MAN, 6, 6),
            new Cast("Gimli", "gimli", Kind.DWARF, 7, 0),
            new Cast("Legolas", "legolas", Kind.MAN, 8, 4),
            new Cast("Boromir", "boromir", Kind.MAN, 8, 2),
            new Cast("Merry", "merry", Kind.HOBBIT, 5, 1),
            new Cast("Pippin", "pippin", Kind.HOBBIT, 5, 4),
            new Cast("Samwise", "samwise", Kind.HOBBIT, 5, 2),
            new Cast("Frodo", "frodo", Kind.HOBBIT, 5, 3));

    // ---- the script ----

    /** One spoken line: who says it, which key holds it, and how the old pack dressed it. */
    private record Line(String who, String key, ChatFormatting colour, boolean italic) {
    }

    /**
     * The departure in the order it is said, which is the old pack's order: Gandalf opens, the hobbits and
     * Aragorn and Sam and Merry each have their turn, Gandalf closes the sending, and Elrond says the five
     * lines that belong to the valley once they are gone.
     */
    private static final List<Line> LINES = List.of(
            line("Gandalf", 1, ChatFormatting.GOLD, false),
            line("Gandalf", 2, ChatFormatting.GOLD, false),
            line("Gandalf", 3, ChatFormatting.GOLD, false),
            line("Frodo", 4, ChatFormatting.WHITE, true),
            line("Bilbo", 5, ChatFormatting.WHITE, true),
            line("Frodo", 6, ChatFormatting.WHITE, true),
            line("Bilbo", 7, ChatFormatting.WHITE, true),
            line("Aragorn", 8, ChatFormatting.WHITE, true),
            line("Aragorn", 9, ChatFormatting.WHITE, true),
            line("Aragorn", 10, ChatFormatting.WHITE, true),
            line("Aragorn", 11, ChatFormatting.WHITE, true),
            line("Aragorn", 12, ChatFormatting.WHITE, true),
            line("Samwise", 13, ChatFormatting.WHITE, true),
            line("Samwise", 14, ChatFormatting.WHITE, true),
            line("Samwise", 15, ChatFormatting.WHITE, true),
            line("Samwise", 16, ChatFormatting.WHITE, true),
            line("Merry", 17, ChatFormatting.WHITE, true),
            line("Merry", 18, ChatFormatting.WHITE, true),
            line("Gandalf", 19, ChatFormatting.GOLD, false),
            line("Gandalf", 20, ChatFormatting.GOLD, false),
            line("Elrond", 21, ChatFormatting.GOLD, false),
            line("Elrond", 22, ChatFormatting.GOLD, false),
            line("Elrond", 23, ChatFormatting.GOLD, false),
            line("Elrond", 24, ChatFormatting.GOLD, false),
            line("Elrond", 25, ChatFormatting.GOLD, false));

    private static Line line(String who, int n, ChatFormatting colour, boolean italic) {
        return new Line(who, LINE + n, colour, italic);
    }

    private Departure() {
    }

    // ---- the summons ----

    /** Whether this player has Elrond's note and has not answered it: the page ahead waits on that. */
    public static boolean awaiting(ServerPlayer player) {
        return player.entityTags().contains(SUMMONS) && !player.entityTags().contains(SEEN);
    }

    /**
     * Leaves the note in the player's hand and says what it is, which is the page that ends chapter one
     * settling. Nothing is handed over that the line would otherwise hand: the next page is shut until the
     * courtyard has been walked to.
     */
    public static void summon(ServerPlayer player) {
        if (player.entityTags().contains(SUMMONS) || player.entityTags().contains(SEEN)) return;
        player.addTag(SUMMONS);
        ItemStack note = new ItemStack(TolkienItems.ITEM_LETTER.get());
        note.set(DataComponents.CUSTOM_NAME, Component.translatable(NOTE_NAME).withStyle(ChatFormatting.GOLD));
        note.set(DataComponents.LORE, new ItemLore(List.of(Component.translatable(NOTE_LORE)
                .withStyle(ChatFormatting.GRAY))));
        if (!player.getInventory().add(note)) player.drop(note, false);
        player.sendSystemMessage(Component.translatable(SUMMON_SAID).withStyle(ChatFormatting.GOLD));
    }

    /** Says the summons again, for a player who comes back with the note still unanswered. */
    public static void remind(ServerPlayer player) {
        if (awaiting(player)) player.sendSystemMessage(Component.translatable(SUMMON_SAID)
                .withStyle(ChatFormatting.GOLD));
    }

    // ---- the scene ----

    /** The state of one showing: where it is, when it opened, which bodies it stood and how far it has read. */
    private static final class Show {
        private final BlockPos at;
        private final String dim;
        private final long start;
        private final List<UUID> bodies = new ArrayList<>();
        /** Which seat of the cast a body took, so it is not confused with a body that failed to stand. */
        private final Map<UUID, Integer> seats = new HashMap<>();
        /** Where a leaver is walking to, by body: the far end of its lane off the courtyard. */
        private final Map<UUID, double[]> targets = new LinkedHashMap<>();
        /** The yaw a body was stood at, which is what its idle sway moves around. */
        private final Map<UUID, Float> facing = new HashMap<>();
        private final boolean rehearsal;
        private int sent;
        private long walkAt;

        private Show(BlockPos at, String dim, long start, boolean rehearsal) {
            this.at = at;
            this.dim = dim;
            this.start = start;
            this.rehearsal = rehearsal;
        }
    }

    /**
     * Runs the seam: starts the scene when an unfulfilled note is walked to the courtyard, reads the next
     * line of it, and ends it by opening chapter two.
     * <p>
     * Called from the campaign's own per-player tick, which runs once a second, so a line three seconds
     * apart reads on the tick it was meant to rather than a beat late.
     */
    public static void tick(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) return;
        Show show = SHOWS.get(player.getUUID());
        if (show != null) {
            advance(player, level, show);
            return;
        }
        if (!awaiting(player) || !player.isAlive()) return;
        BlockPos at = gathering(player, level);
        if (at == null) return;
        if (player.blockPosition().distToCenterSqr(at.getX() + 0.5D, at.getY(), at.getZ() + 0.5D)
                > GATHER_REACH * GATHER_REACH) {
            return;
        }
        start(player, level, at);
    }

    /** Where the company is waiting: the owner's mark if there is one, and Elrond's own stand if not. */
    @Nullable
    private static BlockPos gathering(ServerPlayer player, ServerLevel level) {
        BlockPos pinned = Pins.at(level, GATHER_PIN);
        if (pinned != null) return pinned;
        return Campaign.standOf(player, "Elrond");
    }

    private static void start(ServerPlayer player, ServerLevel level, BlockPos at) {
        Show show = new Show(at, level.dimension().identifier().toString(), level.getGameTime(), false);
        stand(level, show, at);
        SHOWS.put(player.getUUID(), show);
    }

    /**
     * Rehearses the scene where the caller stands, without touching the campaign: no note is answered and
     * no page is opened, so the ground can be looked at and the pacing heard before a marker is set. The
     * bodies, the offsets and the script are the real ones, which is what makes it worth watching.
     */
    public static void play(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) return;
        stop(player);
        // A marked plaza is what there is to look at, so a rehearsal shows it; ground nobody has
        // marked still rehearses at the caller's feet.
        BlockPos at = Pins.at(level, GATHER_PIN);
        if (at == null) at = player.blockPosition();
        Show show = new Show(at, level.dimension().identifier().toString(), level.getGameTime(), true);
        stand(level, show, at);
        SHOWS.put(player.getUUID(), show);
    }

    /** Stands the cast round a mark. The bodies belong to the show and go when it does. */
    private static void stand(ServerLevel level, Show show, BlockPos at) {
        // The middle of the courtyard, which is what everybody turns to: not the gathering mark, because
        // Elrond's own mark is that mark and he would be left facing out of his own scene.
        double centreX = 0.0D;
        double centreZ = 0.0D;
        int counted = 0;
        for (Cast cast : CAST) {
            BlockPos seat = Pins.at(level, bodyPin(cast.slug()));
            if (seat == null) continue;
            centreX += seat.getX();
            centreZ += seat.getZ();
            counted++;
        }
        if (counted == 0) {
            centreX = at.getX();
            centreZ = at.getZ();
        } else {
            centreX /= counted;
            centreZ /= counted;
        }
        for (int seat = 0; seat < CAST.size(); seat++) {
            Cast cast = CAST.get(seat);
            // A body whose ground the owner has marked stands on the mark itself: the block was set
            // down for that body, so the old pack's offset is only for ground nobody has marked.
            BlockPos marked = Pins.at(level, bodyPin(cast.slug()));
            BlockPos spot = marked != null ? marked
                    : level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    at.offset(cast.dx(), 0, cast.dz()));
            Entity entity = switch (cast.kind()) {
                case MAN -> TolkienEntities.ENTITY_TTM_HUMAN.get().create(level, EntitySpawnReason.EVENT);
                case HOBBIT -> TolkienEntities.ENTITY_TTM_HOBBIT.get().create(level, EntitySpawnReason.EVENT);
                case DWARF -> TolkienEntities.ENTITY_TTM_DWARF.get().create(level, EntitySpawnReason.EVENT);
            };
            if (!(entity instanceof Mob body)) continue;
            double lookX = centreX - spot.getX();
            double lookZ = centreZ - spot.getZ();
            float yaw = lookX * lookX + lookZ * lookZ < 0.25D ? 0.0F : yawTowards(lookX, lookZ);
            body.snapTo(spot.getX() + 0.5D, spot.getY(), spot.getZ() + 0.5D, yaw, 0.0F);
            body.setPersistenceRequired();
            // A body in a scene stands where it was put: no wandering, and nothing in the crowd can be
            // killed out from under the words being spoken over it.
            body.setNoAi(true);
            body.setInvulnerable(true);
            // The keeper tag is what makes a named human wear the face drawn for the name it carries.
            body.addTag(Sites.KEEPER_TAG);
            body.setCustomName(Component.literal(cast.name()).withStyle(ChatFormatting.GOLD));
            body.setCustomNameVisible(true);
            level.addFreshEntity(body);
            show.bodies.add(body.getUUID());
            show.seats.put(body.getUUID(), seat);
            show.facing.put(body.getUUID(), yaw);
            // The renderer turns a scene body's head and lifts its arm; what it needs to know is only that
            // this body is one of the scene's own.
            if (body instanceof TolkienVillagerEntity villager) villager.setSceneBody(true);
        }
    }

    /** The yaw that faces along a step, in the game's own reckoning: nought is south, ninety is west. */
    private static float yawTowards(double dx, double dz) {
        return (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
    }

    /** Whether this body is one of the nine who go: all of them but Elrond and Bilbo. */
    private static boolean leaves(Cast cast) {
        return !cast.slug().equals("elrond") && !cast.slug().equals("bilbo");
    }

    /** The pin a marked body stands on: the gathering mark's own key, with the body's slug after it. */
    public static String bodyPin(String slug) {
        return GATHER_PIN + "." + slug;
    }

    /**
     * Where the walk out begins, read off the plaza's own marks: the ring's open side, a few blocks past
     * the outermost body, on the bearing from Elrond through the Company. No mark of its own is needed -
     * the courtyard already says which way out is - and a body moved by a block or a number moves the
     * road's start with it.
     */
    @Nullable
    public static BlockPos exitStart(Level level) {
        BlockPos host = Pins.at(level, bodyPin("elrond"));
        if (host == null) host = Pins.at(level, GATHER_PIN);
        if (host == null) return null;
        double sumX = 0.0D;
        double sumZ = 0.0D;
        double far = 0.0D;
        int found = 0;
        for (Cast cast : CAST) {
            if (cast.slug().equals("elrond")) continue;
            BlockPos at = Pins.at(level, bodyPin(cast.slug()));
            if (at == null) continue;
            double dx = at.getX() - host.getX();
            double dz = at.getZ() - host.getZ();
            sumX += dx;
            sumZ += dz;
            far = Math.max(far, Math.sqrt(dx * dx + dz * dz));
            found++;
        }
        // Half a ring is enough to have a side, but a single body beside the host is not a bearing.
        if (found < 2) return null;
        double dx = sumX / found;
        double dz = sumZ / found;
        double length = Math.sqrt(dx * dx + dz * dz);
        if (length < 0.5D) return null;
        double out = Math.floor(far) + 3.0D;
        BlockPos column = new BlockPos(
                host.getX() + (int) Math.round(dx / length * out), host.getY(),
                host.getZ() + (int) Math.round(dz / length * out));
        return level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, column);
    }

    private static void advance(ServerPlayer player, ServerLevel level, Show show) {
        // A rehearsal is not a showing: the caller may fly off to look at the ground from above and the
        // words go on being said, since nothing is waiting on them.
        if (!show.rehearsal && (!level.dimension().identifier().toString().equals(show.dim)
                || player.blockPosition().distToCenterSqr(show.at.getX() + 0.5D, show.at.getY(),
                show.at.getZ() + 0.5D) > STAGE_REACH * STAGE_REACH)) {
            abort(player, level, show);
            return;
        }
        long elapsed = level.getGameTime() - show.start;
        while (show.sent < LINES.size() && elapsed >= (long) GAP * (show.sent + 1)) {
            Line line = LINES.get(show.sent++);
            Style style = Style.EMPTY.withColor(line.colour()).withItalic(line.italic());
            player.sendSystemMessage(Component.literal(line.who() + ": ").withStyle(line.colour())
                    .append(Component.translatable(line.key()).withStyle(style)));
        }
        // Nobody turns until the last line has been said: Elrond's five are spoken with the whole Company
        // still standing there, and only then does the courtyard empty.
        if (show.sent >= LINES.size()) beginWalk(level, show);
        if (show.sent < LINES.size()) return;
        if (elapsed < (long) GAP * LINES.size() + TAIL) return;
        // The walk out is the end of the scene rather than a thing happening beside it, so Elrond and Bilbo
        // are not taken away until the nine who go are off the plaza.
        if (!show.targets.isEmpty()) return;
        finish(player, level, show);
    }

    /**
     * Sets the nine who go walking: each is given a lane of its own, abreast across the bearing, from the
     * gathering mark out past the courtyard. Nothing moves here - the lanes are written down and stepped on
     * the ticks after - so a scene stopped mid-sentence never leaves a body halfway out of the plaza.
     */
    private static void beginWalk(ServerLevel level, Show show) {
        if (show.walkAt != 0L) return;
        BlockPos exit = exitStart(level);
        if (exit == null) return;
        BlockPos host = Pins.at(level, bodyPin("elrond"));
        if (host == null) host = Pins.at(level, GATHER_PIN);
        double bx = 0.0D;
        double bz = 1.0D;
        if (host != null) {
            double dx = exit.getX() - host.getX();
            double dz = exit.getZ() - host.getZ();
            double length = Math.sqrt(dx * dx + dz * dz);
            if (length > 0.5D) {
                bx = dx / length;
                bz = dz / length;
            }
        }
        int leavers = 0;
        for (Cast cast : CAST) {
            if (leaves(cast)) leavers++;
        }
        Map<Integer, UUID> bySeat = new HashMap<>();
        for (Map.Entry<UUID, Integer> row : show.seats.entrySet()) bySeat.put(row.getValue(), row.getKey());
        int lane = -1;
        for (int seat = 0; seat < CAST.size(); seat++) {
            Cast cast = CAST.get(seat);
            if (!leaves(cast)) continue;
            lane++;
            UUID id = bySeat.get(seat);
            if (id == null) continue;
            // Lanes run across the bearing, so the eight go abreast rather than in single file.
            double side = (lane - (leavers - 1) / 2.0D) * 1.1D;
            double tx = exit.getX() + 0.5D + bx * GONE - bz * side;
            double tz = exit.getZ() + 0.5D + bz * GONE + bx * side;
            BlockPos column = new BlockPos((int) Math.floor(tx), exit.getY(), (int) Math.floor(tz));
            int ty = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, column).getY();
            show.targets.put(id, new double[]{tx, ty, tz});
        }
        show.walkAt = level.getGameTime();
    }

    /**
     * Steps the leavers toward their lanes a tick at a time, and takes a body away when it arrives. The walk
     * is driven rather than navigated: a body standing in a scene has no AI worth asking, and nothing of its
     * own should be able to take it somewhere the courtyard did not say. Its ground is read again on every
     * step, so the rim's easing never leaves it walking in air.
     */
    /**
     * The bodies' own small movements: a slow sway so nobody stands like a signpost, and the flag that tells
     * a body its own line is being said.
     * <p>
     * The sway is made on the body's own yaw. The turn to the player and the arm that comes up are not: those
     * are bones, moved by the renderer from the flag set here, so the body holds the ground it was stood on
     * and only the head moves - which is what turning to look at somebody actually does.
     */
    public static void poseTick(ServerPlayer player) {
        Show show = SHOWS.get(player.getUUID());
        if (show == null || show.facing.isEmpty()) return;
        if (!(player.level() instanceof ServerLevel level)) return;
        long now = level.getGameTime();
        // A line is being said from the tick it lands until the next one does, so an arm comes down of its
        // own accord when the script moves on, and the last line's does not stay up through the walk out.
        Line speaking = show.sent > 0 && now - show.start < (long) GAP * show.sent
                ? LINES.get(show.sent - 1) : null;
        for (UUID id : List.copyOf(show.facing.keySet())) {
            // A body on its way out is steered by the walk, and a body already taken away has nothing to
            // steer.
            if (show.targets.containsKey(id)) continue;
            Entity body = level.getEntity(id);
            if (body == null) continue;
            float base = show.facing.get(id);
            Integer seat = show.seats.get(id);
            Cast cast = seat == null ? null : CAST.get(seat);
            if (body instanceof TolkienVillagerEntity villager) {
                // The one thing the scene says about a body's face: whether its line is the one being said.
                villager.setSpeaking(cast != null && speaking != null && speaking.who().equals(cast.name()));
            }
            // A slow breath on each body's own beat, so eleven of them are never in time.
            float yaw = base + (float) Math.sin(now * 0.05D + (seat == null ? 0 : seat) * 1.7D) * 4.0F;
            body.setYRot(yaw);
            body.setYBodyRot(yaw);
            body.setYHeadRot(yaw);
        }
    }

    public static void walkTick(ServerPlayer player) {
        Show show = SHOWS.get(player.getUUID());
        if (show == null || show.targets.isEmpty()) return;
        if (!(player.level() instanceof ServerLevel level)) return;
        for (UUID id : List.copyOf(show.targets.keySet())) {
            Entity body = level.getEntity(id);
            double[] to = show.targets.get(id);
            if (body == null || to == null) {
                show.targets.remove(id);
                continue;
            }
            double dx = to[0] - body.getX();
            double dz = to[2] - body.getZ();
            double gap = Math.sqrt(dx * dx + dz * dz);
            if (gap <= WALK_STEP || level.getGameTime() - show.walkAt > WALK_LIMIT) {
                body.discard();
                show.targets.remove(id);
                continue;
            }
            double nx = body.getX() + dx / gap * WALK_STEP;
            double nz = body.getZ() + dz / gap * WALK_STEP;
            int ny = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    new BlockPos((int) Math.floor(nx), (int) to[1], (int) Math.floor(nz))).getY();
            body.snapTo(nx, ny, nz, yawTowards(dx, dz), 0.0F);
        }
    }

    /** Takes the company away and opens chapter two: the line's next page falls into the book. */
    private static void finish(ServerPlayer player, ServerLevel level, Show show) {
        clear(level, show);
        SHOWS.remove(player.getUUID());
        // A rehearsal ends as a scene ends - the bodies go - and nothing else about the world moves.
        if (show.rehearsal) return;
        player.removeTag(SUMMONS);
        player.addTag(SEEN);
        Campaign.startChapterTwo(player);
    }

    /** Clears the bodies and leaves the note unanswered, so the player can walk back and watch again. */
    private static void abort(ServerPlayer player, ServerLevel level, Show show) {
        clear(level, show);
        SHOWS.remove(player.getUUID());
    }

    private static void clear(ServerLevel level, Show show) {
        for (UUID id : show.bodies) {
            Entity body = level.getEntity(id);
            if (body != null) body.discard();
        }
        show.bodies.clear();
        show.targets.clear();
    }

    /** Whether a show is running for this player, for the commands that poke at it. */
    public static boolean showing(ServerPlayer player) {
        return SHOWS.containsKey(player.getUUID());
    }

    /** Takes a running show down without opening chapter two, for the commands that poke at it. */
    public static void stop(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) return;
        Show show = SHOWS.remove(player.getUUID());
        if (show != null) clear(level, show);
    }

    /**
     * Answers the seam without watching it: the note is taken back, the having-seen is written down and
     * chapter two is opened. For a scene that will not start or cannot be finished - the escape hatch, and
     * the reason it can be an escape hatch rather than a repair.
     */
    public static void skip(ServerPlayer player) {
        stop(player);
        player.removeTag(SUMMONS);
        player.addTag(SEEN);
        Campaign.startChapterTwo(player);
    }

    /** Forgets the summons and the having-seen, so the seam can be walked from its start again. */
    public static void forget(ServerPlayer player) {
        stop(player);
        player.removeTag(SUMMONS);
        player.removeTag(SEEN);
    }
}
