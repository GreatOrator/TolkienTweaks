package com.greatorator.tolkienmobs.entity.ambient.model;// Made with Blockbench 4.11.1
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.greatorator.tolkienmobs.entity.ambient.FrogEntity;
import com.greatorator.tolkienmobs.entity.ambient.GeckoEntity;
import com.greatorator.tolkienmobs.entity.ambient.animations.FrogAnimations;
import com.greatorator.tolkienmobs.entity.ambient.animations.GeckoAnimations;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.animation.definitions.FrogAnimation;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.frog.Frog;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class FrogModel<T extends FrogEntity> extends HierarchicalModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MODID, "entityttmfrog"), "main");
	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart mouth;
	private final ModelPart bulge;
	private final ModelPart frontLegLeft;
	private final ModelPart frontLegRight;
	private final ModelPart haunchLeft;
	private final ModelPart rearFootLeft;
	private final ModelPart haunchRight;
	private final ModelPart rearFootRight;

	public FrogModel(ModelPart root) {
		this.root = root.getChild("root");
		this.body = this.root.getChild("body");
		this.head = this.body.getChild("head");
		this.mouth = this.head.getChild("mouth");
		this.bulge = this.mouth.getChild("bulge");
		this.frontLegLeft = this.root.getChild("frontLegLeft");
		this.frontLegRight = this.root.getChild("frontLegRight");
		this.haunchLeft = this.root.getChild("haunchLeft");
		this.rearFootLeft = this.haunchLeft.getChild("rearFootLeft");
		this.haunchRight = this.root.getChild("haunchRight");
		this.rearFootRight = this.haunchRight.getChild("rearFootRight");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -2.5F, -5.0F, 6.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.2402F, 3.1305F, -0.3491F, 0.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(32, 0).addBox(-2.5F, -2.6237F, -4.5374F, 5.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(34, 2).addBox(-2.5F, -0.6237F, -1.5374F, 5.0F, 2.0F, 3.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, -1.335F, -5.1054F, 0.3491F, 0.0F, 0.0F));

		PartDefinition head_r1 = head.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(58, 3).addBox(-1.5F, -13.25F, -5.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 9.3763F, -1.5374F, 0.0F, -1.5708F, 0.0F));

		PartDefinition head_r2 = head.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(58, 0).addBox(-0.5F, -13.25F, -1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 9.3763F, -1.5374F, 0.0F, 1.5708F, 0.0F));

		PartDefinition mouth = head.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(36, 15).addBox(-2.5F, -0.5F, -2.8F, 5.0F, 1.0F, 3.0F, new CubeDeformation(-0.1F)), PartPose.offset(0.0F, 0.7763F, -1.5374F));

		PartDefinition bulge = mouth.addOrReplaceChild("bulge", CubeListBuilder.create().texOffs(39, 16).addBox(-2.0F, -0.5F, -1.5F, 4.0F, 1.0F, 3.0F, new CubeDeformation(-0.1F)), PartPose.offset(0.0F, 0.1F, -0.5F));

		PartDefinition tongue = mouth.addOrReplaceChild("tongue", CubeListBuilder.create(), PartPose.offset(0.5F, 2.2006F, 10.8115F));

		PartDefinition tongue_r1 = tongue.addOrReplaceChild("tongue_r1", CubeListBuilder.create().texOffs(46, 23).addBox(0.0F, -2.0F, 1.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -1.6006F, -8.8115F, -0.3491F, 0.0F, 0.0F));

		PartDefinition frontLegLeft = root.addOrReplaceChild("frontLegLeft", CubeListBuilder.create().texOffs(0, 15).addBox(-1.0F, -0.9816F, -1.1908F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -6.0F, -1.0F, -0.192F, 0.0F, 0.0F));

		PartDefinition frontLegRight = root.addOrReplaceChild("frontLegRight", CubeListBuilder.create().texOffs(0, 15).addBox(-1.0F, -0.9816F, -1.1908F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -6.0F, -1.0F, -0.192F, 0.0F, 0.0F));

		PartDefinition haunchLeft = root.addOrReplaceChild("haunchLeft", CubeListBuilder.create(), PartPose.offsetAndRotation(3.0F, -3.7369F, 5.3172F, -0.3665F, 0.0F, 0.0F));

		PartDefinition haunchLeft_r1 = haunchLeft.addOrReplaceChild("haunchLeft_r1", CubeListBuilder.create().texOffs(16, 15).addBox(-1.0F, -2.0F, -2.5F, 2.0F, 4.0F, 5.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5272F, 0.0F, 0.0F));

		PartDefinition rearFootLeft = haunchLeft.addOrReplaceChild("rearFootLeft", CubeListBuilder.create(), PartPose.offset(0.0F, 2.6525F, 1.3445F));

		PartDefinition rearFootLeft_r1 = rearFootLeft.addOrReplaceChild("rearFootLeft_r1", CubeListBuilder.create().texOffs(8, 24).addBox(-1.0F, -0.2444F, -5.7214F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3491F, 0.0F, 0.0F));

		PartDefinition haunchRight = root.addOrReplaceChild("haunchRight", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.0F, -3.7369F, 5.3172F, -0.3665F, 0.0F, 0.0F));

		PartDefinition haunchRight_r1 = haunchRight.addOrReplaceChild("haunchRight_r1", CubeListBuilder.create().texOffs(16, 15).addBox(-1.0F, -2.0F, -2.5F, 2.0F, 4.0F, 5.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5272F, 0.0F, 0.0F));

		PartDefinition rearFootRight = haunchRight.addOrReplaceChild("rearFootRight", CubeListBuilder.create(), PartPose.offset(0.0F, 2.7811F, 1.4554F));

		PartDefinition rearFootRight_r1 = rearFootRight.addOrReplaceChild("rearFootRight_r1", CubeListBuilder.create().texOffs(8, 24).addBox(-4.0F, 1.0F, 2.0F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 1.7189F, -7.6554F, 0.3927F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(FrogEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.applyHeadRotation(netHeadYaw, headPitch);

		this.animateWalk(FrogAnimations.walk, limbSwing, limbSwingAmount, 2f, 2.5f);
		this.animate(entity.idleAnimationState, FrogAnimations.idle, ageInTicks, 1f);
	}

	private void applyHeadRotation(float headYaw, float headPitch) {
		headYaw = Mth.clamp(headYaw, -30f, 30f);
		headPitch = Mth.clamp(headPitch, -25f, 45);

		this.head.yRot = headYaw * ((float)Math.PI / 180f);
		this.head.xRot = headPitch *  ((float)Math.PI / 180f);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}

	@Override
	public ModelPart root() {
		return body;
	}
}