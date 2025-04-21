package com.greatorator.tolkienmobs.world.components.layer;

public interface BigContext<R extends Area> extends Context {
	R createResult(Area transformer);

	R createResult(Area transformer, R layer);

	R createResult(Area transformer, R layer1, R layer2);
}