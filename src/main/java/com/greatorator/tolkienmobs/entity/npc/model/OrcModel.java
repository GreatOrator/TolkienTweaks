package com.greatorator.tolkienmobs.entity.npc.model;

import com.greatorator.tolkienmobs.entity.npc.OrcEntity;
import com.greatorator.tolkienmobs.entity.npc.render.OrcRender;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class OrcModel extends GeoModel<OrcEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "merchant/orc");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "merchant/orc");

	@Override
	public ResourceLocation getModelResource(OrcEntity object, GeoRenderer<OrcEntity> renderer) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(OrcEntity object, GeoRenderer<OrcEntity> renderer) {
		return OrcRender.LOCATION_BY_VARIANT.get(object.getTolkienVariant());
	}

	@Override
	public ResourceLocation getAnimationResource(OrcEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(OrcEntity entity, long uniqueID, AnimationState<OrcEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);
		GeoBone head = this.getAnimationProcessor().getBone("head");
		assert customPredicate != null;
		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}
