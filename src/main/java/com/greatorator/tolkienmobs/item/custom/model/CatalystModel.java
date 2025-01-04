package com.greatorator.tolkienmobs.item.custom.model;

import com.greatorator.tolkienmobs.item.custom.CatalystItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class CatalystModel extends GeoModel<CatalystItem> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/item/arda_staff.geo.json");
	private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/item/tools/obj/arda_staff.png");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/arda_staff.animation.json");

	@Override
	public ResourceLocation getModelResource(CatalystItem object) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(CatalystItem object) {
		return this.texture;
	}

	@Override
	public ResourceLocation getAnimationResource(CatalystItem object) {
		return this.animations;
	}
}