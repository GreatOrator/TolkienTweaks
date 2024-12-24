package com.greatorator.tolkienmobs.entity.npc.model;

import com.greatorator.tolkienmobs.entity.npc.DwarfEntity;
import com.greatorator.tolkienmobs.entity.npc.render.DwarfRender;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class DwarfModel extends GeoModel<DwarfEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/merchant/dwarf.geo.json");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/merchant/dwarf.animation.json");

	@Override
	public ResourceLocation getModelResource(DwarfEntity object) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(DwarfEntity object) {
		return DwarfRender.LOCATION_BY_VARIANT.get(object.getTolkienVariant());
	}

	@Override
	public ResourceLocation getAnimationResource(DwarfEntity object) {
		return this.animations;
	}

	@Override
	public void setCustomAnimations(DwarfEntity entity, long uniqueID, AnimationState<DwarfEntity> customPredicate) {
		super.setCustomAnimations(entity, uniqueID, customPredicate);
		GeoBone head = this.getAnimationProcessor().getBone("head");
		assert customPredicate != null;
		EntityModelData extraData = (EntityModelData) customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
		head.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
		head.setRotY(extraData.netHeadYaw() *0.5f* ((float) Math.PI / 180F));
	}
}