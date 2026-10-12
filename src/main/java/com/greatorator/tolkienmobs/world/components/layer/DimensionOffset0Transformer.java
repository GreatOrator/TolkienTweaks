package com.greatorator.tolkienmobs.world.components.layer;

public interface DimensionOffset0Transformer extends DimensionTransformer {
	@Override
	default int getParentX(int x) {
		return x;
	}

	@Override
	default int getParentY(int z) {
		return z;
	}
}
