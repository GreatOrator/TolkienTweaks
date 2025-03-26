package com.greatorator.tolkienmobs.entity.passive.model;

import com.greatorator.tolkienmobs.entity.passive.GollumEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class GollumModel extends GeoModel<GollumEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/special/gollum.geo.json");
	private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/gollum.png");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/special/gollum.animation.json");

	@Override
	public ResourceLocation getModelResource(GollumEntity object) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(GollumEntity object) {
		return this.texture;
	}

	@Override
	public ResourceLocation getAnimationResource(GollumEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(GollumEntity entity, long uniqueID, AnimationState<GollumEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);
		GeoBone head = this.getAnimationProcessor().getBone("GollumHead");
		GeoBone eating = this.getAnimationProcessor().getBone("fish");

		eating.setHidden(!entity.isEating());

		assert customPredicate != null;
		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}