package com.greatorator.tolkienmobs.faction;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
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

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The commands that let an operator arrange the work by hand, so the quest system can be tried without
 * waiting to be met by a giver.
 * <p>
 * One root, {@code /tmquest <targets> <give|quit|spawn|campaign_giver|complete>}:
 * <ul>
 *   <li>{@code give [count]} writes fresh work straight into the targets' books, as if a giver had
 *   offered it. No NPC is placed, so the entry is named for a giver who does not exist - that is the
 *   point of it, the book filling without a walk in the world.</li>
 *   <li>{@code quit} sets aside everything a target carries, which is how a test starts over.</li>
 *   <li>{@code complete [count]} pays off what the targets are carrying at once, through the same
 *   path a hand-in takes, so a line of campaign pages can be walked without earning every one of them.
 *   This is the only entry here that gives a player something they did not do, and that is why it
 *   stands beside {@code quit} rather than being folded into {@code give}.</li>
 *   <li>{@code spawn} puts a real giver, with a real offer, beside each target now, skipping the wait
 *   between givers and the one-near rule: those are the player's experience of the feature, not rules
 *   about the world.</li>
 *   <li>{@code campaign_giver <alliance>} stands that front's named giver - Maelgrin, Guthlac or
 *   Zarabi - beside each target, so the campaign can be walked without first cutting his hall. A giver
 *   of that alliance already standing near the target is left where it is, and the answer says which
 *   of the two happened.</li>
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
                                                IntegerArgumentType.integer(1, QuestLedger.MAX_BOUNTIES))
                                        .executes(context -> give(context,
                                                IntegerArgumentType.getInteger(context, "count")))))
                        .then(Commands.literal("quit").executes(QuestCommands::quit))
                        .then(Commands.literal("spawn").executes(QuestCommands::spawn))
                        .then(Commands.literal("complete")
                                .executes(context -> complete(context, Integer.MAX_VALUE))
                                .then(Commands.argument("count", IntegerArgumentType.integer(1, 32))
                                        .executes(context -> complete(context,
                                                IntegerArgumentType.getInteger(context, "count")))))
                        .then(Commands.literal("campaign_giver")
                                .then(Commands.argument("alliance", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            for (Alliance alliance : Alliance.values()) {
                                                builder.suggest(alliance.id());
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(QuestCommands::spawnCampaignGiver)))
                .then(Commands.literal("page")
                        .then(Commands.argument("kind", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    for (Quest.Kind kind : Quest.Kind.values()) {
                                        builder.suggest(kind.name().toLowerCase(Locale.ROOT));
                                    }
                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("target", StringArgumentType.string())
                                        .executes(context -> page(context, 1))
                                        .then(Commands.argument("goal",
                                                        IntegerArgumentType.integer(1, 64))
                                                .executes(context -> page(context,
                                                        IntegerArgumentType.getInteger(context, "goal")))))))));
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
                Quest work = QuestPool.offer(player, random, employer)
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

    /**
     * Settles what the targets are carrying, so a chain of pages can be walked without earning it. A
     * book with nothing in it is told so rather than reported as a success of zero.
     */
    private static int complete(CommandContext<CommandSourceStack> context, int limit) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
        int settled = 0;
        for (ServerPlayer player : players) {
            settled += QuestLedger.complete(player, limit);
        }
        if (settled == 0) {
            context.getSource().sendFailure(Component.translatable("command.tolkienmobs.quest.complete.none"));
            return 0;
        }
        int total = settled;
        Collection<ServerPlayer> who = players;
        context.getSource().sendSuccess(() -> Component.translatable("command.tolkienmobs.quest.complete",
                nameOf(who), Component.literal(String.valueOf(total))), true);
        return settled;
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

    /** Stands a named campaign giver for the named alliance beside each target. */
    private static int spawnCampaignGiver(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
        String allianceId = StringArgumentType.getString(context, "alliance");
        Alliance alliance = allianceOf(allianceId);
        if (alliance == null) {
            context.getSource().sendFailure(Component.translatable(
                    "command.tolkienmobs.quest.campaign_giver.unknown", Component.literal(allianceId)));
            return 0;
        }

        int stood = 0;
        int already = 0;
        int blocked = 0;
        for (ServerPlayer player : players) {
            switch (QuestGivers.ensureCampaignGiver(player, alliance)) {
                case STOOD -> stood++;
                case ALREADY_THERE -> already++;
                case NO_GROUND -> blocked++;
            }
        }

        // Three answers, because three different things can have happened: a body was put down, one was
        // already standing, or the ground would not take one. Reporting the last as the second said the
        // command had found the giver it was looking for when it had found nothing at all.
        int total = stood;
        Collection<ServerPlayer> who = players;
        if (total > 0) {
            context.getSource().sendSuccess(() -> Component.translatable(
                    "command.tolkienmobs.quest.campaign_giver", Component.literal(String.valueOf(total)),
                    alliance.displayName(), nameOf(who)), true);
        }
        if (already > 0) {
            context.getSource().sendSuccess(() -> Component.translatable(
                    "command.tolkienmobs.quest.campaign_giver.present", alliance.displayName(), nameOf(who)),
                    false);
        }
        if (blocked > 0) {
            context.getSource().sendFailure(Component.translatable(
                    "command.tolkienmobs.quest.campaign_giver.no_ground", alliance.displayName(), nameOf(who)));
        }
        return total;
    }

    /**
     * Writes one page of a named shape straight into a book, so a page that is walked or spoken can be
     * tried without a hall to hand it out. The target is written the way a page writes it, with a
     * prefix saying what it is: {@code place:greenway}, {@code at:100,64,-20,16},
     * {@code leaf:campaign.tolkienmobs.page.muster} or {@code person:Maelgrin}. A bare word is read as
     * a place for a location page and as a body for a dialogue one.
     */
    private static int page(CommandContext<CommandSourceStack> context, int goal) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
        Quest.Kind kind = Quest.Kind.byName(StringArgumentType.getString(context, "kind"));
        String target = StringArgumentType.getString(context, "target");
        int written = 0;
        for (ServerPlayer player : players) {
            // A page written by hand pays nothing by itself: what is being tried is whether the
            // ground or the words finish it, not what it is worth.
            Quest work = new Quest(kind, target, goal, 0, "", 0, List.of(), "",
                    player.blockPosition(), Quest.NOBODY, false, "");
            if (QuestLedger.accept(player, work)) written++;
        }
        int total = written;
        Collection<ServerPlayer> who = players;
        context.getSource().sendSuccess(() -> Component.translatable("command.tolkienmobs.quest.page",
                nameOf(who), Component.literal(String.valueOf(total))), true);
        return written;
    }

    /**
     * The alliance an id names, or null when it names none. {@link Alliance#byId} answers the West for
     * anything it does not recognise - the suppliers that read a saved name off disk need it to answer
     * something - so a command that has to refuse a typo cannot go through it.
     */
    @Nullable
    private static Alliance allianceOf(String id) {
        for (Alliance alliance : Alliance.values()) {
            if (alliance.id().equals(id)) return alliance;
        }
        return null;
    }

    /** One player by name, or a count when the command was given a selector that matched several. */
    private static Component nameOf(Collection<ServerPlayer> players) {
        return players.size() == 1
                ? players.iterator().next().getDisplayName()
                : Component.translatable("command.tolkienmobs.quest.players",
                Component.literal(String.valueOf(players.size())));
    }
}
