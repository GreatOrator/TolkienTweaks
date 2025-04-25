package com.greatorator.tolkienmobs.entity.boss.model;


import com.greatorator.tolkienmobs.entity.boss.ShelobEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class ShelobModel extends GeoModel<ShelobEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/boss/shelob.geo.json");
	private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/tmshelob.png");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/boss/shelob.animation.json");

	@Override
	public ResourceLocation getModelResource(ShelobEntity object) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(ShelobEntity object) {
		return this.texture;
	}

	@Override
	public ResourceLocation getAnimationResource(ShelobEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(ShelobEntity entity, long uniqueID, AnimationState<ShelobEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);
		GeoBone head = this.getAnimationProcessor().getBone("head");
		assert customPredicate != null;
		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}