package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.world.registration.ArdaMap;
import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * {@code /tmtp} - the quick map: one jump per country of the drawing.
 * <p>
 * Arda is forty-eight cells by thirty-six, half a million blocks across, and nothing in it is a
 * teleport yet: a region is reached by typing three numbers, which is what
 * {@code docs/arda-teleports.md} is a table of. This is that table as a command, so walking a region
 * does not start with copying coordinates out of a file.
 * <p>
 * The points are <b>computed, not typed</b>. A country's middle is {@link ArdaMap#anchor}, the same
 * middle the survey falls back on, and the height above it is read from the drawing with
 * {@link ArdaMap#sample} - so a jump follows the map when a fill moves, which the hand-written table
 * cannot. The three seas are stood over their water line rather than their bed, since the bed of a sea
 * is not a place to be put down in.
 * <p>
 * A country whose build is settled does not want its middle, it wants the build. Those go in
 * {@link #LOCKED}, one line each, and every jump afterwards lands on the build: the country's middle
 * is only the answer until something real stands somewhere.
 * <p>
 * A name is the other half of the command: {@code /tmtp rivendell} is the place, {@code /tmtp troll_cave}
 * is the build, and a country is what answers when nothing has taken the name. Both are read out of the
 * world rather than typed here - the circle a player is told about, and the pin a scout walked - so a
 * name moved by {@code /tmplace} is moved for the jump as well.
 * <p>
 * It answers to the same caller the other arrangement commands do - a gamemaster-level operator, or a
 * player in creative - because a jump across the map is not how the campaign is played.
 */
@EventBusSubscriber(modid = MODID)
public final class TeleportCommands {
    /** Gamemaster-level operators, and anyone building in creative. */
    private static final Predicate<CommandSourceStack> MAY_GO = source -> {
        ServerPlayer player = source.getPlayer();
        return (player != null && player.isCreative())
                || Commands.hasPermission(Commands.LEVEL_GAMEMASTERS).test(source);
    };

    /** How far over the ground the caller is set down, as the hand-written table in the docs does. */
    private static final int CLEARANCE = 8;

    /**
     * Regions whose ground has been settled, in blocks, replacing the middle of the country.
     * <p>
     * A name here is a build, or the cell a page surveys it from, and a jump lands on that point
     * exactly rather than on the middle of the cells the country was drawn in - which is what a pin
     * is for, so the numbers below are pins the scout walked. The key is the country's own name, read
     * through {@link #id}, so a region cannot be spelled two ways:
     * <pre>
     * id(ArdaMap.Country.BREE), new BlockPos(1024, 96, 1024),
     * </pre>
     */
    private static final Map<String, BlockPos> LOCKED = Map.of(
            // Thorenhad, where chapter one's cluster is spoken on: the block the scout stood on, so a
            // jump lands in the camp rather than two hundred blocks west of it. A locked point is used
            // as given - no clearance is added - so its y has to be a place a caller can be put down.
            id(ArdaMap.Country.TROLLSHAWS), new BlockPos(4691, 87, -3273)
    );

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("tmtp")
                .requires(MAY_GO)
                .executes(TeleportCommands::list)
                .then(Commands.literal("list").executes(TeleportCommands::list))
                .then(Commands.argument("target", StringArgumentType.string())
                        .suggests(TeleportCommands::suggest)
                        .executes(TeleportCommands::go)));
    }

    /** Every region name, which is the drawing's countries and nothing else. */
    private static List<String> regions() {
        List<String> names = new ArrayList<>();
        for (ArdaMap.Country country : ArdaMap.Country.values()) {
            names.add(id(country));
        }
        names.sort(String::compareTo);
        return names;
    }

    /** A country as it is typed: its own name, in the lower case a command word is written in. */
    private static String id(ArdaMap.Country country) {
        return country.name().toLowerCase(Locale.ROOT);
    }

    /** The country a name is, or null when it is not a country at all. */
    @Nullable
    private static ArdaMap.Country country(String name) {
        for (ArdaMap.Country each : ArdaMap.Country.values()) {
            if (id(each).equals(name)) return each;
        }
        return null;
    }

    /**
     * Every name that can be jumped to: the drawing's countries, then the ground that has a name of its
     * own - a place a world has written, and every pin, which is the ground a place is written on. A
     * build is named by the last word of its key, so {@code troll_cave} is
     * {@code tolkienmobs:key_structures/troll_cave}; the whole key is accepted too, and is simply not
     * worth a suggestion of its own.
     */
    private static List<String> names(@Nullable Level level) {
        Set<String> names = new TreeSet<>(regions());
        for (String key : pinKeys(level)) {
            names.add(tail(key));
        }
        if (level != null) {
            for (Places.Place place : Places.placed(level)) {
                names.add(place.id());
            }
        }
        return new ArrayList<>(names);
    }

    /** The Arda level, for a name that is read out of the world, or null when Arda is not loaded. */
    @Nullable
    private static ServerLevel ardaLevel(CommandSourceStack source) {
        return source.getServer().getLevel(TolkienDimensions.ARDA);
    }

    /**
     * The pin keys this world can answer with: its own first, since a world's choice is not overruled
     * by the build's, then the ones a pack ships.
     */
    private static List<String> pinKeys(@Nullable Level level) {
        List<String> keys = new ArrayList<>();
        if (level != null) {
            for (Pins.Pin pin : Pins.pinned(level)) {
                keys.add(pin.key());
            }
        }
        for (String key : Pins.sourceKeys()) {
            if (!keys.contains(key)) {
                keys.add(key);
            }
        }
        return keys;
    }

    /** A pin's last word: {@code tolkienmobs:key_structures/troll_cave} is {@code troll_cave}. */
    private static String tail(String key) {
        int cut = Math.max(key.lastIndexOf('/'), key.lastIndexOf(':'));
        return cut < 0 ? key : key.substring(cut + 1);
    }

    /**
     * The ground a name is written on, or null when nothing here has that name.
     * <p>
     * The world's own place comes first, because that is the circle a player is told about and the one
     * {@code /tmplace} moves; a pin is the same block before a world has named it, and for a build it is
     * the only name there is. A pin is stood on as given rather than as a country's middle is: it is a
     * block somebody stood on, not a column of the drawing, so no clearance is added to its height.
     */
    @Nullable
    private static Target named(ServerLevel arda, String name) {
        // A mark outranks both: it is the block somebody walked at the build's own door, where a caller asking for
        // that name wants to be put down - and a pin is the corner a template was planted from, which is a wall.
        BlockPos marked = Marks.at(name);
        if (marked != null) {
            return new Target(marked, "a mark walked on " + name);
        }
        Places.Place place = Places.place(arda, name);
        if (place != null && arda.dimension().equals(place.dimension())) {
            return new Target(place.centre(), "the place named " + name);
        }
        String key = pinKey(arda, name);
        if (key == null) return null;
        BlockPos pinned = Pins.at(arda, key);
        if (pinned == null) return null;
        return new Target(pinned, key.equals(name) ? "a set point" : "the pin " + key);
    }

    /**
     * The pin a name is written under. A whole key is matched before its last word, so a name that is
     * also the end of another key cannot be read as the wrong ground.
     */
    @Nullable
    private static String pinKey(ServerLevel arda, String name) {
        List<String> keys = pinKeys(arda);
        for (String key : keys) {
            if (key.equals(name)) return key;
        }
        for (String key : keys) {
            if (tail(key).equals(name)) return key;
        }
        return null;
    }

    /** A point a jump can land on, and how it is described back to the caller. */
    private record Target(BlockPos point, String what) {
    }

    private static CompletableFuture<Suggestions> suggest(CommandContext<CommandSourceStack> context,
                                                          SuggestionsBuilder builder) {
        for (String name : names(ardaLevel(context.getSource()))) {
            builder.suggest(name);
        }
        return builder.buildFuture();
    }

    /** Says what can be jumped to, since the names are the only part of this that has to be learned. */
    private static int list(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        List<String> regions = regions();
        source.sendSuccess(() -> Component.literal("Arda's " + regions.size()
                + " regions: " + String.join(", ", regions)), false);
        List<String> ground = new ArrayList<>(names(ardaLevel(source)));
        ground.removeAll(regions);
        if (!ground.isEmpty()) {
            source.sendSuccess(() -> Component.literal("Or the ground that has a name: "
                    + String.join(", ", ground)), false);
        }
        return regions.size() + ground.size();
    }

    /** Stands the caller in Arda, on the named region, place or build. */
    private static int go(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();
        String name = StringArgumentType.getString(context, "target").toLowerCase(Locale.ROOT);

        ServerLevel arda = ardaLevel(source);
        if (arda == null) {
            source.sendFailure(Component.literal("Arda is not loaded in this world."));
            return 0;
        }

        // The ground is read before the drawing is: a name is where something actually stands, and a
        // country is only the answer when no ground has taken the name.
        Target target = named(arda, name);
        if (target == null) {
            ArdaMap.Country country = country(name);
            if (country == null) {
                source.sendFailure(Component.literal("Nothing called \"" + name
                        + "\" - /tmtp list names them all."));
                return 0;
            }
            BlockPos locked = LOCKED.get(name);
            if (locked != null) {
                target = new Target(locked, "a set point");
            } else {
                // The anchor is a column of the drawing, not a point in the world: the ground under it is
                // the map's own height there, and a drawn sea's bed is under the water the caller wants to
                // look at, so the seas are stood over their water line instead.
                BlockPos anchor = ArdaMap.anchor(country);
                ArdaMap.Sample sample = ArdaMap.sample(anchor.getX(), anchor.getZ());
                int top = sample.height();
                if (!country.land()) {
                    top = Math.max(top, sample.water());
                }
                target = new Target(new BlockPos(anchor.getX(), top + CLEARANCE, anchor.getZ()),
                        "the middle of the country");
            }
        }

        BlockPos point = target.point();
        int y = Mth.clamp(point.getY(), arda.getMinY() + 1, arda.getMaxY() - 1);

        // A dimension change is a teleport with the level named: the same call the ledger uses to put a
        // player back, so a caller in the overworld arrives in Arda rather than being refused.
        player.teleportTo(arda, point.getX() + 0.5D, y, point.getZ() + 0.5D,
                Set.of(), player.getYRot(), player.getXRot(), false);

        BlockPos landed = new BlockPos(point.getX(), y, point.getZ());
        String where = target.what();
        source.sendSuccess(() -> Component.literal("Arda: " + name + " at " + landed.toShortString()
                + ", " + where), false);
        return 1;
    }
}
