package com.greatorator.tolkienmobs.entity.monster.model;


import com.greatorator.tolkienmobs.entity.monster.HaradrimEntity;
import com.greatorator.tolkienmobs.entity.monster.render.HaradrimRender;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class HaradrimModel extends GeoModel<HaradrimEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "monster/base");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "monster/base");

	@Override
	public ResourceLocation getModelResource(HaradrimEntity object, GeoRenderer<HaradrimEntity> renderer) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(HaradrimEntity object, GeoRenderer<HaradrimEntity> renderer) {
		return HaradrimRender.LOCATION_BY_VARIANT.get(object.getVariant());
	}

	@Override
	public ResourceLocation getAnimationResource(HaradrimEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(HaradrimEntity entity, long uniqueID, AnimationState<HaradrimEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);
		GeoBone head = this.getAnimationProcessor().getBone("head");
		assert customPredicate != null;
		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}