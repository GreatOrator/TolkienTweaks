package com.greatorator.tolkienmobs.entity.monster.model;


import com.greatorator.tolkienmobs.entity.monster.SwampHagEntity;
import com.greatorator.tolkienmobs.entity.monster.render.SwampHagRender;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class SwampHagModel extends GeoModel<SwampHagEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "monster/swamphag");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "monster/swamphag");

	@Override
	public ResourceLocation getModelResource(SwampHagEntity object, GeoRenderer<SwampHagEntity> renderer) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(SwampHagEntity object, GeoRenderer<SwampHagEntity> renderer) {
		return SwampHagRender.LOCATION_BY_VARIANT.get(object.getVariant());
	}

	@Override
	public ResourceLocation getAnimationResource(SwampHagEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(SwampHagEntity entity, long uniqueID, AnimationState<SwampHagEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);
		GeoBone head = this.getAnimationProcessor().getBone("Head");
		assert customPredicate != null;
		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}