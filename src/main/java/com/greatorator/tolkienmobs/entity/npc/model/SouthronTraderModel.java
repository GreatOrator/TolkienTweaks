package com.greatorator.tolkienmobs.entity.npc.model;

import com.greatorator.tolkienmobs.entity.npc.SouthronTraderEntity;
import com.greatorator.tolkienmobs.entity.npc.render.SouthronTraderRender;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.model.data.EntityModelData;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class SouthronTraderModel extends GeoModel<SouthronTraderEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/merchant/southron.geo.json");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/merchant/southron.animation.json");

	@Override
	public ResourceLocation getModelResource(SouthronTraderEntity object, GeoRenderer<SouthronTraderEntity> renderer) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(SouthronTraderEntity object, GeoRenderer<SouthronTraderEntity> renderer) {
		return SouthronTraderRender.LOCATION_BY_VARIANT.get(object.getTolkienVariant());
	}

	@Override
	public ResourceLocation getAnimationResource(SouthronTraderEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(SouthronTraderEntity entity, long uniqueID, AnimationState<SouthronTraderEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);
		GeoBone head = this.getAnimationProcessor().getBone("head");
		assert customPredicate != null;
		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}