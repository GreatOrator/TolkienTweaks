package com.greatorator.tolkienmobs.entity.npc.model;

import com.greatorator.tolkienmobs.entity.npc.HumanEntity;
import com.greatorator.tolkienmobs.entity.npc.render.HumanRender;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class HumanModel extends GeoModel<HumanEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "merchant/human");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "merchant/human");

	@Override
	public ResourceLocation getModelResource(HumanEntity object, GeoRenderer<HumanEntity> renderer) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(HumanEntity object, GeoRenderer<HumanEntity> renderer) {
		return HumanRender.getTexture(object);
	}

	@Override
	public ResourceLocation getAnimationResource(HumanEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(HumanEntity entity, long uniqueID, AnimationState<HumanEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);
		GeoBone head = this.getAnimationProcessor().getBone("head");
		assert customPredicate != null;
		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}