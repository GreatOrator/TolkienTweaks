package com.greatorator.tolkienmobs.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.ToIntBiFunction;

/**
 * The colour a leaf pile's fallen leaves are drawn in, which only the client can answer: it is the
 * tint oak leaves give at that spot, and the block tint list that answers it is a client registry.
 * <p>
 * The block itself must not name any of that. A dedicated server has no client classes at all, and
 * the JVM loads every type a method body mentions while it verifies the class - so a leaf pile that
 * asked the client registry directly failed to even register on a server, taking the whole mod's
 * block registration down with it. The block now asks this holder, the client installs the real
 * lookup during client setup, and on a server the holder simply answers "no tint".
 */
public final class LeafPileTint {
    /** The old "no tint" answer, and what a server gives since it has no colours at all. */
    public static final int NO_TINT = -1;

    @Nullable
    private static ToIntBiFunction<Level, BlockPos> source;

    private LeafPileTint() {
    }

    /** Called once, from the client's setup; a server never calls it and never needs to. */
    public static void install(ToIntBiFunction<Level, BlockPos> lookup) {
        source = lookup;
    }

    public static int colour(Level level, BlockPos pos) {
        ToIntBiFunction<Level, BlockPos> lookup = source;
        return lookup == null ? NO_TINT : lookup.applyAsInt(level, pos);
    }
}
