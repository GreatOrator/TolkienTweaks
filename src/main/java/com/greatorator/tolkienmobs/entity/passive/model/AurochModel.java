package com.greatorator.tolkienmobs.entity.passive.model;


import com.greatorator.tolkienmobs.entity.passive.AurochEntity;
import com.greatorator.tolkienmobs.entity.passive.render.AurochRender;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class AurochModel extends GeoModel<AurochEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "passive/auroch");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "passive/auroch");

	@Override
	public ResourceLocation getModelResource(AurochEntity object, GeoRenderer<AurochEntity> renderer) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(AurochEntity object, GeoRenderer<AurochEntity> renderer) {
		return AurochRender.LOCATION_BY_VARIANT.get(object.getVariant());
	}

	@Override
	public ResourceLocation getAnimationResource(AurochEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(AurochEntity entity, long uniqueID, AnimationState<AurochEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);
		GeoBone head = this.getAnimationProcessor().getBone("head");
		assert customPredicate != null;
		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}