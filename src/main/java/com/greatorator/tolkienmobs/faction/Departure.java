package com.greatorator.tolkienmobs.faction;

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
import net.minecraft.world.level.levelgen.Heightmap;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
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
 * cast placed round one spot, and the lines spoken in order. What it does not carry yet is the exit: the
 * company speaks and then goes, rather than walking off, because the ground they walk to has not been
 * marked. The old script's walk mark and its spawn layout are in {@code docs/departure-scene.md}.
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
    private static final String GATHER_PIN = "rivendell.scene";
    /** How near the mark a player has to come for the scene to begin, in blocks. */
    private static final double GATHER_REACH = 24.0D;
    /** How far they may wander once it has begun before it lets them go, in blocks. */
    private static final double STAGE_REACH = 96.0D;
    /** Ticks between two lines: three seconds, which is slow enough to read and quick enough to hold. */
    private static final int GAP = 60;
    /** Ticks kept after the last line before the company is taken away and the page is handed over. */
    private static final int TAIL = 60;

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
    private record Cast(String name, Kind kind, int dx, int dz) {
    }

    /**
     * The ten who wait in the courtyard, placed as the old pack placed them: Elrond at the middle, the
     * others in the loose half-ring in front of him, the hobbits low and the tall folk behind. Elrond
     * himself is not spawned - he is already kept in the valley by the campaign - so his lines are spoken
     * without a body of this scene's own.
     */
    private static final List<Cast> CAST = List.of(
            new Cast("Gandalf", Kind.MAN, -2, 6),
            new Cast("Bilbo", Kind.HOBBIT, 0, 5),
            new Cast("Aragorn", Kind.MAN, 6, 6),
            new Cast("Gimli", Kind.DWARF, 7, 0),
            new Cast("Legolas", Kind.MAN, 8, 4),
            new Cast("Boromir", Kind.MAN, 8, 2),
            new Cast("Merry", Kind.HOBBIT, 5, 1),
            new Cast("Pippin", Kind.HOBBIT, 5, 4),
            new Cast("Samwise", Kind.HOBBIT, 5, 2),
            new Cast("Frodo", Kind.HOBBIT, 5, 3));

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
        private int sent;

        private Show(BlockPos at, String dim, long start) {
            this.at = at;
            this.dim = dim;
            this.start = start;
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
        Show show = new Show(at, level.dimension().identifier().toString(), level.getGameTime());
        float facing = 0.0F;
        for (Cast cast : CAST) {
            BlockPos spot = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    at.offset(cast.dx(), 0, cast.dz()));
            Entity entity = switch (cast.kind()) {
                case MAN -> TolkienEntities.ENTITY_TTM_HUMAN.get().create(level, EntitySpawnReason.EVENT);
                case HOBBIT -> TolkienEntities.ENTITY_TTM_HOBBIT.get().create(level, EntitySpawnReason.EVENT);
                case DWARF -> TolkienEntities.ENTITY_TTM_DWARF.get().create(level, EntitySpawnReason.EVENT);
            };
            if (!(entity instanceof Mob body)) continue;
            body.snapTo(spot.getX() + 0.5D, spot.getY(), spot.getZ() + 0.5D, facing, 0.0F);
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
        }
        SHOWS.put(player.getUUID(), show);
    }

    private static void advance(ServerPlayer player, ServerLevel level, Show show) {
        if (!level.dimension().identifier().toString().equals(show.dim)
                || player.blockPosition().distToCenterSqr(show.at.getX() + 0.5D, show.at.getY(),
                show.at.getZ() + 0.5D) > STAGE_REACH * STAGE_REACH) {
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
        if (show.sent < LINES.size()) return;
        if (elapsed < (long) GAP * LINES.size() + TAIL) return;
        finish(player, level, show);
    }

    /** Takes the company away and opens chapter two: the line's next page falls into the book. */
    private static void finish(ServerPlayer player, ServerLevel level, Show show) {
        clear(level, show);
        SHOWS.remove(player.getUUID());
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

    /** Forgets the summons and the having-seen, so the seam can be walked from its start again. */
    public static void forget(ServerPlayer player) {
        stop(player);
        player.removeTag(SUMMONS);
        player.removeTag(SEEN);
    }
}
