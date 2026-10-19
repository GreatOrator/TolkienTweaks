package com.greatorator.tolkienmobs.world.components.feature;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class WebFeature extends Feature<NoneFeatureConfiguration> {

	/** How far below a column's surface to look for branches: the mirkwood trunks run 10-20 tall. */
	private static final int WEBS_DEPTH = 24;

	public WebFeature(Codec<NoneFeatureConfiguration> config) {
		super(config);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featurePlaceContext)
	{
		WorldGenLevel worldIn = featurePlaceContext.level();
		ChunkGenerator chunkGenerator = featurePlaceContext.chunkGenerator();
		RandomSource rand = featurePlaceContext.random();
		BlockPos pos = featurePlaceContext.origin();
		NoneFeatureConfiguration config = featurePlaceContext.config();
		int i = 0;
		int j = rand.nextInt(8 - 2) + 2;

		for (int k = pos.getX() - j; k <= pos.getX() + j; ++k)
		{
			for (int l = pos.getZ() - j; l <= pos.getZ() + j; ++l)
			{
				int i1 = k - pos.getX();
				int j1 = l - pos.getZ();
				if (i1 * i1 + j1 * j1 <= j * j)
				{
					// The origin is the top of the highest block in the column, which under a canopy is
					// the top of the tree, so start at a random height inside the tree and walk down.
					// Webs end up strung through the branches at varying heights instead of all
					// collecting at one level, and since the scan begins below that top block it never
					// picks the air block sitting directly on the grass.
					int scanTop = pos.getY() - 1 - rand.nextInt(WEBS_DEPTH);

					for (int k1 = scanTop; k1 >= pos.getY() - 1 - WEBS_DEPTH; --k1)
					{
						BlockPos blockpos = new BlockPos(k, k1, l);
						BlockState blockstate = worldIn.getBlockState(blockpos);
						BlockState webbingstate = Blocks.COBWEB.defaultBlockState();

						int faces = 0;

						for (Direction direction : Direction.values())
						{
							BlockState blockstate1 = worldIn.getBlockState(blockpos.relative(direction));
							if (blockstate1.is(Blocks.GRASS_BLOCK) || blockstate1.is(TolkienBlocks.WOOD_MIRKWOOD.get()) || blockstate1.is(TolkienBlocks.HARDENED_LEAVES_MIRKWOOD.get()) || blockstate1.is(Blocks.GRANITE) || blockstate1.is(Blocks.DRIPSTONE_BLOCK) || blockstate1.is(Blocks.CALCITE) || blockstate1.is(Blocks.TUFF) || blockstate1.is(Blocks.DEEPSLATE))
							{
								faces++;
							}
						}

						if (blockstate.isAir() && faces > 0)
						{
							worldIn.setBlock(blockpos, webbingstate, 2);

							++i;
							break;
						}
					}
				}
			}
		}

		return i > 0;
	}
}
