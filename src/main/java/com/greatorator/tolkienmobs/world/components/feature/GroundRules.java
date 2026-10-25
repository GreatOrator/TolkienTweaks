package com.greatorator.tolkienmobs.world.components.feature;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

/**
 * What counts as ground for the loose surface decorations - rubble, rock piles, stone spikes and the
 * dead trees of Mordor.
 * <p>
 * These features used to ask for {@code minecraft:grass_block} alone, which is the surface of a
 * temperate forest and none of Mordor: its floor is dark stone, so every one of them refused to
 * place there. Bare rock is ground too.
 */
public final class GroundRules {
    private GroundRules() {
    }

    /** How far under a column's surface the floor of a leaf pile may be looked for. */
    private static final int CANOPY_DEPTH = 48;

    /**
     * The floor a fallen-leaf pile lies on. A wood's surface heightmap counts the leaves above it, so
     * in the middle of one its answer sits on the tops of the trees and the soil test under it fails;
     * this walks down the column to the first cell with room and soil - or water - beneath it, which
     * is where a pile lies in a clearing, on open ground and under a tree alike.
     */
    @Nullable
    public static BlockPos leafPileFloor(WorldGenLevel level, BlockPos from) {
        BlockPos.MutableBlockPos pos = from.mutable();
        for (int step = 0; step < CANOPY_DEPTH && pos.getY() > level.getMinY(); step++, pos.move(0, -1, 0)) {
            if (!level.isEmptyBlock(pos) && !level.getBlockState(pos).canBeReplaced()) {
                continue;
            }
            BlockState floor = level.getBlockState(pos.below());
            if (floor.is(BlockTags.DIRT) || level.getFluidState(pos.below()).is(Fluids.WATER)) {
                return pos.immutable();
            }
        }
        return null;
    }

    public static boolean isBareGround(BlockState state) {
        return state.is(BlockTags.DIRT)
                || state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.PODZOL)
                || state.is(Blocks.MYCELIUM)
                || state.is(Blocks.STONE)
                || state.is(Blocks.COBBLESTONE)
                || state.is(Blocks.GRAVEL)
                || state.is(Blocks.ANDESITE)
                || state.is(Blocks.DIORITE)
                || state.is(Blocks.GRANITE)
                || state.is(TolkienBlocks.DARK_STONE.get())
                // Mordor's and the Ash Mountains' surface rules lay these two down wherever their
                // own stone noise says so, so the same biome can have dark stone, cobbled dark stone
                // or smooth dark stone on top from one column to the next. Only the plain one
                // counted as ground, which quietly skipped every column the other two finished.
                || state.is(TolkienBlocks.COBBLED_DARK_STONE.get())
                || state.is(TolkienBlocks.SMOOTH_DARK_STONE.get());
    }
}
