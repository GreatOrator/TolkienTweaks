package com.greatorator.tolkienmobs.world.tree;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.world.tree.placers.TreeRootsDecorator;
import com.greatorator.tolkienmobs.world.tree.placers.TrunkSideDecorator;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

public class TolkienDecorators {
    public static final BlockStateProvider ROOT_BLEND_PROVIDER = new WeightedStateProvider(new SimpleWeightedRandomList.Builder<BlockState>().add(Blocks.MANGROVE_ROOTS.defaultBlockState(), 6).add(Blocks.MANGROVE_ROOTS.defaultBlockState(), 1).build());
    public static final TreeRootsDecorator LIVING_ROOTS = new TreeRootsDecorator(3, 1, 5, TolkienDecorators.ROOT_BLEND_PROVIDER, 1);
//    public static final TrunkSideDecorator LIGHTNINGBUG = new TrunkSideDecorator(2, 1.0f, BlockStateProvider.simple(TolkienBlocks.LIGHTNINGBUG_BLOCK.get().defaultBlockState()));
}