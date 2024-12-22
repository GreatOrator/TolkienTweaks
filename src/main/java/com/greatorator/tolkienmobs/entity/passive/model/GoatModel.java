package com.greatorator.tolkienmobs.entity.passive.model;


import com.greatorator.tolkienmobs.entity.passive.GoatEntity;
import com.greatorator.tolkienmobs.entity.passive.render.GoatRender;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class GoatModel extends GeoModel<GoatEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/passive/goat.geo.json");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/passive/goat.animation.json");

	@Override
	public ResourceLocation getModelResource(GoatEntity object) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(GoatEntity object) {
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
		assert customPredicate != null;
		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}