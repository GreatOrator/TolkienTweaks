package com.greatorator.tolkienmobs.entity.projectiles.model;


import com.greatorator.tolkienmobs.entity.ambient.CrebainEntity;
import com.greatorator.tolkienmobs.entity.projectiles.BoulderEntity;
import com.greatorator.tolkienmobs.entity.projectiles.TornadoEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TornadoModel extends GeoModel<TornadoEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/item/ammo_tornado.geo.json");
	private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/ammo_tornado.png");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/ammo_tornado.animation.json");

	@Override
	public ResourceLocation getModelResource(TornadoEntity object) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(TornadoEntity object) {
		return this.texture;
	}

	@Override
	public ResourceLocation getAnimationResource(TornadoEntity object) {
		return this.animations;
	}
}