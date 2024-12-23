package com.greatorator.tolkienmobs.entity.projectiles.model;


import com.greatorator.tolkienmobs.entity.projectiles.CobwebProjectileEntity;
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

public class CobwebModel extends GeoModel<CobwebProjectileEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/item/ammo_cobweb.geo.json");
	private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/ammo_cobweb.png");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/ammo_cobweb.json");

	@Override
	public ResourceLocation getModelResource(CobwebProjectileEntity animatable) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(CobwebProjectileEntity animatable) {
		return this.texture;
	}

	@Override
	public ResourceLocation getAnimationResource(CobwebProjectileEntity animatable) {
		return this.animations;
	}
}