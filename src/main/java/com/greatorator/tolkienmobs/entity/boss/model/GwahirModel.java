package com.greatorator.tolkienmobs.entity.boss.model;

import com.greatorator.tolkienmobs.entity.boss.GwahirEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class GwahirModel extends GeoModel<GwahirEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/special/eagle.geo.json");
	private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/birds/entitygreateagle.png");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/special/eagle.animation.json");

	@Override
	public ResourceLocation getModelResource(GwahirEntity object) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(GwahirEntity object) {
		return this.texture;
	}

	@Override
	public ResourceLocation getAnimationResource(GwahirEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(GwahirEntity entity, long uniqueID, AnimationState<GwahirEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);

		if (customPredicate == null) return;

		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		GeoBone head = this.getAnimationProcessor().getBone("neck");
		GeoBone king = this.getAnimationProcessor().getBone("crown");

		king.setHidden(false);

		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}