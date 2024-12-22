package com.greatorator.tolkienmobs.entity.ambient.model;


import com.greatorator.tolkienmobs.entity.ambient.FrogEntity;
import com.greatorator.tolkienmobs.entity.ambient.render.FrogRender;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class FrogModel extends GeoModel<FrogEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/ambient/frog.geo.json");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/ambient/frog.animation.json");

	@Override
	public ResourceLocation getModelResource(FrogEntity object) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(FrogEntity object) {
		return FrogRender.LOCATION_BY_VARIANT.get(object.getVariant());
	}

	@Override
	public ResourceLocation getAnimationResource(FrogEntity object) {
		return this.animations;
	}
}