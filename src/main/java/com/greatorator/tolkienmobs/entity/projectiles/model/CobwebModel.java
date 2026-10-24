package com.greatorator.tolkienmobs.entity.projectiles.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.entity.projectiles.CobwebProjectileEntity;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/** The web shows one texture, so the model needs nothing from the render state. */
public class CobwebModel extends GeoModel<CobwebProjectileEntity> {
	private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "item/ammo_cobweb");
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(MODID, "textures/entity/ammo_cobweb.png");
	private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "ammo_cobweb");

	@Override
	public Identifier getModelResource(GeoRenderState renderState) {
		return MODEL;
	}

	@Override
	public Identifier getTextureResource(GeoRenderState renderState) {
		return TEXTURE;
	}

	@Override
	public Identifier getAnimationResource(CobwebProjectileEntity animatable) {
		return ANIMATIONS;
	}
}
