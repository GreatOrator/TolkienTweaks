package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.world.registration.ArdaMap;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Rotation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.List;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * {@code /tmgate} - the Gundabad gate tried in a world before anything is written down for every world.
 * <p>
 * The gate is eight structure templates laid as a build, and it has a site: {@link ArdaMap#gatePlan()}, facade
 * south, the coordinates the valley under Gundabad is shaped to. That is where it is laid unless somebody asks for
 * {@code here}, which lays it at the player's feet and facing whichever way they look - a tool for trying a piece
 * somewhere else, not how the gate stands. {@code clear} takes the site's box down again, and {@code clear here} a
 * box at the player, because a spot tried is a spot to leave as it was found.
 * <p>
 * A command is the right home for this and not a place: the players who walk the map have no commands, so
 * nothing here is allowed to be the only way the gate ever stands. Once a spot is chosen, the gate is pinned and
 * raised from the campaign clock the way the goblin cave and the camps are, and these two stay as tools.
 */
@EventBusSubscriber(modid = MODID)
public final class GateCommands {
    private GateCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("tmgate")
                .requires(source -> Commands.hasPermission(Commands.LEVEL_GAMEMASTERS).test(source))
                .then(Commands.literal("place").executes(GateCommands::site))
                .then(Commands.literal("site").executes(GateCommands::site))
                .then(Commands.literal("here").executes(GateCommands::here))
                .then(Commands.literal("clear")
                        .executes(GateCommands::clear)
                        .then(Commands.literal("here").executes(GateCommands::clearHere))));
    }

    /**
     * Lays the eight templates at the player's feet, facing the way they face, and dresses the seam.
     * <p>
     * A tool, not the way the gate is placed: where the build lands has to be the ground's measure, not the walker's
     * position, or every judgement of the terrain is made against a gate in a different place. This is for trying a
     * piece somewhere else.
     */
    private static int here(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayer();
        if (player == null || !(source.getLevel() instanceof ServerLevel level)) {
            source.sendFailure(Component.literal("A gate is placed where somebody stands - be a player in a world first."));
            return 0;
        }
        return lay(source, level, player.blockPosition(), GundabadGate.rotationFor(player.getDirection()),
                "facing " + player.getDirection().getName());
    }

    /**
     * Lays the gate at the site the pass was drawn for: {@link ArdaMap#gatePlan()}, facade south.
     * <p>
     * Turned four-square ({@link Rotation#NONE}). Turned half round it filled the same box but faced north - into the
     * mountain behind the plaza - with its back to the approach, so the courtyard was behind the gate instead of in
     * front of it.
     * <p>
     * The valley is shaped to that one spot - its floor at 187, its mountains out of the same floor, its plaza behind
     * the box's back - so the gate that fits it is the gate at those coordinates and no other. Every measurement of
     * the terrain, and every screenshot of the gate in it, is worth comparing only while the build lands in the same
     * place: this is the default, and {@code here} is the exception.
     */
    private static int site(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!(source.getLevel() instanceof ServerLevel level)) {
            source.sendFailure(Component.literal("The gate's site is ground in Arda - ask this in a world."));
            return 0;
        }
        return lay(source, level, ArdaMap.gatePlan(), Rotation.NONE, "facing south, at its own site");
    }

    /** The laying itself, and the seam dressed after it: the same act whether it is at a site or at a player. */
    private static int lay(CommandSourceStack source, ServerLevel level, BlockPos front, Rotation rotation,
                           String where) {
        int laid = GundabadGate.place(level, front, rotation);
        List<String> missing = GundabadGate.missing(level);
        if (missing.isEmpty()) {
            // The seam is left undressed for now, so what is judged is the ground the pass made and not the skirt the
            // drape lays over it: GundabadGate.drape is still here, and is called again once the ground is settled.
            source.sendSuccess(() -> Component.literal("Laid " + laid + " pieces of the Gundabad gate at "
                    + front.toShortString() + ", " + where + ": " + GundabadGate.WIDE + " wide, "
                    + GundabadGate.TALL + " tall, " + GundabadGate.DEEP + " deep, seam left undressed."), true);
        } else {
            source.sendSuccess(() -> Component.literal("Laid " + laid + " of 8 pieces of the Gundabad gate at "
                    + front.toShortString() + "; not in the pack: " + String.join(", ", missing) + "."), true);
        }
        return laid;
    }

    /** Takes the whole gate's box out again at its own site, so the spot is as it was found. */
    private static int clear(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!(source.getLevel() instanceof ServerLevel level)) {
            source.sendFailure(Component.literal("The gate's site is ground in Arda - ask this in a world."));
            return 0;
        }
        BlockPos front = ArdaMap.gatePlan();
        int cleared = GundabadGate.clear(level, front, Rotation.NONE);
        source.sendSuccess(() -> Component.literal("Cleared " + cleared + " blocks out of the gate's box at "
                + front.toShortString() + "."), true);
        return cleared;
    }

    /** Takes a gate's box out where the player stands, for a piece tried somewhere else. */
    private static int clearHere(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayer();
        if (player == null || !(source.getLevel() instanceof ServerLevel level)) {
            source.sendFailure(Component.literal("A gate is cleared where one was placed - be a player in a world first."));
            return 0;
        }
        BlockPos front = player.blockPosition();
        Rotation rotation = GundabadGate.rotationFor(player.getDirection());
        int cleared = GundabadGate.clear(level, front, rotation);
        source.sendSuccess(() -> Component.literal("Cleared " + cleared + " blocks out of the gate's box at "
                + front.toShortString() + "."), true);
        return cleared;
    }
}
