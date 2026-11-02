package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.network.manager.WaypointManager;
import net.minecraft.core.BlockPos;
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
        String person = quest.person();
        if (person == null) return null;
        String place = Campaign.groundOf(person);
        if (place == null) return null;
        Places.Place where = Places.place(level, place);
        if (where == null || !where.dimension().equals(level.dimension())) return null;
        return new Mark("place.tolkienmobs." + place, where.centre(), level.dimension());
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

    /** Sends the player the direction of the first page they are carrying, or an empty mark for none. */
    public static void send(ServerPlayer player, List<Quest> row) {
        Quest carried = null;
        for (Quest quest : row) {
            // The campaign's pages are the only story in the book; an errand has no place to walk to.
            if (quest.story()) {
                carried = quest;
                break;
            }
        }
        // The stone the page is worth is planted where the page is taken rather than where it is
        // settled: a player walks out of the hall and up the road, and the stone that gets them back
        // has to be standing at the valley by the time they get there.
        if (carried != null) Stones.plant(player, carried);
        // Between pages the book is empty and the needle used to go blank, which was the one moment a
        // player most needed it: the step is finished, and the next one belongs to somebody else. The
        // line knows whose it is and where that body is kept, so the needle keeps its bearing.
        Mark mark = carried != null ? ground(player, carried) : onward(player);
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
