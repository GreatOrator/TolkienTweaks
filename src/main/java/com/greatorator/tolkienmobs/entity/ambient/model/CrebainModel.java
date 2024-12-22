package com.greatorator.tolkienmobs.entity.ambient.model;// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.greatorator.tolkienmobs.entity.ambient.CrebainEntity;
import com.greatorator.tolkienmobs.entity.ambient.ThrushEntity;
import com.greatorator.tolkienmobs.entity.ambient.animations.CrebainAnimations;
import com.greatorator.tolkienmobs.entity.util.ModelAnimator;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.model.GeoModel;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class CrebainModel extends GeoModel<CrebainEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/ambient/crebain.geo.json");
	private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/birds/crebain.png");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/ambient/crebain.animation.json");

	@Override
	public ResourceLocation getModelResource(CrebainEntity object) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(CrebainEntity object) {
		return this.texture;
	}

	@Override
	public ResourceLocation getAnimationResource(CrebainEntity object) {
		return this.animations;
	}
}