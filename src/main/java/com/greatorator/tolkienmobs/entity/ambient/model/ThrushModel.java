package com.greatorator.tolkienmobs.entity.ambient.model;

import com.greatorator.tolkienmobs.entity.ambient.SquirrelEntity;
import com.greatorator.tolkienmobs.entity.ambient.ThrushEntity;
import com.greatorator.tolkienmobs.entity.ambient.animations.FrogAnimations;
import com.greatorator.tolkienmobs.entity.ambient.animations.ThrushAnimations;
import com.greatorator.tolkienmobs.entity.ambient.render.FrogRender;
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

public class ThrushModel extends GeoModel<ThrushEntity> {
	private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/ambient/thrush.geo.json");
	private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/birds/thrush.png");
	private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(MODID, "animations/ambient/thrush.animation.json");

	@Override
	public ResourceLocation getModelResource(ThrushEntity object) {
		return this.model;
	}

	@Override
	public ResourceLocation getTextureResource(ThrushEntity object) {
		return this.texture;
	}

	@Override
	public ResourceLocation getAnimationResource(ThrushEntity object) {
		return this.animations;
	}
}