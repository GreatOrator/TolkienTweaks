package com.greatorator.tolkienmobs.entity.ambient.model;// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.greatorator.tolkienmobs.entity.ambient.CrebainEntity;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class CrebainModel extends GeoModel<CrebainEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "ambient/crebain");
	private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/birds/crebain.png");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "ambient/crebain");

	@Override
	public ResourceLocation getModelResource(CrebainEntity object, GeoRenderer<CrebainEntity> renderer) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(CrebainEntity object, GeoRenderer<CrebainEntity> renderer) {
		return this.texture;
	}

	@Override
	public ResourceLocation getAnimationResource(CrebainEntity object) {
		return this.animations;
	}
}