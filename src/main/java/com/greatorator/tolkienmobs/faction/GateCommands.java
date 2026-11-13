package com.greatorator.tolkienmobs.faction;

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
 * The gate is eight structure templates laid as a build. It belongs to a page or a place yet - a spot for it is
 * being measured - so it is asked for by hand, at the player's own feet and facing whichever way they look: the
 * facade's middle where they stand, the gate running on into the hill ahead of them. {@code clear} takes it down
 * again, because a spot tried is a spot to leave as it was found.
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
                .then(Commands.literal("place").executes(GateCommands::place))
                .then(Commands.literal("clear").executes(GateCommands::clear)));
    }

    /** Lays the eight templates at the player's feet, facing the way they face. */
    private static int place(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayer();
        if (player == null || !(source.getLevel() instanceof ServerLevel level)) {
            source.sendFailure(Component.literal("A gate is placed where somebody stands - be a player in a world first."));
            return 0;
        }
        BlockPos front = player.blockPosition();
        Rotation rotation = GundabadGate.rotationFor(player.getDirection());
        int laid = GundabadGate.place(level, front, rotation);
        List<String> missing = GundabadGate.missing(level);
        if (missing.isEmpty()) {
            source.sendSuccess(() -> Component.literal("Laid " + laid + " pieces of the Gundabad gate at "
                    + front.toShortString() + ", facing " + player.getDirection().getName() + ": "
                    + GundabadGate.WIDE + " wide, " + GundabadGate.TALL + " tall, " + GundabadGate.DEEP
                    + " deep, running into the ground ahead."), true);
        } else {
            source.sendSuccess(() -> Component.literal("Laid " + laid + " of 8 pieces of the Gundabad gate at "
                    + front.toShortString() + "; not in the pack: " + String.join(", ", missing) + "."), true);
        }
        return laid;
    }

    /** Takes the whole gate's box out again, so the spot is as it was found. */
    private static int clear(CommandContext<CommandSourceStack> context) {
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
