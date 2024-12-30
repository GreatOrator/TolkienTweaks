package com.greatorator.tolkienmobs.entity.monster.model;


import com.greatorator.tolkienmobs.entity.monster.MinotaurEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class MinotaurModel extends GeoModel<MinotaurEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/monster/minotaur.geo.json");
	private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/minotaur.png");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/monster/minotaur.animation.json");

	@Override
	public ResourceLocation getModelResource(MinotaurEntity object) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(MinotaurEntity object) {
		return this.texture;
	}

	@Override
	public ResourceLocation getAnimationResource(MinotaurEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(MinotaurEntity entity, long uniqueID, AnimationState<MinotaurEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);
		GeoBone head = this.getAnimationProcessor().getBone("head");
		assert customPredicate != null;
		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}