package com.greatorator.tolkienmobs.faction;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Collection;
import java.util.function.Predicate;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The commands that let an operator arrange the work by hand, so the quest system can be tried without
 * waiting to be met by a giver.
 * <p>
 * One root, {@code /tmquest <targets> <give|quit|spawn>}:
 * <ul>
 *   <li>{@code give [count]} writes fresh work straight into the targets' books, as if a giver had
 *   offered it. No NPC is placed, so the entry is named for a giver who does not exist - that is the
 *   point of it, the book filling without a walk in the world.</li>
 *   <li>{@code quit} sets aside everything a target carries, which is how a test starts over.</li>
 *   <li>{@code spawn} puts a real giver, with a real offer, beside each target now, skipping the wait
 *   between givers and the one-near rule: those are the player's experience of the feature, not rules
 *   about the world.</li>
 * </ul>
 * This is gamemaster work - the work a player takes is meant to be earned - so the root answers only to
 * an operator at gamemaster level or to a player in creative, exactly as the faction commands do.
 */
@EventBusSubscriber(modid = MODID)
public final class QuestCommands {
    /** Gamemaster-level operators, and anyone building in creative. */
    private static final Predicate<CommandSourceStack> MAY_ARRANGE = source -> {
        ServerPlayer player = source.getPlayer();
        return (player != null && player.isCreative())
                || Commands.hasPermission(Commands.LEVEL_GAMEMASTERS).test(source);
    };

    private QuestCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("tmquest")
                .requires(MAY_ARRANGE)
                .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.literal("give")
                                .executes(context -> give(context, 1))
                                .then(Commands.argument("count",
                                                IntegerArgumentType.integer(1, QuestLedger.MAX_ACTIVE))
                                        .executes(context -> give(context,
                                                IntegerArgumentType.getInteger(context, "count")))))
                        .then(Commands.literal("quit").executes(QuestCommands::quit))
                        .then(Commands.literal("spawn").executes(QuestCommands::spawn))));
    }

    /**
     * Fills a book with work drawn the way a giver draws it, from the country the player is standing in.
     * A book that is already full takes nothing more, so {@code count} is an upper bound rather than a
     * promise, and the answer says how many actually landed.
     */
    private static int give(CommandContext<CommandSourceStack> context, int count) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
        int given = 0;
        for (ServerPlayer player : players) {
            RandomSource random = player.level().getRandom();
            Alliance employer = QuestGivers.employer(player);
            for (int i = 0; i < count; i++) {
                Quest work = QuestPool.offer(random, employer)
                        .withGiver(QuestPool.giverLabel(random), player.blockPosition(), Quest.NOBODY);
                if (!QuestLedger.accept(player, work)) break;
                given++;
            }
        }
        int total = given;
        Collection<ServerPlayer> who = players;
        context.getSource().sendSuccess(() -> Component.translatable("command.tolkienmobs.quest.give",
                nameOf(who), Component.literal(String.valueOf(total))), true);
        return given;
    }

    /** Takes the work back off every target, without paying for any of it. */
    private static int quit(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
        int cleared = 0;
        for (ServerPlayer player : players) {
            cleared += QuestLedger.clear(player);
        }
        int total = cleared;
        Collection<ServerPlayer> who = players;
        context.getSource().sendSuccess(() -> Component.translatable("command.tolkienmobs.quest.quit",
                Component.literal(String.valueOf(total)), nameOf(who)), true);
        return cleared;
    }

    /** Stands a giver beside each target at once, each holding work of its own. */
    private static int spawn(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
        int spawned = 0;
        for (ServerPlayer player : players) {
            if (QuestGivers.forceSpawn(player)) {
                spawned++;
            } else {
                context.getSource().sendFailure(Component.translatable("command.tolkienmobs.quest.spawn.failed",
                        player.getDisplayName()));
            }
        }
        if (spawned > 0) {
            int total = spawned;
            Collection<ServerPlayer> who = players;
            context.getSource().sendSuccess(() -> Component.translatable("command.tolkienmobs.quest.spawn",
                    nameOf(who), Component.literal(String.valueOf(total))), true);
        }
        return spawned;
    }

    /** One player by name, or a count when the command was given a selector that matched several. */
    private static Component nameOf(Collection<ServerPlayer> players) {
        return players.size() == 1
                ? players.iterator().next().getDisplayName()
                : Component.translatable("command.tolkienmobs.quest.players",
                Component.literal(String.valueOf(players.size())));
    }
}
