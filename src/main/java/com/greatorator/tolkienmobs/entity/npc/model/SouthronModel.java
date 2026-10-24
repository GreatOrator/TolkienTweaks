package com.greatorator.tolkienmobs.entity.npc.model;

import com.greatorator.tolkienmobs.entity.npc.SouthronEntity;
import com.greatorator.tolkienmobs.entity.npc.render.SouthronRender;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class SouthronModel extends GeoModel<SouthronEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "merchant/southron");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "merchant/southron");

	@Override
	public ResourceLocation getModelResource(SouthronEntity object, GeoRenderer<SouthronEntity> renderer) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(SouthronEntity object, GeoRenderer<SouthronEntity> renderer) {
		return SouthronRender.LOCATION_BY_VARIANT.get(object.getTolkienVariant());
	}

	@Override
	public ResourceLocation getAnimationResource(SouthronEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(SouthronEntity entity, long uniqueID, AnimationState<SouthronEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);
		GeoBone head = this.getAnimationProcessor().getBone("head");
		assert customPredicate != null;
		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}
