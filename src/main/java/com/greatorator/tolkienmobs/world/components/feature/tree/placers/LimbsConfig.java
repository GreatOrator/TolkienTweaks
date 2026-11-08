package com.greatorator.tolkienmobs.world.components.feature.tree.placers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

/**
 * Where a tree's limbs leave the bole, how many there are and how far they reach.
 * <p>
 * The same shape {@link BranchesConfig} keeps for the branching placers: the trunk's own height stays with the trunk
 * placer, and everything about the limbs travels in one piece, so a tree's shape is one number of wood and one set of
 * limbs rather than a dozen numbers strung along the trunk placer's codec.
 *
 * @param limbProvider     what a limb is made of
 * @param count            how many limbs a tree has at the fewest
 * @param randomAddCount   how many more it may have
 * @param start            how far up the bole the first limb leaves, as a fraction of the trunk's height
 * @param length           how far a limb reaches
 * @param randomAddLength  how much further a limb may reach
 * @param pitch            how far a limb leans out of the vertical: nought is straight up, a half is straight out
 */
public record LimbsConfig(BlockStateProvider limbProvider, int count, int randomAddCount, double start,
                          double length, double randomAddLength, double pitch) {
    public static final Codec<LimbsConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BlockStateProvider.CODEC.fieldOf("limb_provider").forGetter(LimbsConfig::limbProvider),
                    Codec.intRange(0, 16).fieldOf("count_minimum").forGetter(LimbsConfig::count),
                    Codec.intRange(0, 16).fieldOf("random_add_count").orElse(0).forGetter(LimbsConfig::randomAddCount),
                    Codec.doubleRange(0d, 1d).fieldOf("start").forGetter(LimbsConfig::start),
                    Codec.doubleRange(1d, 24d).fieldOf("length").forGetter(LimbsConfig::length),
                    Codec.doubleRange(0d, 16d).fieldOf("random_add_length").orElse(0d).forGetter(LimbsConfig::randomAddLength),
                    Codec.doubleRange(0d, 1d).fieldOf("pitch").orElse(0.3d).forGetter(LimbsConfig::pitch)
            ).apply(instance, LimbsConfig::new));
}
