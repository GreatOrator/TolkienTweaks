package com.greatorator.tolkienmobs.entity.boss.model;


import com.greatorator.tolkienmobs.entity.boss.FellBeastEntity;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class FellBeastModel extends GeoModel<FellBeastEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "boss/fellbeast");
	private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/fellbeast/fellbeast.png");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "boss/fellbeast");

	@Override
	public ResourceLocation getModelResource(FellBeastEntity object, GeoRenderer<FellBeastEntity> renderer) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(FellBeastEntity object, GeoRenderer<FellBeastEntity> renderer) {
		return this.texture;
	}

	@Override
	public ResourceLocation getAnimationResource(FellBeastEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(FellBeastEntity entity, long uniqueID, AnimationState<FellBeastEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);
		GeoBone head = this.getAnimationProcessor().getBone("head");
		assert customPredicate != null;
		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}