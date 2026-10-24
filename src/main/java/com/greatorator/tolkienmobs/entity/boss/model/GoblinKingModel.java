package com.greatorator.tolkienmobs.entity.boss.model;


import com.greatorator.tolkienmobs.entity.boss.GoblinKingEntity;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class GoblinKingModel extends GeoModel<GoblinKingEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "boss/goblinking");
	private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/goblin/goblinking.png");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "boss/goblinking");

	@Override
	public ResourceLocation getModelResource(GoblinKingEntity object, GeoRenderer<GoblinKingEntity> renderer) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(GoblinKingEntity object, GeoRenderer<GoblinKingEntity> renderer) {
		return this.texture;
	}

	@Override
	public ResourceLocation getAnimationResource(GoblinKingEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(GoblinKingEntity entity, long uniqueID, AnimationState<GoblinKingEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);
		GeoBone head = this.getAnimationProcessor().getBone("head");
		assert customPredicate != null;
		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}