package com.greatorator.tolkienmobs.entity.monster.model;


import com.greatorator.tolkienmobs.entity.monster.HuronEntity;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class HuronModel extends GeoModel<HuronEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "monster/huron");
	private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/huron.png");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "monster/huron");

	@Override
	public ResourceLocation getModelResource(HuronEntity object, GeoRenderer<HuronEntity> renderer) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(HuronEntity object, GeoRenderer<HuronEntity> renderer) {
		return this.texture;
	}

	@Override
	public ResourceLocation getAnimationResource(HuronEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(HuronEntity entity, long uniqueID, AnimationState<HuronEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);
		GeoBone head = this.getAnimationProcessor().getBone("head");
		assert customPredicate != null;
		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}