package com.greatorator.tolkienmobs.entity.monster.model;


import com.greatorator.tolkienmobs.entity.monster.TreeEntEntity;
import com.greatorator.tolkienmobs.entity.monster.render.TreeEntRender;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TreeEntModel extends GeoModel<TreeEntEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "monster/treeent");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "monster/treeent");

	@Override
	public ResourceLocation getModelResource(TreeEntEntity object, GeoRenderer<TreeEntEntity> renderer) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(TreeEntEntity object, GeoRenderer<TreeEntEntity> renderer) {
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