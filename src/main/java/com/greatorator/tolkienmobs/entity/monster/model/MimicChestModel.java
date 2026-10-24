package com.greatorator.tolkienmobs.entity.monster.model;


import com.greatorator.tolkienmobs.entity.monster.MimicChestEntity;
import com.greatorator.tolkienmobs.entity.monster.render.MimicChestRender;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class MimicChestModel extends GeoModel<MimicChestEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "monster/mimic");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "monster/mimic");

	@Override
	public ResourceLocation getModelResource(MimicChestEntity object, GeoRenderer<MimicChestEntity> renderer) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(MimicChestEntity object, GeoRenderer<MimicChestEntity> renderer) {
		return MimicChestRender.LOCATION_BY_VARIANT.get(object.getVariant());
	}

	@Override
	public ResourceLocation getAnimationResource(MimicChestEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(MimicChestEntity entity, long uniqueID, AnimationState<MimicChestEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);

		GeoBone active1 = this.getAnimationProcessor().getBone("MimicTongue");
		GeoBone active2 = this.getAnimationProcessor().getBone("leftLeg");
		GeoBone active3 = this.getAnimationProcessor().getBone("rightLeg");
		GeoBone active4 = this.getAnimationProcessor().getBone("leftbackLeg");
		GeoBone active5 = this.getAnimationProcessor().getBone("rightbackLeg");

		active1.setHidden(!entity.isActive());
		active2.setHidden(!entity.isActive());
		active3.setHidden(!entity.isActive());
		active4.setHidden(!entity.isActive());
		active5.setHidden(!entity.isActive());
	}
}