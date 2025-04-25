package com.greatorator.tolkienmobs.entity.boss.model;


import com.greatorator.tolkienmobs.entity.boss.WatcherEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class WatcherModel extends GeoModel<WatcherEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/boss/tmwatcher.geo.json");
	private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/tmwatcher.png");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/boss/tmwatcher.animation.json");

	@Override
	public ResourceLocation getModelResource(WatcherEntity object) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(WatcherEntity object) {
		return this.texture;
	}

	@Override
	public ResourceLocation getAnimationResource(WatcherEntity object) {
		return this.animations;
	}
}