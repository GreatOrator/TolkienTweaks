package com.greatorator.tolkienmobs.item.custom.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.item.custom.CatalystItem;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/** The staff shows one texture, so the model needs nothing from the render state. */
public class CatalystModel extends GeoModel<CatalystItem> {
	private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "item/arda_staff");
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(MODID, "textures/item/tools/obj/arda_staff.png");
	private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "arda_staff");

	@Override
	public Identifier getModelResource(GeoRenderState renderState) {
		return MODEL;
	}

	@Override
	public Identifier getTextureResource(GeoRenderState renderState) {
		return TEXTURE;
	}

	@Override
	public Identifier getAnimationResource(CatalystItem animatable) {
		return ANIMATIONS;
	}
}
