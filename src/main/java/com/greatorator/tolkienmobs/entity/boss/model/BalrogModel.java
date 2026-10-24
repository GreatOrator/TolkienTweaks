package com.greatorator.tolkienmobs.entity.boss.model;


import com.greatorator.tolkienmobs.entity.boss.BalrogEntity;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class BalrogModel extends GeoModel<BalrogEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "boss/balrog");
	private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/balrog.png");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "boss/balrog");

	@Override
	public ResourceLocation getModelResource(BalrogEntity object, GeoRenderer<BalrogEntity> renderer) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(BalrogEntity object, GeoRenderer<BalrogEntity> renderer) {
		return this.texture;
	}

	@Override
	public ResourceLocation getAnimationResource(BalrogEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(BalrogEntity entity, long uniqueID, AnimationState<BalrogEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);
		GeoBone head = this.getAnimationProcessor().getBone("head");
		assert customPredicate != null;
		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}