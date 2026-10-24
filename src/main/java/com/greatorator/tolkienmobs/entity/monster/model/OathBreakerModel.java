package com.greatorator.tolkienmobs.entity.monster.model;


import com.greatorator.tolkienmobs.entity.monster.OathBreakerEntity;
import com.greatorator.tolkienmobs.entity.monster.render.OathBreakerRender;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class OathBreakerModel extends GeoModel<OathBreakerEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "monster/base");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "monster/base");

	@Override
	public ResourceLocation getModelResource(OathBreakerEntity object, GeoRenderer<OathBreakerEntity> renderer) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(OathBreakerEntity object, GeoRenderer<OathBreakerEntity> renderer) {
		return OathBreakerRender.LOCATION_BY_VARIANT.get(object.getVariant());
	}

	@Override
	public ResourceLocation getAnimationResource(OathBreakerEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(OathBreakerEntity entity, long uniqueID, AnimationState<OathBreakerEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);
		GeoBone head = this.getAnimationProcessor().getBone("head");
		assert customPredicate != null;
		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}