package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.world.registration.ArdaMap;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.function.Predicate;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * What the drawing and its grading make of a column, said in the world.
 * <p>
 * The river work was measured by re-writing the map's own rules in a script: the branch's profile, the valley's
 * sections, whether an ordinary bank moves at all. A script is the wrong instrument for the last step of it. It does
 * not have the bilinear read, the roll, the clamped corners or the rounding, so it can agree with the code while both
 * are wrong - and it says nothing at all about the column a player is actually looking at.
 * <p>
 * So the numbers come out of the classes the generator runs: {@code /tmriver at} reads the column under the hand, and
 * {@code /tmriver along} walks a line and reads one every few blocks, which is a section across a valley or a profile
 * down one. The ceiling is the number worth watching: the valley's rule is a rate, a reach and a depth, and a rate
 * read wrong is a hillside cut wrong over hundreds of blocks.
 */
@EventBusSubscriber(modid = MODID)
public final class RiverCommands {
    /** Whoever may read the map, which is anyone the gm level is given to. */
    private static final Predicate<CommandSourceStack> MAY_ASK = source ->
            Commands.hasPermission(Commands.LEVEL_GAMEMASTERS).test(source);

    private RiverCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("tmriver")
                .requires(MAY_ASK)
                .then(Commands.literal("at")
                        .executes(RiverCommands::at))
                .then(Commands.literal("along")
                        .then(Commands.argument("from", BlockPosArgument.blockPos())
                                .then(Commands.argument("to", BlockPosArgument.blockPos())
                                        .executes(context -> along(context, 8))
                                        .then(Commands.argument("step", IntegerArgumentType.integer(1, 64))
                                                .executes(context -> along(context,
                                                        IntegerArgumentType.getInteger(context, "step"))))))));
    }

    /** The column under the hand. */
    private static int at(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        BlockPos feet = BlockPos.containing(source.getPosition());
        ArdaMap.Column column = ArdaMap.column(feet.getX(), feet.getZ());
        source.sendSuccess(() -> Component.literal(spoken(column)), false);
        return 1;
    }

    /**
     * A line of columns, one every {@code step} blocks, from the named corner to the named corner.
     * <p>
     * Both ends are read, so a section taken from one bank to the other says what the bank is as well as what the
     * channel is, and the last line sums the run for a hand that only wants the shape of it.
     */
    private static int along(CommandContext<CommandSourceStack> context, int step) {
        CommandSourceStack source = context.getSource();
        BlockPos from = BlockPosArgument.getBlockPos(context, "from");
        BlockPos to = BlockPosArgument.getBlockPos(context, "to");
        double span = Math.hypot(to.getX() - from.getX(), to.getZ() - from.getZ());
        int readings = (int) Math.ceil(span / Math.max(1, step)) + 1;
        if (readings > 512) {
            source.sendFailure(Component.literal("That is " + readings + " columns: " + span
                    + " blocks long at " + step + " a step. Take it in two, or a longer step."));
            return 0;
        }
        int deepest = 0;
        int lowest = Integer.MAX_VALUE;
        int highest = Integer.MIN_VALUE;
        for (int at = 0; at < readings; at++) {
            double share = readings == 1 ? 0.0D : at / (double) (readings - 1);
            int x = (int) Math.round(from.getX() + (to.getX() - from.getX()) * share);
            int z = (int) Math.round(from.getZ() + (to.getZ() - from.getZ()) * share);
            ArdaMap.Column column = ArdaMap.column(x, z);
            deepest = Math.max(deepest, column.taken());
            lowest = Math.min(lowest, column.ground());
            highest = Math.max(highest, column.ground());
            source.sendSuccess(() -> Component.literal(spoken(column)), false);
        }
        int cut = deepest;
        int low = lowest;
        int high = highest;
        int count = readings;
        source.sendSuccess(() -> Component.literal("That is " + count + " columns, ground " + low + " to " + high
                + ", and the valley takes at most " + cut + " blocks off any of them."), false);
        return count;
    }

    /** One column as a line: where it is, what it stands at, and what the river's valley holds it to. */
    private static String spoken(ArdaMap.Column column) {
        String ceiling = column.ceiling() == Integer.MAX_VALUE ? "none" : Integer.toString(column.ceiling());
        return column.x() + " " + column.z()
                + "  ground " + column.ground()
                + ", water " + column.water()
                + ", ceiling " + ceiling
                + (column.taken() > 0 ? " (takes " + column.taken() + ")" : "")
                + (column.river() ? ", river" : "");
    }
}
