package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.network.manager.WaypointManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Where the page in a player's hand is sending them, as a point the client can point at.
 * <p>
 * A player who is told to go and speak to Elrond in Rivendell has been told nothing they can act on: the
 * valley is a walk of thousands of blocks across country the map draws and the game does not signpost.
 * The book says what a page wants and who it wants it of; this is the other half of that sentence - the
 * direction - kept to a bearing rather than an errand the player could follow with their eyes shut.
 * <p>
 * The point itself is resolved on the server, where a place's ground is written down and a site's is
 * chosen, and sent as a position and a name. The client is given no way to ask for anybody else's, and
 * the needle is a hint: nothing in the game reads it, no page checks it, and a player who walks the
 * other way finds the same country in the end.
 */
public final class Waypoint {
    /**
     * One destination: the name to read and the ground to point at. The name is a translation key where
     * it has one and the words themselves where it does not, which is the same rule the book uses for a
     * giver's name - a place is translated, a body's name is not.
     */
    public record Mark(String label, BlockPos pos, ResourceKey<Level> dimension) {
    }

    /** What the client was last told, or null when the page in hand names no ground at all. */
    @Nullable
    private static Mark client;

    private Waypoint() {
    }

    /**
     * The ground a page sends a player to, or null when the page names none: a hunt is wherever the mob
     * is, a fetch is wherever the goods are, and neither is a place a needle can honestly point at.
     */
    @Nullable
    public static Mark ground(ServerPlayer player, Quest quest) {
        if (!(player.level() instanceof ServerLevel level)) return null;
        // Ground the campaign raises is chosen when the page is taken, so the page itself does not know
        // where it is - the site record does, and it is the same ground the walk will be measured against.
        if (quest.isSite()) {
            Sites.Site site = Sites.siteOf(player);
            if (site == null || !site.dimension().equals(level.dimension())) return null;
            return new Mark("quest.tolkienmobs.site", site.centre(), level.dimension());
        }
        // A page of the stone that asks for caves names no ground at all, and the crag it is asked in is
        // not the page's either: the campaign pitches it when the page is taken, so it is a site and not a
        // place. Read as neither, this was the one page with no needle in the book - a search of the dark,
        // with three hills already cut for it and no direction to any of them.
        if (quest.kind() == Quest.Kind.SURVEY) {
            Sites.Site cave = Sites.siteOf(player);
            if (cave != null && Sites.isCave(cave.structure()) && cave.dimension().equals(level.dimension())) {
                // The keeper is what the page is waiting on, so once the crag is cut the needle follows
                // him into the chamber: the middle of the hill is where the player walks in, and an arrow
                // to the ground under their own feet is not a direction.
                BlockPos keeper = caveKeeper(level, cave.centre());
                if (keeper != null) return new Mark("quest.tolkienmobs.cave", keeper, level.dimension());
                // Standing in a crag whose chamber is empty is the one place the needle has nothing to
                // add: the page is a kill at depth, not a walk, and a bearing to a point two blocks away
                // reads as a broken compass rather than as an errand. It comes back with the next asking.
                if (cave.centre().distToCenterSqr(player.position()) < CAVE_INSIDE * CAVE_INSIDE) return null;
                return new Mark("quest.tolkienmobs.cave", cave.centre(), level.dimension());
            }
        }
        // A page of the stone names ground as plainly as any other page, and its spot is null because
        // the page is not settled by arriving: it is settled by holding the beryl over a place. Read as
        // nothing but a spot, the ford page was a page with no direction at all.
        String named = quest.namedGround();
        if (!named.isEmpty()) {
            Places.Place place = Places.place(level, named);
            if (place == null || !place.dimension().equals(level.dimension())) return null;
            return new Mark("place.tolkienmobs." + named, place.centre(), level.dimension());
        }

        // A body the campaign stands on ground it pitched itself - the camp at the pass, the hold in the
        // mountains - is named by a page that is a conversation rather than a walk, and nothing above has
        // looked at the site for it, since the site branch is for pages whose target is a site. Read as a
        // place only, Glóin's page had no needle at all: his camp is not a place the map already has, it
        // is ground the campaign raises when the page is handed over. So the ground a page's body stands
        // on is asked for before the place the map would name.
        Sites.Site pitched = Sites.siteOf(player);
        if (pitched != null && pitched.dimension().equals(level.dimension())
                && pitchesFor(quest.person(), pitched.structure())) {
            return new Mark("quest.tolkienmobs.site", pitched.centre(), level.dimension());
        }
        Quest.Spot spot = quest.spot();
        if (spot != null) {
            if (spot.place() == null) return new Mark("hud.tolkienmobs.road.mark", spot.centre(), level.dimension());
            Places.Place place = Places.place(level, spot.place());
            if (place == null || !place.dimension().equals(level.dimension())) return null;
            return new Mark("place.tolkienmobs." + spot.place(), place.centre(), level.dimension());
        }
        // A page that names a body points at the ground the line keeps that body on. The body itself
        // would be better and is not something a server can promise: it is only standing while the page
        // is the one it belongs to, and a needle that vanished when a keeper unloaded would be worse
        // than one that points at the town he lives in.
        // A page whose quarry is a named body rather than a kind of mob: the campaign stands that one
        // creature itself, so the ground behind the page is the lair it was pitched in, not a mob that
        // may be found anywhere. Read as nothing but a name, this was another page with no needle.
        Mark quarry = Campaign.quarryMark(player, quest);
        if (quarry != null) return quarry;
        String person = quest.person();
        if (person == null) return null;
        String place = Campaign.groundOf(person);
        if (place == null) return null;
        Places.Place where = Places.place(level, place);
        if (where == null || !where.dimension().equals(level.dimension())) return null;
        // The body, not the middle of the town: a place's centre is where the place is, and the body a
        // page names is usually standing a step off it - Glorfindel's step is on the square's south road,
        // while the middle of that square is a fountain. Pointed at the middle, the needle led into water.
        BlockPos stand = Campaign.standOf(player, person);
        return new Mark("place.tolkienmobs." + place,
                stand == null ? where.centre() : stand, level.dimension());
    }

    /**
     * The step after the one just settled, pointed at through the ground the next speaker is kept on.
     * Null when the line has run out, or when the next page is held by a body with no town of its own.
     */
    @Nullable
    private static Mark onward(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) return null;
        Campaign.Next next = Campaign.nextStep(player, Alliance.WEST);
        if (next == null || next.place() == null) return null;
        Places.Place where = Places.place(level, next.place());
        if (where == null || !where.dimension().equals(level.dimension())) return null;
        return new Mark("place.tolkienmobs." + next.place(), where.centre(), level.dimension());
    }

    /**
     * Whether the ground the campaign has pitched is the ground a page's body is standing on: Glóin at the
     * camp at the east end of the pass, Gimli in the hold. Matched on the names the line writes, because a
     * page names a body in words rather than by tag, and the map has no entry for either of them.
     */
    private static boolean pitchesFor(@Nullable String person, String structure) {
        if (person == null) return false;
        String said = person.toLowerCase(java.util.Locale.ROOT);
        if (said.contains("glóin") || said.contains("gloin")) return structure.equals(Sites.GLOIN_CAMP);
        if (said.contains("gimli")) return structure.equals(Sites.GUNDABAD);
        return false;
    }

    /** How near a crag's middle a player counts as being inside it, in blocks. */
    private static final int CAVE_INSIDE = 24;

    /** The crag's keeper, wherever he stands in it, or null while the chamber is empty. */
    @Nullable
    private static BlockPos caveKeeper(ServerLevel level, BlockPos centre) {
        for (Entity found : level.getEntities((Entity) null,
                new net.minecraft.world.phys.AABB(centre).inflate(64.0D),
                e -> e.entityTags().contains(Campaign.CAVE_TROLL_TAG))) {
            return found.blockPosition();
        }
        return null;
    }

    /** What each player's needle is showing, so ground that appears later is still sent. */
    private static final java.util.Map<java.util.UUID, String> SENT = new java.util.HashMap<>();

    /** Pages whose bearing has already been said to a player, so the sentence is only read once. */
    private static final java.util.Set<String> TOLD = new java.util.HashSet<>();

    /**
     * Says how far off a page's ground is and which way, the first time the page is carried.
     * <p>
     * A bearing on a HUD is a line to walk and nothing more: it does not say that the water is two days
     * off, and a player told "hold the stone aloft at the Ford" is told a place and no distance at all.
     * So the sentence is said once beside the needle, in the same tick, and only for ground the world
     * has already written - a page whose ground is still being placed will say it when it exists.
     */
    private static void sayBearing(ServerPlayer player, Quest quest) {
        Mark mark = ground(player, quest);
        if (mark == null || !(player.level() instanceof ServerLevel level)) return;
        // Only the marks that are a place and not a phrase: a site page already has its own sentence,
        // and "somewhere out past the ridge lies four hundred blocks north" is not one of them.
        if (!mark.label().startsWith("place.tolkienmobs.")
                && !mark.label().equals("quest.tolkienmobs.cave")
                && !mark.label().equals("quest.tolkienmobs.lair")
                && !mark.label().equals("hud.tolkienmobs.road.mark")) return;
        if (!mark.dimension().equals(level.dimension())) return;
        BlockPos from = player.blockPosition();
        int away = (int) Math.sqrt(from.distSqr(mark.pos()));
        if (away < 64) return;
        if (!TOLD.add(player.getUUID() + "|" + mark.label() + "|" + mark.pos().toShortString())) return;
        player.sendSystemMessage(net.minecraft.network.chat.Component.translatable(
                "campaign.tolkienmobs.bearing",
                net.minecraft.network.chat.Component.translatable(mark.label()),
                net.minecraft.network.chat.Component.literal(String.valueOf(away)),
                net.minecraft.network.chat.Component.translatable(
                        "campaign.tolkienmobs.bearing." + compass(from, mark.pos())))
                .withStyle(net.minecraft.ChatFormatting.GRAY));
    }

    /**
     * Which of the eight bearings a point lies on, as the key of its word. North is -Z, which is how the
     * world is laid out and how a map is drawn, so a player reading "north" and pressing F3 agrees with it.
     */
    private static String compass(BlockPos from, BlockPos to) {
        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();
        int sector = Math.floorMod((int) Math.round(Math.toDegrees(Math.atan2(dx, -dz)) / 45.0D), 8);
        return switch (sector) {
            case 0 -> "n";
            case 1 -> "ne";
            case 2 -> "e";
            case 3 -> "se";
            case 4 -> "s";
            case 5 -> "sw";
            case 6 -> "w";
            default -> "nw";
        };
    }

    /** Sends the player the direction of the first page they are carrying, or an empty mark for none. */
    public static void send(ServerPlayer player, List<Quest> row) {
        Quest carried = carriedPage(row);
        // The stone the page is worth is planted where the page is taken rather than where it is
        // settled: a player walks out of the hall and up the road, and the stone that gets them back
        // has to be standing at the valley by the time they get there.
        if (carried != null) Stones.plant(player, carried);
        Mark mark = carried != null ? ground(player, carried) : onward(player);
        SENT.put(player.getUUID(), keyOf(mark));
        if (carried != null) sayBearing(player, carried);
        sendMark(player, mark);
    }

    /**
     * Sends the needle again whenever the ground behind the carried page has changed since the last look.
     * <p>
     * The needle used to be worked out at the three moments a page moves - taken, handed in, and read -
     * and never again, which is a promise the world cannot keep: a crag is pitched on the road <em>after</em>
     * the cave page is in hand, a body walks to his stand, and a place is named the first time somebody
     * carries a page that names it. Worked out once, each of those arrived too late and left the book with
     * a page that says where to go and no direction on the screen. Worked out every second and sent only
     * when it differs, the needle cannot be out of date for longer than a second.
     */
    public static void keepCurrent(ServerPlayer player) {
        List<Quest> row = QuestLedger.active(player);
        Quest carried = carriedPage(row);
        Mark mark = carried != null ? ground(player, carried) : onward(player);
        if (keyOf(mark).equals(SENT.get(player.getUUID()))) return;
        SENT.put(player.getUUID(), keyOf(mark));
        if (carried != null) sayBearing(player, carried);
        sendMark(player, mark);
    }

    /** The first page in the book that belongs to the campaign, which is the only kind with a road. */
    @Nullable
    private static Quest carriedPage(List<Quest> row) {
        for (Quest quest : row) {
            // The campaign's pages are the only story in the book; an errand has no place to walk to.
            if (quest.story()) return quest;
        }
        return null;
    }

    /** What a mark looks like written down, so the same ground is not sent twice. */
    private static String keyOf(@Nullable Mark mark) {
        return mark == null ? "-"
                : mark.label() + "@" + mark.pos().asLong() + "#" + mark.dimension().identifier();
    }

    private static void sendMark(ServerPlayer player, @Nullable Mark mark) {
        PacketDistributor.sendToPlayer(player, mark == null
                ? new WaypointManager("", BlockPos.ZERO, "", false)
                : new WaypointManager(mark.label(), mark.pos(), mark.dimension().identifier().toString(), true));
    }

    public static void acceptClient(WaypointManager payload) {
        client = payload.present()
                ? new Mark(payload.label(), payload.pos(),
                        ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION,
                                net.minecraft.resources.Identifier.parse(payload.dimension())))
                : null;
    }

    /** What the HUD draws from, or null when there is nothing to walk toward. */
    @Nullable
    public static Mark client() {
        return client;
    }
}
