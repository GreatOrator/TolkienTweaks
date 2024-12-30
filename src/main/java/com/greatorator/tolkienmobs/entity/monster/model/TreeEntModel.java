package com.greatorator.tolkienmobs.entity.monster.model;


import com.greatorator.tolkienmobs.entity.monster.TreeEntEntity;
import com.greatorator.tolkienmobs.entity.monster.render.TreeEntRender;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TreeEntModel extends GeoModel<TreeEntEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/monster/treeent.geo.json");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/monster/treeent.animation.json");

	@Override
	public ResourceLocation getModelResource(TreeEntEntity object) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(TreeEntEntity object) {
		return TreeEntRender.LOCATION_BY_VARIANT.get(object.getVariant());
	}

	@Override
	public ResourceLocation getAnimationResource(TreeEntEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(TreeEntEntity entity, long uniqueID, AnimationState<TreeEntEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);
		GeoBone head = this.getAnimationProcessor().getBone("head");
		GeoBone ranged = this.getAnimationProcessor().getBone("boulder");
		assert customPredicate != null;
		ranged.setHidden(!entity.getRanged());
		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}