package com.greatorator.tolkienmobs.entity.monster.model;


import com.greatorator.tolkienmobs.entity.monster.DuergarEntity;
import com.greatorator.tolkienmobs.entity.monster.render.DuergarRender;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class DuergarModel extends GeoModel<DuergarEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "monster/duergar");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "monster/duergar");

	@Override
	public ResourceLocation getModelResource(DuergarEntity object, GeoRenderer<DuergarEntity> renderer) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(DuergarEntity object, GeoRenderer<DuergarEntity> renderer) {
		return DuergarRender.LOCATION_BY_VARIANT.get(object.getVariant());
	}

	@Override
	public ResourceLocation getAnimationResource(DuergarEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(DuergarEntity entity, long uniqueID, AnimationState<DuergarEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);
		GeoBone head = this.getAnimationProcessor().getBone("head");
		assert customPredicate != null;
		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}