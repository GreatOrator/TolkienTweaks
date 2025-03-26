package com.greatorator.tolkienmobs.entity.monster.model;


import com.greatorator.tolkienmobs.entity.monster.MimicChestEntity;
import com.greatorator.tolkienmobs.entity.monster.render.MimicChestRender;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class MimicChestModel extends GeoModel<MimicChestEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/monster/mimic.geo.json");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/monster/mimic.animation.json");

	@Override
	public ResourceLocation getModelResource(MimicChestEntity object) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(MimicChestEntity object) {
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