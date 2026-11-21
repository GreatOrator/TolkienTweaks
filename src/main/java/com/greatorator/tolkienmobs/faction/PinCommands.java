package com.greatorator.tolkienmobs.faction;

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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import org.jetbrains.annotations.Nullable;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * {@code /tmpin} - stand where a build belongs and say which build it is.
 * <p>
 * The alternative is editing {@link Pins#SOURCE} with coordinates copied out of the debug screen,
 * which is a transcription with a chance of error at every digit. This is the same act done where the
 * ground is in front of you: the caller's feet are the pin, the name says what it is, and the exact
 * line for the source table is printed back so the spot can be kept for every world by copying rather
 * than by retyping.
 * <p>
 * Two names, because there are two kinds of location and the same word can mean both - Weathertop is
 * a name announced on arrival and a hilltop template raised for the page. So {@code place} pins a
 * circle of named ground, {@code build} pins a template, and neither has to guess what was meant.
 * <p>
 * It answers to the same caller the other arrangement commands do - a gamemaster-level operator, or a
 * player in creative - and only inside Arda, since these are Arda's coordinates and the pin is read by
 * the campaign's own placement.
 */
@EventBusSubscriber(modid = MODID)
public final class PinCommands {
    /** Gamemaster-level operators, and anyone building in creative. */
    private static final Predicate<CommandSourceStack> MAY_PIN = source -> {
        ServerPlayer player = source.getPlayer();
        return (player != null && player.isCreative())
                || Commands.hasPermission(Commands.LEVEL_GAMEMASTERS).test(source);
    };

    /**
     * What a builder types for a building, and the template id the placement code asks with. The short
     * names are the ones a page uses in its own words - a camp, the ruin, a cave - so the command can
     * be used without a template path to hand.
     */
    private static final Map<String, String> BUILDINGS = new LinkedHashMap<>();

    static {
        BUILDINGS.put("camp", Sites.ELLADAN_CAMP);
        BUILDINGS.put("elledancamp", Sites.ELLADAN_CAMP);
        BUILDINGS.put("tent", Sites.WILD_TENT);
        BUILDINGS.put("ruin", Sites.WOOD_RUIN);
        BUILDINGS.put("cave", Sites.TROLL_CAVE);
        BUILDINGS.put("cave1", Sites.TROLL_CAVE);
        BUILDINGS.put("cave2", Sites.TROLL_CAVE_2);
        BUILDINGS.put("cave3", Sites.TROLL_CAVE_3);
        BUILDINGS.put("cave4", Sites.TROLL_CAVE_4);
        BUILDINGS.put("gloincamp", Sites.GLOIN_CAMP);
        BUILDINGS.put("sheltercave", Sites.SHELTER_CAMP);
        BUILDINGS.put("shelter", Sites.SHELTER_CAMP);
        // The keep is named, but it is not a building a hand pins: its corner is written down in ArdaMap and the world
        // stands it there, so /tmpin build refuses it rather than writing a pin at somebody's feet. "gundabad" used to
        // mean this template, which read as the gate; the gate is eight pieces and has a plan of its own.
        BUILDINGS.put("keep", Sites.GUNDABAD);
        BUILDINGS.put("gundabadkeep", Sites.GUNDABAD);
        BUILDINGS.put("weathertop", Sites.WEATHERTOP);
        BUILDINGS.put("castle", Sites.BOSS_CASTLE);
        BUILDINGS.put("arena", Sites.BOSS_ARENA);
        // The orc buildings, which are pieces to try against the ground rather than places a page sends anyone to.
        // The wild tent keeps "tent": this pack's tent is the quest's, and an orc one is asked for by its own name.
        BUILDINGS.put("ram", Sites.ORC_RAM);
        BUILDINGS.put("orcram", Sites.ORC_RAM);
        BUILDINGS.put("orctent", Sites.ORC_TENT);
        BUILDINGS.put("tower", Sites.ORC_TOWER);
        BUILDINGS.put("hut", Sites.ORC_HUT);
        BUILDINGS.put("farm", Sites.ORC_FARM);
        BUILDINGS.put("butcher", Sites.ORC_BUTCHER);
        BUILDINGS.put("hall", Sites.ORC_HALL);
        BUILDINGS.put("hall2", Sites.ORC_HALL_2);
        BUILDINGS.put("pit", Sites.ORC_LAVA_PIT);
        BUILDINGS.put("lavapit", Sites.ORC_LAVA_PIT);
        BUILDINGS.put("lava", Sites.ORC_LAVA_PIT);
    }

    /**
     * What a typed building name means, as the template it is: the catalogue first, then a full template id typed
     * as it is. This is the one table of building names in the pack - the command that pins a building and the one
     * that lays it both ask here, so a name added for one is a name the other knows.
     *
     * @return the template id, or null when the word is neither a name in the catalogue nor a template id
     */
    static String buildId(String typed) {
        String name = typed.toLowerCase(Locale.ROOT);
        String building = BUILDINGS.get(name);
        if (building != null) return building;
        return typed.indexOf(':') >= 0 ? typed : null;
    }

    /** The names that table carries, for a suggestion list. */
    static List<String> buildNames() {
        return new ArrayList<>(BUILDINGS.keySet());
    }

    private PinCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("tmpin")
                .requires(MAY_PIN)
                .executes(PinCommands::list)
                .then(Commands.literal("list").executes(PinCommands::list))
                .then(Commands.literal("place")
                        .then(Commands.argument("id", StringArgumentType.word())
                                .suggests(PinCommands::suggestPlaces)
                                .executes(PinCommands::pinPlace)))
                .then(Commands.literal("build")
                        .then(Commands.argument("build", StringArgumentType.string())
                                .suggests(PinCommands::suggestBuilds)
                                .executes(PinCommands::pinBuild)))
                .then(Commands.literal("hall")
                        .then(Commands.literal("add")
                                .then(Commands.argument("town", StringArgumentType.word())
                                        .suggests(PinCommands::suggestTowns)
                                        .executes(PinCommands::pinHall)))
                        .then(Commands.literal("remove")
                                .then(Commands.argument("town", StringArgumentType.word())
                                        .suggests(PinCommands::suggestTowns)
                                        .executes(PinCommands::unpinHall))))
                .then(Commands.literal("checklist").executes(PinCommands::checklist))
                .then(Commands.literal("remove")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .suggests(PinCommands::suggestAll)
                                .executes(PinCommands::unpin))));
    }

    // ---- pinning ----

    /** Pins a name from the catalogue at the caller's feet. */
    private static int pinPlace(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();
        String id = StringArgumentType.getString(context, "id").toLowerCase(Locale.ROOT);
        if (Places.known(id) == null) {
            source.sendFailure(Component.literal("No place called \"" + id + "\" - the catalogue has "
                    + Places.catalogue().size() + " names, and /tmpin place suggests them."));
            return 0;
        }
        return put(source, player, id);
    }

    /** Pins a template at the caller's feet, by its short name or by its own id. */
    private static int pinBuild(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();
        String typed = StringArgumentType.getString(context, "build");
        String key = resolve(typed);
        if (Sites.GUNDABAD.equals(key)) {
            source.sendFailure(Component.literal("The keep is not pinned by hand: its corner is written down in"
                    + " ArdaMap and the world stands it there, so a pin at your feet would move a hold that is planned."
                    + " /tmgate keep lays it again at the plan."));
            return 0;
        }
        if (key == null) {
            source.sendFailure(Component.literal("No building called \"" + typed + "\" - "
                    + String.join(", ", BUILDINGS.keySet()) + ", or a full template id."));
            return 0;
        }
        return put(source, player, key);
    }

    /**
     * Writes the caller's feet down as the pinned block, and hands back the source-table line so the
     * spot can be kept for every world without opening the file first.
     */
    private static int put(CommandSourceStack source, ServerPlayer player, String key) {
        ServerLevel level = pinnable(source, player);
        if (level == null) return 0;
        BlockPos at = player.blockPosition();
        BlockPos before = Pins.at(level, key);
        Pins.set(level, key, at);

        source.sendSuccess(() -> Component.literal((before == null ? "Pinned " : "Moved ") + key + " to "
                + at.toShortString() + (before == null ? "" : " (was " + before.toShortString() + ")")), false);
        source.sendSuccess(() -> Component.literal("For every world, add to Pins.SOURCE: "
                + line(key, at)), false);
        return 1;
    }

    /**
     * Pins a town's mustering hall where the caller stands.
     * <p>
     * The hall is its alliance's own build, so the only thing the caller chooses is the town - and choosing the town
     * chooses the hall. A town the catalogue gives no country keeps no hall: there would be nobody's hall to stand
     * there.
     */
    private static int pinHall(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();
        String town = StringArgumentType.getString(context, "town").toLowerCase(Locale.ROOT);
        if (!Places.isTown(town)) {
            source.sendFailure(Component.literal("\"" + town + "\" is not a town a board points at - the towns are "
                    + String.join(", ", Places.towns()) + "."));
            return 0;
        }
        String template = TownHalls.template(town);
        if (template == null) {
            source.sendFailure(Component.literal("The catalogue gives " + town + " no country, so it has no hall"
                    + " to be built: a hall is one of the three alliances' own."));
            return 0;
        }
        ServerLevel level = pinnable(source, player);
        if (level == null) return 0;
        BlockPos at = player.blockPosition();
        BlockPos before = Pins.at(level, TownHalls.key(town));
        Pins.set(level, TownHalls.key(town), at);

        // A town that is already finished gets its hall here and now; one that is not waits for its own pass, which
        // would lay over anything put in the footprint before it.
        int laid = TownHalls.raise(level, town);

        source.sendSuccess(() -> Component.literal((before == null ? "Pinned " : "Moved ") + town + "'s hall to "
                + at.toShortString() + (before == null ? "" : " (was " + before.toShortString() + ")")
                + ", built as " + template + "."), false);
        source.sendSuccess(() -> Component.literal(laid > 0
                ? "The hall is standing there: " + laid + " blocks of " + template + "."
                : TownHalls.waits(level, town)
                        ? "The hall waits: " + town + " is not finished, and its own pass would lay over the pin."
                        + " It goes up when the town does."
                        : "The hall is already standing at the pin, and is left as it is."), false);
        source.sendSuccess(() -> Component.literal("For every world, add to Pins.SOURCE: "
                + line(TownHalls.key(town), at)), false);
        return 1;
    }

    /** Takes a town's hall out of this world. */
    private static int unpinHall(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();
        String town = StringArgumentType.getString(context, "town").toLowerCase(Locale.ROOT);
        if (!Places.isTown(town)) {
            source.sendFailure(Component.literal("\"" + town + "\" is not a town a board points at - the towns are "
                    + String.join(", ", Places.towns()) + "."));
            return 0;
        }
        if (!(player.level() instanceof ServerLevel level)) return 0;
        if (!Pins.remove(level, TownHalls.key(town))) {
            source.sendFailure(Component.literal("This world has no pin for " + town + "'s hall. A source pin is in"
                    + " the build, not in the world."));
            return 0;
        }
        // The record of which way the hall was laid belongs to the pin: a hall pinned again is a hall still to be laid.
        TownHalls.forget(level, town);
        source.sendSuccess(() -> Component.literal("Forgot this world's pin for " + town + "'s hall."), false);
        return 1;
    }

    /** The level a pin may be written in, or null with the reason already sent to the caller. */
    @Nullable
    private static ServerLevel pinnable(CommandSourceStack source, ServerPlayer player) {
        if (!TolkienDimensions.ARDA.equals(player.level().dimension())) {
            source.sendFailure(Component.literal("Ground is pinned in Arda - stand where the build goes first."));
            return null;
        }
        if (!(player.level() instanceof ServerLevel level)) {
            source.sendFailure(Component.literal("This level cannot hold a pin."));
            return null;
        }
        return level;
    }

    /** Takes a pin out of this world, by either kind of name. */
    private static int unpin(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();
        String typed = StringArgumentType.getString(context, "name");
        String key = resolve(typed);
        if (key == null) {
            source.sendFailure(Component.literal("No place or building called \"" + typed + "\"."));
            return 0;
        }
        if (!(player.level() instanceof ServerLevel level)) return 0;
        if (!Pins.remove(level, key)) {
            source.sendFailure(Component.literal("This world has no pin for " + key
                    + ". A source pin is in the build, not in the world."));
            return 0;
        }
        source.sendSuccess(() -> Component.literal("Forgot this world's pin for " + key + "."), false);
        return 1;
    }

    /** Says what this world has pinned, and what the build pins for every world. */
    private static int list(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();
        if (!(player.level() instanceof ServerLevel level)) return 0;
        List<Pins.Pin> pins = Pins.pinned(level);
        if (pins.isEmpty() && Pins.sourced() == 0) {
            source.sendSuccess(() -> Component.literal("Nothing is pinned by hand - every location is "
                    + "surveyed from the drawing. Stand where something belongs and use /tmpin place "
                    + "<name> or /tmpin build <name>."), false);
            return 0;
        }
        for (Pins.Pin pin : pins) {
            source.sendSuccess(() -> Component.literal("  " + pin.key() + " at " + pin.at().toShortString()
                    + ", this world"), false);
        }
        for (String key : Pins.sourceKeys()) {
            source.sendSuccess(() -> Component.literal("  " + key + ", every world"), false);
        }
        return pins.size();
    }

    /**
     * The ground the two chapters ask for and what this world has pinned of it, which is the whole
     * scouting job in one answer: a location not on this list is not ground a page names, and a
     * location marked not pinned is one the drawing is still guessing at.
     */
    private static int checklist(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();
        if (!(player.level() instanceof ServerLevel level)) return 0;
        List<Pins.Need> needs = Pins.needed();
        int done = 0;
        for (Pins.Need need : needs) {
            if (Pins.state(level, need.key()) != Pins.State.NONE) done++;
        }
        int pinned = done;
        source.sendSuccess(() -> Component.literal("Quest ground: " + pinned + " of " + needs.size()
                + " pinned"), false);
        for (Pins.Need need : needs) {
            String mark = switch (Pins.state(level, need.key())) {
                case WORLD -> "this world";
                case SOURCE -> "every world";
                case NONE -> "not pinned";
            };
            source.sendSuccess(() -> Component.literal("  " + pad(need.key(), 36) + pad(mark, 14)
                    + need.what()), false);
        }
        if (pinned < needs.size()) {
            source.sendSuccess(() -> Component.literal("Stand where one belongs and use /tmpin place <id> "
                    + "or /tmpin build <name>."), false);
        }
        return pinned;
    }

    /** Pads a column so the checklist lines up when it is read in a chat window. */
    private static String pad(String text, int width) {
        return text.length() >= width ? text + " " : text + " ".repeat(width - text.length());
    }

    // ---- names ----

    /**
     * The key a typed name means: a building's short name first, then a catalogue place, then a full
     * template id. A word that is both - Weathertop, Gundabad, Glóin's camp - is a building here,
     * because the short names list is the smaller and more specific of the two, and the place branch
     * takes it by its own id when that is what was meant.
     */
    private static String resolve(String typed) {
        String name = typed.toLowerCase(Locale.ROOT);
        String building = BUILDINGS.get(name);
        if (building != null) return building;
        if (Places.known(name) != null) return name;
        return typed.indexOf(':') >= 0 ? typed : null;
    }

    /** The line a source table would carry for this pin. */
    private static String line(String key, BlockPos at) {
        return "Map.entry(\"" + key + "\", new BlockPos(" + at.getX() + ", " + at.getY() + ", " + at.getZ() + ")),";
    }

    // ---- suggestions ----

    private static CompletableFuture<Suggestions> suggestPlaces(CommandContext<CommandSourceStack> context,
                                                                SuggestionsBuilder builder) {
        for (Places.Name name : Places.catalogue()) {
            builder.suggest(name.id());
        }
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestBuilds(CommandContext<CommandSourceStack> context,
                                                               SuggestionsBuilder builder) {
        for (String name : BUILDINGS.keySet()) {
            builder.suggest(name);
        }
        return builder.buildFuture();
    }

    /** The towns a board points at, which are the only things a hall can be pinned for. */
    private static CompletableFuture<Suggestions> suggestTowns(CommandContext<CommandSourceStack> context,
                                                               SuggestionsBuilder builder) {
        for (String town : Places.towns()) {
            builder.suggest(town);
        }
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestAll(CommandContext<CommandSourceStack> context,
                                                             SuggestionsBuilder builder) {
        List<String> names = new ArrayList<>(BUILDINGS.keySet());
        for (Places.Name name : Places.catalogue()) {
            if (!names.contains(name.id())) names.add(name.id());
        }
        for (String name : names) {
            builder.suggest(name);
        }
        return builder.buildFuture();
    }
}
