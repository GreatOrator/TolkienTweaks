package com.greatorator.tolkienmobs.entity.monster.model;


import com.greatorator.tolkienmobs.entity.monster.EnragedFellSpiritEntity;
import com.greatorator.tolkienmobs.entity.monster.render.EnragedFellSpiritRender;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class EnragedFellSpiritModel extends GeoModel<EnragedFellSpiritEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/monster/base.geo.json");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/monster/base.animation.json");

	@Override
	public ResourceLocation getModelResource(EnragedFellSpiritEntity object) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(EnragedFellSpiritEntity object) {
		return EnragedFellSpiritRender.LOCATION_BY_VARIANT.get(object.getVariant());
	}

	@Override
	public ResourceLocation getAnimationResource(EnragedFellSpiritEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(EnragedFellSpiritEntity entity, long uniqueID, AnimationState<EnragedFellSpiritEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);
		GeoBone head = this.getAnimationProcessor().getBone("head");
		assert customPredicate != null;
		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}