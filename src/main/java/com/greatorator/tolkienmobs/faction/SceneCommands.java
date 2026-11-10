package com.greatorator.tolkienmobs.faction;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.function.Predicate;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * {@code /tmscene} - the chapter-one seam, put on and taken off by hand.
 * <p>
 * The seam is short and it sits behind a whole chapter, so it is the one piece of the campaign that cannot
 * be tried without walking to it: page 25 has to settle before the note exists, and the note has to be
 * carried to a courtyard to see anything at all. This is the same act done where the ground is: the note
 * can be handed over, the Company can be gathered round the caller's own feet, and the scene can be taken
 * away again without a single page being played.
 * <p>
 * {@code play} is the one that matters before any marker is set. It is a rehearsal - the cast stands where
 * the caller is standing, the lines are spoken, and the scene is taken away at the end without answering
 * the note or opening chapter two. So the placement can be looked at, and the pacing heard, on ground that
 * has not been pinned yet; what is seen there is what the pinned courtyard will get, because it is the
 * same bodies, the same offsets and the same script.
 * <p>
 * It answers to the same caller the other arrangement commands do - a gamemaster-level operator, or a
 * player in creative - since putting the chapter's people into the world is arranging, not playing.
 */
@EventBusSubscriber(modid = MODID)
public final class SceneCommands {
    /** Gamemaster-level operators, and anyone building in creative. */
    private static final Predicate<CommandSourceStack> MAY_DIRECT = source -> {
        ServerPlayer player = source.getPlayer();
        return (player != null && player.isCreative())
                || Commands.hasPermission(Commands.LEVEL_GAMEMASTERS).test(source);
    };

    private SceneCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("tmscene")
                .requires(MAY_DIRECT)
                .executes(SceneCommands::state)
                .then(Commands.literal("state").executes(SceneCommands::state))
                .then(Commands.literal("note").executes(SceneCommands::note))
                .then(Commands.literal("play").executes(SceneCommands::play))
                .then(Commands.literal("stop").executes(SceneCommands::stop))
                .then(Commands.literal("forget").executes(SceneCommands::forget))
                .then(Commands.literal("skip").executes(SceneCommands::skip)));
    }

    /** Where the seam stands for the caller: which page, whether the note is waiting, whether it is playing. */
    private static int state(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        int stage = Campaign.stage(player, Alliance.WEST);
        int pages = Campaign.pages(Alliance.WEST);
        Component yes = Component.translatable("command.tolkienmobs.scene.yes");
        Component no = Component.translatable("command.tolkienmobs.scene.no");
        Component note = Departure.awaiting(player) ? yes : no;
        Component show = Departure.showing(player) ? yes : no;
        context.getSource().sendSuccess(() -> Component.translatable("command.tolkienmobs.scene.state",
                Component.literal(Integer.toString(stage)), Component.literal(Integer.toString(pages)),
                note, show), false);
        return stage;
    }

    /** Leaves the summons in the caller's hand, as the end of chapter one would. */
    private static int note(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Departure.summon(player);
        if (!Departure.awaiting(player)) {
            context.getSource().sendFailure(Component.translatable("command.tolkienmobs.scene.answered",
                    player.getName()));
            return 0;
        }
        context.getSource().sendSuccess(() -> Component.translatable("command.tolkienmobs.scene.note",
                player.getName()), false);
        return 1;
    }

    /** Rehearses the scene where the caller stands, opening no page. */
    private static int play(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Departure.play(player);
        context.getSource().sendSuccess(() -> Component.translatable("command.tolkienmobs.scene.play",
                player.getName()), false);
        return 1;
    }

    /** Takes the cast away and leaves the note as it was. */
    private static int stop(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Departure.stop(player);
        context.getSource().sendSuccess(() -> Component.translatable("command.tolkienmobs.scene.stop"), false);
        return 1;
    }

    /** Forgets the note and the having-seen, so the seam can be walked from its start again. */
    private static int forget(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Departure.forget(player);
        context.getSource().sendSuccess(() -> Component.translatable("command.tolkienmobs.scene.forget",
                player.getName()), false);
        return 1;
    }

    /** Answers the seam without watching it, for a scene that would not start or could not finish. */
    private static int skip(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Departure.skip(player);
        context.getSource().sendSuccess(() -> Component.translatable("command.tolkienmobs.scene.skip",
                player.getName()), false);
        return 1;
    }
}
