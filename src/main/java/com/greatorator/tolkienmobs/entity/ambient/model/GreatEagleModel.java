package com.greatorator.tolkienmobs.entity.ambient.model;

import com.greatorator.tolkienmobs.entity.ambient.GreatEagleEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class GreatEagleModel extends GeoModel<GreatEagleEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/special/eagle.geo.json");
	private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/birds/entitygreateagle.png");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/special/eagle.animation.json");

	@Override
	public ResourceLocation getModelResource(GreatEagleEntity object) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(GreatEagleEntity object) {
		return this.texture;
	}

	@Override
	public ResourceLocation getAnimationResource(GreatEagleEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(GreatEagleEntity entity, long uniqueID, AnimationState<GreatEagleEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);

		if (customPredicate == null) return;

		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		GeoBone head = this.getAnimationProcessor().getBone("neck");
		GeoBone king = this.getAnimationProcessor().getBone("crown");

		king.setHidden(true);

		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}