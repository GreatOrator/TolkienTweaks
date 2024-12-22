package com.greatorator.tolkienmobs.entity.passive.model;


import com.greatorator.tolkienmobs.entity.passive.MumakilEntity;
import com.greatorator.tolkienmobs.entity.passive.render.MumakilRender;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class MumakilModel extends GeoModel<MumakilEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/passive/mumakil.geo.json");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/passive/mumakil.animation.json");

	@Override
	public ResourceLocation getModelResource(MumakilEntity object) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(MumakilEntity object) {
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