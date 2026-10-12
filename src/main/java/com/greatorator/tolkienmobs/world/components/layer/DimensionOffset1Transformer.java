package com.greatorator.tolkienmobs.world.components.layer;

public interface DimensionOffset1Transformer extends DimensionTransformer {
	@Override
	default int getParentX(int x) {
		return x - 1;
	}

	@Override
	default int getParentY(int z) {
		return z - 1;
	}
}
