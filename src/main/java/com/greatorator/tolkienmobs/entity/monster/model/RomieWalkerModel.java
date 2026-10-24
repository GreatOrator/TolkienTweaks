package com.greatorator.tolkienmobs.entity.monster.model;


import com.greatorator.tolkienmobs.entity.monster.RomieWalkerEntity;
import com.greatorator.tolkienmobs.entity.monster.render.RomieWalkerRender;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class RomieWalkerModel extends GeoModel<RomieWalkerEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "monster/base");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "monster/base");

	@Override
	public ResourceLocation getModelResource(RomieWalkerEntity object, GeoRenderer<RomieWalkerEntity> renderer) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(RomieWalkerEntity object, GeoRenderer<RomieWalkerEntity> renderer) {
		return RomieWalkerRender.LOCATION_BY_VARIANT.get(object.getVariant());
	}

	@Override
	public ResourceLocation getAnimationResource(RomieWalkerEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(RomieWalkerEntity entity, long uniqueID, AnimationState<RomieWalkerEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);
		GeoBone head = this.getAnimationProcessor().getBone("head");
		assert customPredicate != null;
		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}