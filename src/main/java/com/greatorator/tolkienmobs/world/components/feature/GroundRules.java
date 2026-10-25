package com.greatorator.tolkienmobs.world.components.feature;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

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
                || state.is(TolkienBlocks.DARK_STONE.get());
    }
}
