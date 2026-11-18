package com.greatorator.tolkienmobs.faction;

import com.mojang.serialization.Codec;
import com.greatorator.tolkienmobs.TolkienMobsMain;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The roads that were planned in this world, kept so a planned road outlives the session that planned it.
 * <p>
 * A planned road is made at runtime: the hand names two blocks, {@link RoadPlan} works the way out between them, and
 * the sections are added to {@link Roads#ROUTES} for the run. That was all a road needed while it was being laid, and
 * nothing at all the next morning - after a restart the list was empty again, so a player standing on a road they had
 * laid themselves could not find it, could not forget it and could not have it re-laid, and every road in the world
 * had to be planned again from memory.
 * <p>
 * What is kept is what the plan decided, not the road that was laid: a section is its own name, its kind, whether the
 * bands decide its width, its two ends, and the marks it is bent through - the whole of what {@link RoadPlan} says, and
 * the same numbers a route named in the code carries. A crossing keeps its banks, its deck and its pieces. So a road
 * read back is the road that was planned rather than a guess at it, and the pass that lays it, the water that answers
 * for its span and the hand that forgets it all work as they did before the world stopped.
 * <p>
 * The store is a sibling of {@link RoadCommands.Progress} rather than part of it, because the two are answers about
 * different things: progress is how far a pass has got and is thrown away when the road is forgotten, where a plan is
 * the road itself and is kept until somebody says otherwise. Forgetting a road clears its progress and leaves its plan,
 * so the next pass walks the same way it was planned to.
 */
final class RoadPlans extends SavedData {
    private static final String NAME = "road_plans";
    /** Every planned section, by its own id, in the order the plans were made. */
    private static final Map<String, CompoundTag> SECTIONS = new HashMap<>();

    static final Codec<RoadPlans> CODEC = CompoundTag.CODEC.xmap(RoadPlans::read, RoadPlans::write);

    static final SavedDataType<RoadPlans> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(TolkienMobsMain.MODID, NAME), RoadPlans::new, CODEC);

    /** A world with no plans file has no planned roads, and forgets whatever the last world planned. */
    private RoadPlans() {
        this(true);
    }

    private RoadPlans(boolean fresh) {
        super();
        if (fresh) SECTIONS.clear();
    }

    private static RoadPlans read(CompoundTag tag) {
        SECTIONS.clear();
        for (String id : tag.keySet()) SECTIONS.put(id, tag.getCompoundOrEmpty(id));
        return new RoadPlans(false);
    }

    private CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        SECTIONS.forEach(tag::put);
        return tag;
    }

    /**
     * Writes down the sections a plan came to, so the next start of this world finds the road again.
     * <p>
     * Kept per section rather than per road: a road that crosses water is several routes, and each of them is a road
     * in its own right - it has its own note, its own pass and its own name in the log. One road planned again with a
     * different crossing leaves the section beside the old one, which is a road the world has twice; that is the same
     * bargain the walked network made with its routes, and the section a hand no longer wants is forgotten on its own.
     */
    static void keep(ServerLevel level, List<Route> sections, Roads.Kind kind, boolean town) {
        for (Route section : sections) SECTIONS.put(section.id(), tag(section, kind, town));
        instance(level).setDirty();
    }

    /**
     * Puts every planned road this world has back in the tables the pass reads, and says how many it took.
     * <p>
     * The world may be a different one from the last: a single-player save swapped for another keeps the class's
     * static tables, so whatever the previous world planned is taken back out first. A road named in the code is not
     * one of these and is never touched.
     */
    static int load(ServerLevel level) {
        instance(level);
        Roads.clearPlanned();
        int restored = 0;
        for (CompoundTag tag : new ArrayList<>(SECTIONS.values())) {
            Route route = route(tag);
            if (route == null) continue;
            RoadPlan.restore(route, kind(tag.getStringOr("kind", Roads.Kind.WILD_WIDE.name())),
                    tag.getBooleanOr("town", false), bends(tag));
            Roads.remember(route);
            restored++;
        }
        return restored;
    }

    /** One section as the plan describes it. */
    private static CompoundTag tag(Route section, Roads.Kind kind, boolean town) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", section.id());
        tag.putString("kind", kind.name());
        tag.putBoolean("town", town);
        tag.putInt("cityX0", section.cityX0());
        tag.putInt("cityX1", section.cityX1());
        tag.putInt("cityZ0", section.cityZ0());
        tag.putInt("cityZ1", section.cityZ1());
        tag.putInt("pathX", section.pathX());
        tag.putInt("pathZ", section.pathZ());
        if (section instanceof Route.Crossing span) {
            tag.putString("type", "span");
            tag.putInt("bankX", span.bankX());
            tag.putInt("bankZ", span.bankZ());
            tag.putInt("farX", span.farX());
            tag.putInt("farZ", span.farZ());
            tag.putInt("half", span.half());
            tag.putInt("bridgeBase", span.bridgeBase());
            tag.putInt("endBase", span.endBase());
            tag.putInt("deckY", span.deckY());
            tag.putInt("runIn", span.runIn());
            tag.putInt("exitReach", span.exitReach());
            tag.putString("endPiece", span.endPiece());
            tag.putString("middlePiece", span.middlePiece());
        } else if (section instanceof Route.Riverless dry) {
            tag.putString("type", "dry");
            tag.putInt("farX", dry.farX());
            tag.putInt("farZ", dry.farZ());
        }
        List<BlockPos> marks = RoadPlan.bends(section.id());
        int[] flat = new int[marks.size() * 3];
        for (int i = 0; i < marks.size(); i++) {
            BlockPos mark = marks.get(i);
            flat[i * 3] = mark.getX();
            flat[i * 3 + 1] = mark.getY();
            flat[i * 3 + 2] = mark.getZ();
        }
        tag.putIntArray("bends", flat);
        return tag;
    }

    /** The section a tag describes, or null when the tag is not one this wrote. */
    private static Route route(CompoundTag tag) {
        String id = tag.getStringOr("id", "");
        if (id.isEmpty()) return null;
        int x0 = tag.getIntOr("cityX0", 0);
        int x1 = tag.getIntOr("cityX1", 0);
        int z0 = tag.getIntOr("cityZ0", 0);
        int z1 = tag.getIntOr("cityZ1", 0);
        int pathX = tag.getIntOr("pathX", 0);
        int pathZ = tag.getIntOr("pathZ", 0);
        try {
            if ("span".equals(tag.getStringOr("type", "dry"))) {
                return new Route.Crossing(id, x0, x1, z0, z1, pathX, pathZ,
                        tag.getIntOr("bankX", 0), tag.getIntOr("bankZ", 0),
                        tag.getIntOr("farX", 0), tag.getIntOr("farZ", 0),
                        tag.getIntOr("half", 4), tag.getIntOr("bridgeBase", 0), tag.getIntOr("endBase", 0),
                        tag.getIntOr("deckY", 0), tag.getIntOr("runIn", 8), tag.getIntOr("exitReach", 16),
                        tag.getStringOr("endPiece", "bridge_end"), tag.getStringOr("middlePiece", "bridge_middle"));
            }
            return new Route.Riverless(id, x0, x1, z0, z1, pathX, pathZ,
                    tag.getIntOr("farX", 0), tag.getIntOr("farZ", 0));
        } catch (IllegalArgumentException impossible) {
            // A span whose banks are not on one axis is not a road this wrote. Nothing is laid on a guess.
            return null;
        }
    }

    /** The marks a section is bent through, as the plan left them. */
    private static List<BlockPos> bends(CompoundTag tag) {
        int[] flat = tag.getIntArray("bends").orElse(new int[0]);
        List<BlockPos> marks = new ArrayList<>(flat.length / 3);
        for (int i = 0; i + 2 < flat.length; i += 3) {
            marks.add(new BlockPos(flat[i], flat[i + 1], flat[i + 2]));
        }
        return marks;
    }

    /** The kind a section was planned at, or the country's own track when the name is one this version has lost. */
    private static Roads.Kind kind(String named) {
        try {
            return Roads.Kind.valueOf(named);
        } catch (IllegalArgumentException unknown) {
            return Roads.Kind.WILD_WIDE;
        }
    }

    private static RoadPlans instance(ServerLevel level) {
        ServerLevel overworld = level.getServer() == null ? null : level.getServer().getLevel(Level.OVERWORLD);
        if (overworld == null) return new RoadPlans(false);
        return overworld.getDataStorage().computeIfAbsent(TYPE);
    }
}
