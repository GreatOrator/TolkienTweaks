package com.greatorator.tolkienmobs.entity.passive.model;


import com.greatorator.tolkienmobs.entity.passive.IstariEntity;
import com.greatorator.tolkienmobs.entity.passive.render.IstariRender;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class IstariModel extends GeoModel<IstariEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "special/istari");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "special/istari");

	@Override
	public ResourceLocation getModelResource(IstariEntity object, GeoRenderer<IstariEntity> renderer) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(IstariEntity object, GeoRenderer<IstariEntity> renderer) {
		return IstariRender.LOCATION_BY_VARIANT.get(object.getVariant());
	}

	@Override
	public ResourceLocation getAnimationResource(IstariEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(IstariEntity entity, long uniqueID, AnimationState<IstariEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);

		GeoBone head = this.getAnimationProcessor().getBone("head");
		GeoBone book = this.getAnimationProcessor().getBone("book");

		assert customPredicate != null;

		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);

		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));

		book.setHidden(!entity.getHidden());
	}
}