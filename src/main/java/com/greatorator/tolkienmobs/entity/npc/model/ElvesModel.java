package com.greatorator.tolkienmobs.entity.npc.model;

import com.greatorator.tolkienmobs.entity.npc.ElvesEntity;
import com.greatorator.tolkienmobs.entity.npc.render.ElvesRender;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class ElvesModel extends GeoModel<ElvesEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "merchant/elf");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "merchant/elf");

	@Override
	public ResourceLocation getModelResource(ElvesEntity object, GeoRenderer<ElvesEntity> renderer) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(ElvesEntity object, GeoRenderer<ElvesEntity> renderer) {
		return ElvesRender.LOCATION_BY_VARIANT.get(object.getTolkienVariant());
	}

	@Override
	public ResourceLocation getAnimationResource(ElvesEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(ElvesEntity entity, long uniqueID, AnimationState<ElvesEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);
		GeoBone head = this.getAnimationProcessor().getBone("head");
		assert customPredicate != null;
		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}