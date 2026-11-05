package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.world.registration.ArdaMap;
import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * {@code /tmtp} - the quick map: one jump per country of the drawing.
 * <p>
 * Arda is forty-eight cells by thirty-six, half a million blocks across, and nothing in it is a
 * teleport yet: a region is reached by typing three numbers, which is what
 * {@code docs/arda-teleports.md} is a table of. This is that table as a command, so walking a region
 * does not start with copying coordinates out of a file.
 * <p>
 * The points are <b>computed, not typed</b>. A country's middle is {@link ArdaMap#anchor}, the same
 * middle the survey falls back on, and the height above it is read from the drawing with
 * {@link ArdaMap#sample} - so a jump follows the map when a fill moves, which the hand-written table
 * cannot. The three seas are stood over their water line rather than their bed, since the bed of a sea
 * is not a place to be put down in.
 * <p>
 * A country whose build is settled does not want its middle, it wants the build. Those go in
 * {@link #LOCKED}, one line each, and every jump afterwards lands on the build: the country's middle
 * is only the answer until something real stands somewhere.
 * <p>
 * It answers to the same caller the other arrangement commands do - a gamemaster-level operator, or a
 * player in creative - because a jump across the map is not how the campaign is played.
 */
@EventBusSubscriber(modid = MODID)
public final class TeleportCommands {
    /** Gamemaster-level operators, and anyone building in creative. */
    private static final Predicate<CommandSourceStack> MAY_GO = source -> {
        ServerPlayer player = source.getPlayer();
        return (player != null && player.isCreative())
                || Commands.hasPermission(Commands.LEVEL_GAMEMASTERS).test(source);
    };

    /** How far over the ground the caller is set down, as the hand-written table in the docs does. */
    private static final int CLEARANCE = 8;

    /**
     * Regions whose ground has been settled, in blocks, replacing the middle of the country.
     * <p>
     * A name here is a build, or the cell a page surveys it from, and a jump lands on that point
     * exactly rather than on the middle of the cells the country was drawn in. This is the one place
     * that changes when a build is pinned, so it is kept to names and positions with nothing else in
     * it:
     * <pre>
     * "bree", new BlockPos(1024, 96, 1024),
     * </pre>
     */
    private static final Map<String, BlockPos> LOCKED = Map.of(
            // Nothing is pinned yet: every region is still its country's middle.
    );

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("tmtp")
                .requires(MAY_GO)
                .executes(TeleportCommands::list)
                .then(Commands.literal("list").executes(TeleportCommands::list))
                .then(Commands.argument("region", StringArgumentType.word())
                        .suggests(TeleportCommands::suggest)
                        .executes(TeleportCommands::go)));
    }

    /** Every region name, which is the drawing's countries and nothing else. */
    private static List<String> regions() {
        List<String> names = new ArrayList<>();
        for (ArdaMap.Country country : ArdaMap.Country.values()) {
            names.add(id(country));
        }
        names.sort(String::compareTo);
        return names;
    }

    /** A country as it is typed: its own name, in the lower case a command word is written in. */
    private static String id(ArdaMap.Country country) {
        return country.name().toLowerCase(Locale.ROOT);
    }

    private static CompletableFuture<Suggestions> suggest(CommandContext<CommandSourceStack> context,
                                                          SuggestionsBuilder builder) {
        for (String name : regions()) {
            builder.suggest(name);
        }
        return builder.buildFuture();
    }

    /** Says what can be jumped to, since the names are the only part of this that has to be learned. */
    private static int list(CommandContext<CommandSourceStack> context) {
        List<String> names = regions();
        context.getSource().sendSuccess(() -> Component.literal("Arda's " + names.size()
                + " regions: " + String.join(", ", names)), false);
        return names.size();
    }

    /** Stands the caller in Arda, on the named region. */
    private static int go(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();
        String name = StringArgumentType.getString(context, "region").toLowerCase(Locale.ROOT);

        BlockPos locked = LOCKED.get(name);
        ArdaMap.Country country = null;
        for (ArdaMap.Country each : ArdaMap.Country.values()) {
            if (id(each).equals(name)) {
                country = each;
                break;
            }
        }
        if (country == null && locked == null) {
            source.sendFailure(Component.literal("No region called \"" + name + "\" - /tmtp list names them all."));
            return 0;
        }

        ServerLevel arda = source.getServer().getLevel(TolkienDimensions.ARDA);
        if (arda == null) {
            source.sendFailure(Component.literal("Arda is not loaded in this world."));
            return 0;
        }

        BlockPos point = locked != null ? locked : ArdaMap.anchor(country);
        int y = point.getY();
        if (locked == null) {
            // The anchor is a column of the drawing, not a point in the world: the ground under it is
            // the map's own height there, and a drawn sea's bed is under the water the caller wants to
            // look at, so the seas are stood over their water line instead.
            ArdaMap.Sample sample = ArdaMap.sample(point.getX(), point.getZ());
            int top = sample.height();
            if (!country.land()) {
                top = Math.max(top, sample.water());
            }
            y = top + CLEARANCE;
        }
        y = Mth.clamp(y, arda.getMinY() + 1, arda.getMaxY() - 1);

        // A dimension change is a teleport with the level named: the same call the ledger uses to put a
        // player back, so a caller in the overworld arrives in Arda rather than being refused.
        player.teleportTo(arda, point.getX() + 0.5D, y, point.getZ() + 0.5D,
                Set.of(), player.getYRot(), player.getXRot(), false);

        BlockPos landed = new BlockPos(point.getX(), y, point.getZ());
        String where = locked != null ? "a set point" : "the middle of the country";
        source.sendSuccess(() -> Component.literal("Arda: " + name + " at " + landed.toShortString()
                + ", " + where), false);
        return 1;
    }
}
