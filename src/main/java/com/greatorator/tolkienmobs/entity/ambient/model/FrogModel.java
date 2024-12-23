package com.greatorator.tolkienmobs.entity.ambient.model;


import com.greatorator.tolkienmobs.entity.ambient.FrogEntity;
import com.greatorator.tolkienmobs.entity.ambient.render.FrogRender;
import com.greatorator.tolkienmobs.entity.monster.BarrowWightEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import javax.annotation.Nullable;
import java.util.List;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class FrogModel extends GeoModel<FrogEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/ambient/frog.geo.json");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/ambient/frog.animation.json");

	@Override
	public ResourceLocation getModelResource(FrogEntity object) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(FrogEntity object) {
		return FrogRender.LOCATION_BY_VARIANT.get(object.getVariant());
	}

	@Override
	public ResourceLocation getAnimationResource(FrogEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(FrogEntity entity, long uniqueID, AnimationState<FrogEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);

		if (customPredicate == null) return;

		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		GeoBone head = this.getAnimationProcessor().getBone("head");
		GeoBone catchBug = this.getAnimationProcessor().getBone("insect");
		GeoBone tongue = this.getAnimationProcessor().getBone("tongue");

		catchBug.setHidden(!entity.isCatching());
		tongue.setHidden(!entity.isCatching());

		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}