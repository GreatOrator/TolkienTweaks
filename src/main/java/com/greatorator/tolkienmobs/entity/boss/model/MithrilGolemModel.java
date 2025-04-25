package com.greatorator.tolkienmobs.entity.boss.model;


import com.greatorator.tolkienmobs.entity.boss.MithrilGolemEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class MithrilGolemModel extends GeoModel<MithrilGolemEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/monster/golem.geo.json");
	private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/elementalgolem/elemental_golem_mithril.png");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/monster/golem.animation.json");

	@Override
	public ResourceLocation getModelResource(MithrilGolemEntity object) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(MithrilGolemEntity object) {
		return this.texture;
	}

	@Override
	public ResourceLocation getAnimationResource(MithrilGolemEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(MithrilGolemEntity entity, long uniqueID, AnimationState<MithrilGolemEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);
		GeoBone head = this.getAnimationProcessor().getBone("head");
		assert customPredicate != null;
		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() * ((float) Math.PI / 180F));
	}
}