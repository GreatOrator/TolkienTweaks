package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * {@code /tmscene} - the chapter-one seam, put on and taken off by hand.
 * <p>
 * The seam is short and it sits behind a whole chapter, so it is the one piece of the campaign that cannot
 * be tried without walking to it: page 25 has to settle before the note exists, and the note has to be
 * carried to a courtyard to see anything at all. This is the same act done where the ground is: the note
 * can be handed over, the Company can be gathered, and the scene can be taken away again without a single
 * page being played.
 * <p>
 * {@code play} is a rehearsal - the cast stands where the scene really happens, or at the caller's feet
 * while no plaza has been marked, and the scene is taken away at the end without answering the note or
 * opening chapter two. So the placement can be looked at, and the pacing heard, before a page is spent on
 * it; what is seen there is what the pinned courtyard will get, because it is the same bodies, the same
 * marks and the same script.
 * <p>
 * {@code marks} is what turns the owner's placing into the scene's own: the eleven blocks set down on the
 * plaza, one per body, are read where they lie and written down as pins, so the layout is the blocks'
 * rather than a script's.
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
                .then(Commands.literal("skip").executes(SceneCommands::skip))
                .then(Commands.literal("marks")
                        .executes(context -> marks(context, 16))
                        .then(Commands.argument("radius", IntegerArgumentType.integer(2, 64))
                                .executes(context -> marks(context,
                                        IntegerArgumentType.getInteger(context, "radius"))))));
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

    /** Rehearses the scene where it really happens, opening no page. */
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

    // ---- the plaza's own marks ----

    /** One mark the owner set down: the block they chose, and the body it was chosen for. */
    private record Mark(Supplier<Block> block, String who, String slug) {
    }

    /**
     * The eleven blocks the plaza was marked with, by the body each one is for. The block is the whole of
     * the legend nobody has to keep: a mark is read where it lies, never typed in, so a mark that is moved
     * is read again by running the command again.
     */
    private static final List<Mark> MARKS = List.of(
            new Mark(TolkienBlocks.BLOCK_MORGULIRON, "Gandalf", "gandalf"),
            new Mark(TolkienBlocks.BLOCK_MITHRIL, "Elrond", "elrond"),
            new Mark(TolkienBlocks.BLOCK_AMMOLITE, "Aragorn", "aragorn"),
            new Mark(TolkienBlocks.DWARVEN_STONE, "Gimli", "gimli"),
            new Mark(TolkienBlocks.ELVEN_MARBLE, "Legolas", "legolas"),
            new Mark(TolkienBlocks.MOUNTAIN_STONE, "Boromir", "boromir"),
            new Mark(TolkienBlocks.ANGAINOR, "Bilbo", "bilbo"),
            new Mark(TolkienBlocks.PLANKS_CULUMALDA, "Frodo", "frodo"),
            new Mark(TolkienBlocks.PLANKS_LEBETHRON, "Samwise", "samwise"),
            new Mark(TolkienBlocks.PLANKS_DEADWOOD, "Pippin", "pippin"),
            new Mark(TolkienBlocks.PLANKS_DWARVEN_MAPLE, "Merry", "merry"));

    /**
     * Reads the plaza's marker blocks into pins: one body's ground for each, and Elrond's mark as the
     * gathering mark the scene begins at. The nearest block of each kind is the one taken, so a block of
     * the same stone left lying about cannot drag a mark off the plaza.
     */
    private static int marks(CommandContext<CommandSourceStack> context, int radius)
            throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        if (!(player.level() instanceof ServerLevel level)) return 0;
        BlockPos middle = player.blockPosition();
        Map<Block, Mark> byBlock = new HashMap<>();
        for (Mark mark : MARKS) byBlock.put(mark.block().get(), mark);
        Map<String, BlockPos> found = new LinkedHashMap<>();
        for (BlockPos pos : BlockPos.betweenClosed(middle.offset(-radius, -radius, -radius),
                middle.offset(radius, radius, radius))) {
            Mark mark = byBlock.get(level.getBlockState(pos).getBlock());
            if (mark == null) continue;
            BlockPos at = pos.immutable();
            BlockPos seen = found.get(mark.slug());
            if (seen == null || at.distToCenterSqr(middle.getX() + 0.5D, middle.getY(),
                    middle.getZ() + 0.5D) < seen.distToCenterSqr(middle.getX() + 0.5D, middle.getY(),
                    middle.getZ() + 0.5D)) {
                found.put(mark.slug(), at);
            }
        }
        if (found.isEmpty()) {
            context.getSource().sendFailure(Component.translatable("command.tolkienmobs.scene.marks.none",
                    Component.literal(Integer.toString(radius))));
            return 0;
        }
        context.getSource().sendSuccess(() -> Component.translatable("command.tolkienmobs.scene.marks",
                Component.literal(Integer.toString(found.size())),
                Component.literal(Integer.toString(MARKS.size()))), false);
        List<String> missing = new ArrayList<>();
        for (Mark mark : MARKS) {
            BlockPos at = found.get(mark.slug());
            if (at == null) {
                missing.add(mark.who());
                continue;
            }
            // A body stands on the ground its mark is set in, not on the mark: the block may be the floor
            // itself or laid flush in it, so the first air over that column is the spot in every case.
            BlockPos stand = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, at);
            Pins.set(level, Departure.bodyPin(mark.slug()), stand);
            // Elrond's own mark is also where the scene gathers and where the note begins to be watched.
            if (mark.slug().equals("elrond")) Pins.set(level, Departure.GATHER_PIN, stand);
            context.getSource().sendSuccess(() -> Component.translatable("command.tolkienmobs.scene.marks.set",
                    Component.literal(mark.who()), where(stand)), false);
        }
        if (!missing.isEmpty()) {
            context.getSource().sendSuccess(() -> Component.translatable(
                    "command.tolkienmobs.scene.marks.missing",
                    Component.literal(String.join(", ", missing))), false);
        }
        // The walk out needs no mark of its own: the ring has an open side, and that side is the road.
        BlockPos roadStart = Departure.exitStart(level);
        if (roadStart != null) {
            context.getSource().sendSuccess(() -> Component.translatable(
                    "command.tolkienmobs.scene.marks.exit", where(roadStart)), false);
        }
        return found.size();
    }

    /** A block as a player reads it, for a report line. */
    private static Component where(BlockPos at) {
        return Component.literal(at.getX() + ", " + at.getY() + ", " + at.getZ());
    }
}
