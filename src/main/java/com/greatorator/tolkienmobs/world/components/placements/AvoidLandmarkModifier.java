package com.greatorator.tolkienmobs.world.components.placements;

import com.greatorator.tolkienmobs.init.TolkienFeatureModifiers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.stream.Stream;

public class AvoidLandmarkModifier extends PlacementModifier {

	public static final MapCodec<AvoidLandmarkModifier> CODEC = RecordCodecBuilder.<AvoidLandmarkModifier>mapCodec(instance -> instance.group(
		Codec.BOOL.fieldOf("occupies_surface").forGetter(o -> o.occupiesSurface),
		Codec.BOOL.fieldOf("occupies_underground").forGetter(o -> o.occupiesUnderground),
		Codec.INT.fieldOf("additional_clearance").forGetter(o -> o.additionalClearance)
	).apply(instance, AvoidLandmarkModifier::new)).validate(AvoidLandmarkModifier::validate);

	private final boolean occupiesSurface;
	private final boolean occupiesUnderground;
	private final int additionalClearance;

	public AvoidLandmarkModifier(boolean occupiesSurface, boolean occupiesUnderground, int additionalClearance) {
		this.occupiesSurface = occupiesSurface;
		this.occupiesUnderground = occupiesUnderground;
		this.additionalClearance = additionalClearance;
	}

	public static AvoidLandmarkModifier checkSurface() {
		return new AvoidLandmarkModifier(true, false, 0);
	}

	public static AvoidLandmarkModifier checkUnderground() {
		return new AvoidLandmarkModifier(false, true, 0);
	}

	public static AvoidLandmarkModifier checkBoth() {
		return new AvoidLandmarkModifier(true, true, 0);
	}

	/**
	 * The landmark clearance this modifier was written against is gone: it asked a structure start
	 * whether surface decoration was allowed, through a landmark system the mod no longer carries, so
	 * there is nothing left to consult and every place it is asked about keeps its position.
	 * <p>
	 * It used to answer with no positions at all, which emptied every placed feature that listed it -
	 * the fallen-leaf piles of all six woods and the wood and plant roots - while their biome
	 * attachments and their generated data stayed exactly as they were.
	 */
	@Override
	public Stream<BlockPos> getPositions(PlacementContext worldDecoratingHelper, RandomSource random, BlockPos blockPos) {
		return Stream.of(blockPos);
	}

	@Override
	public PlacementModifierType<?> type() {
		return TolkienFeatureModifiers.NO_STRUCTURE.get();
	}

	private static DataResult<AvoidLandmarkModifier> validate(AvoidLandmarkModifier config) {
		return config.occupiesSurface || config.occupiesUnderground ? DataResult.success(config) : DataResult.error(() -> "Feature Decorator cannot occupy neither surface nor underground");
	}
}
