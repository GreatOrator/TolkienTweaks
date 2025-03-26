package com.greatorator.tolkienmobs.entity.monster.model;


import com.greatorator.tolkienmobs.entity.monster.MirkwoodSpiderEntity;
import com.greatorator.tolkienmobs.entity.monster.render.MirkwoodSpiderRender;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class MirkwoodSpiderModel extends GeoModel<MirkwoodSpiderEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/monster/mirkwoodspider.geo.json");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/monster/mirkwoodspider.animation.json");

	@Override
	public ResourceLocation getModelResource(MirkwoodSpiderEntity object) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(MirkwoodSpiderEntity object) {
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