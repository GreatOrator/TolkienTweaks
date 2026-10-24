package com.greatorator.tolkienmobs.entity.projectiles.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.entity.projectiles.TornadoEntity;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/** The funnel shows one texture, so the model needs nothing from the render state. */
public class TornadoModel extends GeoModel<TornadoEntity> {
	private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "item/ammo_tornado");
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(MODID, "textures/entity/ammo_tornado.png");
	private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "ammo_tornado");

	@Override
	public Identifier getModelResource(GeoRenderState renderState) {
		return MODEL;
	}

	@Override
	public Identifier getTextureResource(GeoRenderState renderState) {
		return TEXTURE;
	}

	@Override
	public Identifier getAnimationResource(TornadoEntity animatable) {
		return ANIMATIONS;
	}
}
