package com.greatorator.tolkienmobs.entity.passive.model;


import com.greatorator.tolkienmobs.entity.passive.MumakilEntity;
import com.greatorator.tolkienmobs.entity.passive.render.MumakilRender;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class MumakilModel extends GeoModel<MumakilEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "passive/mumakil");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "passive/mumakil");

	@Override
	public ResourceLocation getModelResource(MumakilEntity object, GeoRenderer<MumakilEntity> renderer) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(MumakilEntity object, GeoRenderer<MumakilEntity> renderer) {
		return MumakilRender.LOCATION_BY_VARIANT.get(object.getVariant());
	}

	@Override
	public ResourceLocation getAnimationResource(MumakilEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(MumakilEntity entity, long uniqueID, AnimationState<MumakilEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);
		GeoBone head = this.getAnimationProcessor().getBone("MumuNeck");
		assert customPredicate != null;
		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}