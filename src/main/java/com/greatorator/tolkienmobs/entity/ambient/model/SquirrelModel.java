package com.greatorator.tolkienmobs.entity.ambient.model;// Made with Blockbench 4.11.1
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.greatorator.tolkienmobs.entity.ambient.SquirrelEntity;
import com.greatorator.tolkienmobs.entity.ambient.render.FrogRender;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class SquirrelModel extends GeoModel<SquirrelEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/ambient/squirrel.geo.json");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/ambient/squirrel.animation.json");

	@Override
	public ResourceLocation getModelResource(SquirrelEntity object) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(SquirrelEntity object) {
		return FrogRender.LOCATION_BY_VARIANT.get(object.getVariant());
	}

	@Override
	public ResourceLocation getAnimationResource(SquirrelEntity object) {
		return this.animations;
	}
}