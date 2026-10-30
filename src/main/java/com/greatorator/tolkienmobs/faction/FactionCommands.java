package com.greatorator.tolkienmobs.faction;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Predicate;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The commands that let an operator arrange a world's politics by hand: putting a player's standing
 * with one alliance where they want it, and reading back where that player stands with all three.
 * <p>
 * They answer to one root, {@code /tmfaction set} and {@code /tmfaction get}, and to the two flat names
 * {@code /tmsetfaction} and {@code /tmgetfaction} for anyone who would rather type the verb first.
 * <p>
 * Both are for someone running the world rather than playing in it - a gamemaster-level operator, or a
 * player in creative, which is how a creative world is usually run. Everything else about a standing is
 * earned, so these exist for building, staging a scene and testing the ranks themselves; the setter is
 * held to the same bounds as a hand-in or a kill, so a command cannot put a standing below the floor a
 * country turns hostile at or above the ceiling.
 */
@EventBusSubscriber(modid = MODID)
public final class FactionCommands {
    /** Gamemaster-level operators, and anyone building in creative. */
    private static final Predicate<CommandSourceStack> MAY_EDIT = source -> {
        ServerPlayer player = source.getPlayer();
        return (player != null && player.isCreative())
                || Commands.hasPermission(Commands.LEVEL_GAMEMASTERS).test(source);
    };

    private FactionCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(setBranch("tmsetfaction"));
        dispatcher.register(getBranch("tmgetfaction"));
        dispatcher.register(Commands.literal("tmfaction")
                .requires(MAY_EDIT)
                .then(setBranch("set"))
                .then(getBranch("get")));
    }

    /** {@code <name> <targets> <alliance> <amount>}: the whole setter, under whichever root it is given. */
    private static LiteralArgumentBuilder<CommandSourceStack> setBranch(String name) {
        LiteralArgumentBuilder<CommandSourceStack> branch = Commands.literal(name)
                .requires(MAY_EDIT)
                .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.literal(Alliance.WEST.id())
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .executes(context -> set(context, Alliance.WEST))))
                        .then(Commands.literal(Alliance.EAST.id())
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .executes(context -> set(context, Alliance.EAST))))
                        .then(Commands.literal(Alliance.HARAD.id())
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .executes(context -> set(context, Alliance.HARAD)))));
        return branch;
    }

    /** {@code <name> <targets> [alliance]}: the whole reader, under whichever root it is given. */
    private static LiteralArgumentBuilder<CommandSourceStack> getBranch(String name) {
        return Commands.literal(name)
                .requires(MAY_EDIT)
                .then(Commands.argument("targets", EntityArgument.players())
                        .executes(context -> get(context, null))
                        .then(Commands.literal(Alliance.WEST.id()).executes(context -> get(context, Alliance.WEST)))
                        .then(Commands.literal(Alliance.EAST.id()).executes(context -> get(context, Alliance.EAST)))
                        .then(Commands.literal(Alliance.HARAD.id()).executes(context -> get(context, Alliance.HARAD))));
    }

    /** Puts every target at the same standing with one alliance, and answers with where they landed. */
    private static int set(CommandContext<CommandSourceStack> context, Alliance alliance) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
        int amount = FactionStandings.clamp(IntegerArgumentType.getInteger(context, "amount"));
        for (ServerPlayer player : players) {
            FactionStandings.set(player, alliance, amount);
        }

        Component points = Component.literal(String.valueOf(amount));
        Component rank = FactionEvents.rankName(FactionRank.of(amount));
        Component message = players.size() == 1
                ? Component.translatable("command.tolkienmobs.set_faction",
                        players.iterator().next().getDisplayName(), alliance.displayName(), points, rank)
                : Component.translatable("command.tolkienmobs.set_faction.many",
                        players.size(), alliance.displayName(), points, rank);
        context.getSource().sendSuccess(() -> message, true);
        return players.size();
    }

    /**
     * Reads a standing back: one alliance when it is named, all three when it is not. The answer goes to
     * whoever ran the command rather than to the world, so asking about a player does not announce it.
     */
    private static int get(CommandContext<CommandSourceStack> context, @Nullable Alliance only) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
        for (ServerPlayer player : players) {
            if (only != null) {
                int standing = FactionStandings.standing(player, only);
                Component message = Component.translatable("command.tolkienmobs.get_faction.one",
                        player.getDisplayName(), only.displayName(),
                        Component.literal(String.valueOf(standing)),
                        FactionEvents.rankName(FactionRank.of(standing)));
                context.getSource().sendSuccess(() -> message, false);
            } else {
                Component message = Component.translatable("command.tolkienmobs.get_faction.all",
                        player.getDisplayName(), describeAll(player));
                context.getSource().sendSuccess(() -> message, false);
            }
        }
        return players.size();
    }

    /** The three standings on one line, each in its alliance's own colour. */
    private static Component describeAll(ServerPlayer player) {
        MutableComponent line = Component.empty();
        boolean first = true;
        for (Alliance alliance : Alliance.values()) {
            int standing = FactionStandings.standing(player, alliance);
            if (!first) {
                line.append(Component.literal("   "));
            }
            first = false;
            line.append(Component.translatable("command.tolkienmobs.get_faction.entry",
                            alliance.displayName(), Component.literal(String.valueOf(standing)),
                            FactionEvents.rankName(FactionRank.of(standing)))
                    .withStyle(alliance.chat()));
        }
        return line;
    }
}
