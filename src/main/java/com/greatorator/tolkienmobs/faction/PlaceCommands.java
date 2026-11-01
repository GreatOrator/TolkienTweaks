package com.greatorator.tolkienmobs.faction;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The commands that name ground.
 * <p>
 * A place's name is the campaign's and its coordinates are the world's - the generator puts hills
 * where it likes, so no two saves put a barrow in the same gap, and a name without a spot is a name
 * nobody can walk into. This is where an operator puts the two together: stand where the place
 * belongs, say what it is, and every player who walks in afterwards is told once.
 * <p>
 * One root, {@code /tmplace}, with five hands: {@code add <id> [radius]}, {@code remove <id>},
 * {@code list}, {@code where}, and {@code forget <targets>}. {@code forget} is the one a builder
 * needs and a player never does - a banner is sent once in a world's life, so seeing it again means
 * being allowed to forget it.
 * <p>
 * It answers to the same caller the other arrangement commands do: a gamemaster-level operator, or a
 * player in creative.
 */
@EventBusSubscriber(modid = MODID)
public final class PlaceCommands {
    /** Gamemaster-level operators, and anyone building in creative. */
    private static final Predicate<CommandSourceStack> MAY_ARRANGE = source -> {
        ServerPlayer player = source.getPlayer();
        return (player != null && player.isCreative())
                || Commands.hasPermission(Commands.LEVEL_GAMEMASTERS).test(source);
    };

    private static final DynamicCommandExceptionType UNKNOWN = new DynamicCommandExceptionType(
            id -> Component.translatable("command.tolkienmobs.place.unknown", id));
    private static final DynamicCommandExceptionType NOT_SET = new DynamicCommandExceptionType(
            name -> Component.translatable("command.tolkienmobs.place.not_set", name));

    private PlaceCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("tmplace")
                .requires(MAY_ARRANGE)
                .then(Commands.literal("add")
                        .then(Commands.argument("id", StringArgumentType.word())
                                .suggests(PlaceCommands::suggest)
                                .executes(context -> add(context, Places.DEFAULT_RADIUS))
                                .then(Commands.argument("radius", IntegerArgumentType.integer(4, 512))
                                        .executes(context -> add(context,
                                                IntegerArgumentType.getInteger(context, "radius"))))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("id", StringArgumentType.word())
                                .suggests(PlaceCommands::suggest)
                                .executes(PlaceCommands::remove)))
                .then(Commands.literal("list").executes(PlaceCommands::list))
                .then(Commands.literal("where").executes(PlaceCommands::where))
                .then(Commands.literal("forget")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(PlaceCommands::forget))));
    }

    /** Sets a place around the caller's feet, or puts it somewhere new if it already had a home. */
    private static int add(CommandContext<CommandSourceStack> context, int radius)
            throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String id = StringArgumentType.getString(context, "id");
        Places.Name name = Places.known(id);
        if (name == null) throw UNKNOWN.create(id);
        Places.Place place = Places.set(source.getLevel(), id, BlockPos.containing(source.getPosition()), radius);
        Component label = Component.translatable(name.key());
        Component width = Component.literal(String.valueOf(place.radius()));
        source.sendSuccess(() -> Component.translatable("command.tolkienmobs.place.added", label, width), true);
        return 1;
    }

    /** Takes a name back off the ground, so nothing is introduced there any more. */
    private static int remove(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String id = StringArgumentType.getString(context, "id");
        Places.Name name = Places.known(id);
        if (name == null) throw UNKNOWN.create(id);
        Component label = Component.translatable(name.key());
        if (!Places.remove(source.getLevel(), id)) throw NOT_SET.create(label);
        source.sendSuccess(() -> Component.translatable("command.tolkienmobs.place.removed", label), true);
        return 1;
    }

    /**
     * Reads the catalogue back with where each name has been put - or says plainly that it has not
     * been put anywhere, which is the state a fresh world is in and the one worth seeing.
     */
    private static int list(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        List<Places.Name> catalogue = Places.catalogue();
        if (catalogue.isEmpty()) {
            source.sendSuccess(() -> Component.translatable("command.tolkienmobs.place.list.empty"), false);
            return 0;
        }
        int set = 0;
        for (Places.Name name : catalogue) {
            Component label = Component.translatable(name.key());
            Places.Place place = Places.place(source.getLevel(), name.id());
            if (place == null) {
                source.sendSuccess(() -> Component.translatable("command.tolkienmobs.place.list.unset", label), false);
                continue;
            }
            set++;
            BlockPos centre = place.centre();
            Component where = Component.literal(centre.getX() + ", " + centre.getY() + ", " + centre.getZ());
            Component width = Component.literal(String.valueOf(place.radius()));
            source.sendSuccess(() -> Component.translatable("command.tolkienmobs.place.list.one",
                    label, where, width), false);
        }
        return set;
    }

    /** Says which place the caller is standing in, which is how a radius is checked by hand. */
    private static int where(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();
        Places.Place here = Places.at(player);
        Places.Name name = here == null ? null : Places.known(here.id());
        if (name == null) {
            source.sendSuccess(() -> Component.translatable("command.tolkienmobs.place.nowhere"), false);
            return 0;
        }
        Component label = Component.translatable(name.key());
        source.sendSuccess(() -> Component.translatable("command.tolkienmobs.place.where", label), false);
        return 1;
    }

    /** Clears what the targets have been shown, so the banners on a road can be walked again. */
    private static int forget(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
        int cleared = 0;
        for (ServerPlayer player : players) {
            cleared += Places.forget(player);
        }
        int total = cleared;
        Component who = nameOf(players);
        Component count = Component.literal(String.valueOf(total));
        context.getSource().sendSuccess(() -> Component.translatable("command.tolkienmobs.place.forgot",
                who, count), true);
        return total;
    }

    /** Offers the catalogue, since an id typed by hand is an id the operator has to remember. */
    private static java.util.concurrent.CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> suggest(
            CommandContext<CommandSourceStack> context,
            com.mojang.brigadier.suggestion.SuggestionsBuilder builder) {
        List<String> ids = new ArrayList<>(Places.catalogue().size());
        for (Places.Name name : Places.catalogue()) {
            ids.add(name.id());
        }
        return SharedSuggestionProvider.suggest(ids, builder);
    }

    /** One name, or a count when several were named at once. */
    private static Component nameOf(Collection<ServerPlayer> players) {
        if (players.size() == 1) return players.iterator().next().getDisplayName();
        return Component.literal(String.valueOf(players.size()));
    }
}
