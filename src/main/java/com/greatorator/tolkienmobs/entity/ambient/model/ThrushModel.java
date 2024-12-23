package com.greatorator.tolkienmobs.entity.ambient.model;

import com.greatorator.tolkienmobs.entity.ambient.FrogEntity;
import com.greatorator.tolkienmobs.entity.ambient.ThrushEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class ThrushModel extends GeoModel<ThrushEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/ambient/thrush.geo.json");
	private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/birds/thrush.png");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/ambient/thrush.animation.json");

	@Override
	public ResourceLocation getModelResource(ThrushEntity object) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(ThrushEntity object) {
		return this.texture;
	}

	@Override
	public ResourceLocation getAnimationResource(ThrushEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(ThrushEntity entity, long uniqueID, AnimationState<ThrushEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);

		if (customPredicate == null) return;

		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		GeoBone head = this.getAnimationProcessor().getBone("head");
		GeoBone pecking = this.getAnimationProcessor().getBone("snail");
		GeoBone tamed = this.getAnimationProcessor().getBone("legband");

		pecking.setHidden(!entity.isPecking());
		tamed.setHidden(!entity.isTame());

		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}