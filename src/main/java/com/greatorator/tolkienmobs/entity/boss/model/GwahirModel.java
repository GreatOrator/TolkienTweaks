package com.greatorator.tolkienmobs.entity.boss.model;

import com.greatorator.tolkienmobs.entity.boss.GwahirEntity;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class GwahirModel extends GeoModel<GwahirEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "special/eagle");
	private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/birds/entitygreateagle.png");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "special/eagle");

	@Override
	public ResourceLocation getModelResource(GwahirEntity object, GeoRenderer<GwahirEntity> renderer) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(GwahirEntity object, GeoRenderer<GwahirEntity> renderer) {
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