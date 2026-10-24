package com.greatorator.tolkienmobs.entity.monster.model;


import com.greatorator.tolkienmobs.entity.monster.MirkwoodSpiderEntity;
import com.greatorator.tolkienmobs.entity.monster.render.MirkwoodSpiderRender;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class MirkwoodSpiderModel extends GeoModel<MirkwoodSpiderEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "monster/mirkwoodspider");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "monster/mirkwoodspider");

	@Override
	public ResourceLocation getModelResource(MirkwoodSpiderEntity object, GeoRenderer<MirkwoodSpiderEntity> renderer) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(MirkwoodSpiderEntity object, GeoRenderer<MirkwoodSpiderEntity> renderer) {
		return MirkwoodSpiderRender.LOCATION_BY_VARIANT.get(object.getVariant());
	}

	@Override
	public ResourceLocation getAnimationResource(MirkwoodSpiderEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(MirkwoodSpiderEntity entity, long uniqueID, AnimationState<MirkwoodSpiderEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);
		GeoBone head = this.getAnimationProcessor().getBone("head");
		GeoBone webbing = this.getAnimationProcessor().getBone("webbing");

		webbing.setHidden(!entity.isWebShooting());

		assert customPredicate != null;
		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}