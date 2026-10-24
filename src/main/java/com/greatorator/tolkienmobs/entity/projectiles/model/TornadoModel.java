package com.greatorator.tolkienmobs.entity.projectiles.model;


import com.greatorator.tolkienmobs.entity.projectiles.TornadoEntity;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TornadoModel extends GeoModel<TornadoEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "item/ammo_tornado");
	private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/ammo_tornado.png");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "ammo_tornado");

	@Override
	public ResourceLocation getModelResource(TornadoEntity object, GeoRenderer<TornadoEntity> renderer) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(TornadoEntity object, GeoRenderer<TornadoEntity> renderer) {
		return this.texture;
	}

	@Override
	public ResourceLocation getAnimationResource(TornadoEntity object) {
		return this.animations;
	}
}