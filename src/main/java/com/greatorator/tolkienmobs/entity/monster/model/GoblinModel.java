package com.greatorator.tolkienmobs.entity.monster.model;


import com.greatorator.tolkienmobs.entity.monster.GoblinEntity;
import com.greatorator.tolkienmobs.entity.monster.render.GoblinRender;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class GoblinModel extends GeoModel<GoblinEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "monster/goblin");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "monster/goblin");

	@Override
	public ResourceLocation getModelResource(GoblinEntity object, GeoRenderer<GoblinEntity> renderer) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(GoblinEntity object, GeoRenderer<GoblinEntity> renderer) {
		return GoblinRender.LOCATION_BY_VARIANT.get(object.getVariant());
	}

	@Override
	public ResourceLocation getAnimationResource(GoblinEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(GoblinEntity entity, long uniqueID, AnimationState<GoblinEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);
		GeoBone head = this.getAnimationProcessor().getBone("head");
		assert customPredicate != null;
		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}