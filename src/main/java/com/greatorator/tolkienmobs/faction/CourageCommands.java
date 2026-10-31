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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Collection;
import java.util.function.Predicate;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The commands that let an operator put a player's courage where they need it to be.
 * <p>
 * Courage is earned - work handed in, fear stood in, the Nine put down - so there is nothing here a
 * player would use. It exists because the rungs are a long climb by design: trying what the fourth
 * rung does to a Nazgul should not cost an afternoon of questing, and a scene staged in a test world
 * should be able to start with a player who has already been through the war.
 * <p>
 * One root, {@code /tmcourage <targets> <deeds>} and {@code /tmcourage <targets> get}. It answers to
 * the same caller the other arrangement commands do: a gamemaster-level operator, or a player in
 * creative.
 */
@EventBusSubscriber(modid = MODID)
public final class CourageCommands {
    /** Gamemaster-level operators, and anyone building in creative. */
    private static final Predicate<CommandSourceStack> MAY_ARRANGE = source -> {
        ServerPlayer player = source.getPlayer();
        return (player != null && player.isCreative())
                || Commands.hasPermission(Commands.LEVEL_GAMEMASTERS).test(source);
    };

    private CourageCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("tmcourage")
                .requires(MAY_ARRANGE)
                .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.literal("get").executes(CourageCommands::get))
                        .then(Commands.argument("deeds", IntegerArgumentType.integer(0, Courage.CEILING))
                                .executes(CourageCommands::set))));
    }

    /** Puts every target's courage at a exact number of deeds, and says where that leaves them. */
    private static int set(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
        int deeds = IntegerArgumentType.getInteger(context, "deeds");
        for (ServerPlayer player : players) {
            CourageLedger.set(player, deeds);
        }
        Collection<ServerPlayer> who = players;
        context.getSource().sendSuccess(() -> Component.translatable("command.tolkienmobs.courage.set",
                nameOf(who), Component.literal(String.valueOf(deeds))), true);
        return players.size();
    }

    /** Reads back what each target has earned, which is what the ledger's row is drawing. */
    private static int get(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
        for (ServerPlayer player : players) {
            int deeds = CourageLedger.courage(player);
            Courage stage = Courage.of(deeds);
            Component name = player.getDisplayName();
            context.getSource().sendSuccess(() -> Component.translatable("command.tolkienmobs.courage.get",
                    name, Component.literal(String.valueOf(deeds)), stage.displayName()), false);
        }
        return players.size();
    }

    /** One name, or a count when several were named at once. */
    private static Component nameOf(Collection<ServerPlayer> players) {
        if (players.size() == 1) return players.iterator().next().getDisplayName();
        return Component.literal(String.valueOf(players.size()));
    }
}
