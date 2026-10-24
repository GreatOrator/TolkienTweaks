package com.greatorator.tolkienmobs.entity.boss.model;


import com.greatorator.tolkienmobs.entity.boss.WatcherEntity;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class WatcherModel extends GeoModel<WatcherEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "boss/tmwatcher");
	private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/tmwatcher.png");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "boss/tmwatcher");

	@Override
	public ResourceLocation getModelResource(WatcherEntity object, GeoRenderer<WatcherEntity> renderer) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(WatcherEntity object, GeoRenderer<WatcherEntity> renderer) {
		return this.texture;
	}

	@Override
	public ResourceLocation getAnimationResource(WatcherEntity object) {
		return this.animations;
	}
}