package com.greatorator.tolkienmobs.entity.passive.model;


import com.greatorator.tolkienmobs.entity.passive.GoatEntity;
import com.greatorator.tolkienmobs.entity.passive.render.GoatRender;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class GoatModel extends GeoModel<GoatEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "passive/goat");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "passive/goat");

	@Override
	public ResourceLocation getModelResource(GoatEntity object, GeoRenderer<GoatEntity> renderer) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(GoatEntity object, GeoRenderer<GoatEntity> renderer) {
		return GoatRender.LOCATION_BY_VARIANT.get(object.getVariant());
	}

	@Override
	public ResourceLocation getAnimationResource(GoatEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(GoatEntity entity, long uniqueID, AnimationState<GoatEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);
		GeoBone head = this.getAnimationProcessor().getBone("head");
		GeoBone chested = this.getAnimationProcessor().getBone("chest");
		GeoBone saddled = this.getAnimationProcessor().getBone("Saddle");
		assert customPredicate != null;

		saddled.setHidden(!entity.isSaddled());
		chested.setHidden(!entity.hasChest());

		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}