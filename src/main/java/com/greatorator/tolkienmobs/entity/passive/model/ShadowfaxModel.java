package com.greatorator.tolkienmobs.entity.passive.model;


import com.greatorator.tolkienmobs.entity.passive.ShadowfaxEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class ShadowfaxModel extends GeoModel<ShadowfaxEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/special/shadowfax.geo.json");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/special/horse.animation.json");

	@Override
	public ResourceLocation getModelResource(ShadowfaxEntity object) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(ShadowfaxEntity object) {
		return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/horse/shadowfax.png");
	}

	@Override
	public ResourceLocation getAnimationResource(ShadowfaxEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(ShadowfaxEntity entity, long uniqueID, AnimationState<ShadowfaxEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);
		GeoBone head = this.getAnimationProcessor().getBone("head");
		GeoBone chested = this.getAnimationProcessor().getBone("chest");
		GeoBone saddled2 = this.getAnimationProcessor().getBone("saddle");
		GeoBone saddled = this.getAnimationProcessor().getBone("headpiece");
		assert customPredicate != null;

		saddled.setHidden(true);
		saddled2.setHidden(true);
		chested.setHidden(true);

		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}