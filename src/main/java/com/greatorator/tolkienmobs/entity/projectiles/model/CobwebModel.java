package com.greatorator.tolkienmobs.entity.projectiles.model;


import com.greatorator.tolkienmobs.entity.projectiles.CobwebProjectileEntity;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class CobwebModel extends GeoModel<CobwebProjectileEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "item/ammo_cobweb");
	private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/ammo_cobweb.png");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "ammo_cobweb");

	@Override
	public ResourceLocation getModelResource(CobwebProjectileEntity object, GeoRenderer<CobwebProjectileEntity> renderer) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(CobwebProjectileEntity object, GeoRenderer<CobwebProjectileEntity> renderer) {
		return this.texture;
	}

	@Override
	public ResourceLocation getAnimationResource(CobwebProjectileEntity object) {
		return this.animations;
	}
}
