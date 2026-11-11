package com.greatorator.tolkienmobs.faction;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * {@code /tmcave} - the cave cut by hand, so it can be walked before anything is built around it.
 * <p>
 * A cave is taken out of the hill the world already made, which means it cannot be placed while the
 * ground under it is being made the way a structure's pieces are: the ground has to exist first. So the
 * cut is a hand rather than worldgen, and this is it - it reads the world's goblin pin, cuts the cave
 * behind it, and says what came out, leaving the design and the pin the only two things to change.
 */
@EventBusSubscriber(modid = MODID)
public final class CaveCommands {
    private CaveCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("tmcave")
                .requires(source -> Commands.hasPermission(Commands.LEVEL_GAMEMASTERS).test(source))
                .executes(CaveCommands::cut)
                .then(Commands.literal("cut").executes(CaveCommands::cut))
                .then(Commands.literal("dress").executes(CaveCommands::dress)));
    }

    /** Cuts the goblin cave behind this world's pin, and reports the cut. */
    private static int cut(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!(source.getLevel() instanceof ServerLevel level)) {
            source.sendFailure(Component.literal("A cave is cut in Arda - stand where the door goes first."));
            return 0;
        }
        GoblinCave.Result result = GoblinCave.cut(level);
        if (result == null) {
            source.sendFailure(Component.literal("No goblin door to cut at: stand where it goes and use "
                    + "/tmpin place " + GoblinCave.PIN + ", or the ground there is not made yet."));
            return 0;
        }
        source.sendSuccess(() -> Component.literal("Cut the goblin cave at " + result.door().toShortString()
                + ", entered towards " + result.into().getName() + ": " + result.cells() + " blocks out, "
                + result.dropped() + " dropped where the hill was too shallow to roof them, floor "
                + result.floor() + " to ceiling " + result.ceiling()
                + (result.hall() != null
                ? ", hall laid from " + result.hall().toShortString() + "."
                : ", no hall: tolkienmobs:goblin_hall is not in the pack.")), true);
        return result.cells();
    }

    /**
     * Lays the hall's structure over the hall the cut made, without cutting the cave again: the way a hall
     * furnished by hand gets back into the world, by saving the decorated room over its template and asking
     * for it here.
     */
    private static int dress(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!(source.getLevel() instanceof ServerLevel level)) {
            source.sendFailure(Component.literal("A cave is dressed in Arda - stand where the door is first."));
            return 0;
        }
        BlockPos hall = GoblinCave.dress(level);
        if (hall == null) {
            source.sendFailure(Component.literal("No goblin cave to dress at: /tmpin place " + GoblinCave.PIN
                    + " first, or tolkienmobs:goblin_hall is not in the pack."));
            return 0;
        }
        source.sendSuccess(() -> Component.literal("Dressed the goblin hall from its structure, laid from "
                + hall.toShortString() + "."), true);
        return 1;
    }
}
