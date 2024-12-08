package com.greatorator.tolkienmobs.entity.ambient.model;// Made with Blockbench 4.11.1
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.greatorator.tolkienmobs.entity.ambient.RatEntity;
import com.greatorator.tolkienmobs.entity.ambient.animations.GeckoAnimations;
import com.greatorator.tolkienmobs.entity.ambient.animations.RatAnimations;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class RatModel<T extends RatEntity> extends HierarchicalModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MODID, "entityttmrat"), "main");
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart RatMouth;
	private final ModelPart RatNose;
	private final ModelPart RatWhiskers;
	private final ModelPart RatWhiskers3;
	private final ModelPart RatWhiskers2;
	private final ModelPart RatEarR;
	private final ModelPart RatEarL;
	private final ModelPart body;
	private final ModelPart RatTail1;
	private final ModelPart RatTail2;
	private final ModelPart RatTail3;
	private final ModelPart fLegRight;
	private final ModelPart RatFootFR;
	private final ModelPart fLegLeft;
	private final ModelPart RatFootFL;
	private final ModelPart rLegRight;
	private final ModelPart RatFootRR;
	private final ModelPart rLegLeft;
	private final ModelPart RatFootRL;

	public RatModel(ModelPart root) {
		this.root = root.getChild("root");
		this.head = this.root.getChild("head");
		this.RatMouth = this.head.getChild("RatMouth");
		this.RatNose = this.RatMouth.getChild("RatNose");
		this.RatWhiskers = this.RatNose.getChild("RatWhiskers");
		this.RatWhiskers3 = this.RatWhiskers.getChild("RatWhiskers3");
		this.RatWhiskers2 = this.RatWhiskers.getChild("RatWhiskers2");
		this.RatEarR = this.head.getChild("RatEarR");
		this.RatEarL = this.head.getChild("RatEarL");
		this.body = this.root.getChild("body");
		this.RatTail1 = this.body.getChild("RatTail1");
		this.RatTail2 = this.RatTail1.getChild("RatTail2");
		this.RatTail3 = this.RatTail2.getChild("RatTail3");
		this.fLegRight = this.root.getChild("fLegRight");
		this.RatFootFR = this.fLegRight.getChild("RatFootFR");
		this.fLegLeft = this.root.getChild("fLegLeft");
		this.RatFootFL = this.fLegLeft.getChild("RatFootFL");
		this.rLegRight = this.root.getChild("rLegRight");
		this.RatFootRR = this.rLegRight.getChild("RatFootRR");
		this.rLegLeft = this.root.getChild("rLegLeft");
		this.RatFootRL = this.rLegLeft.getChild("RatFootRL");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 15).addBox(-1.5F, -1.5F, -3.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.0F, -5.5F));

		PartDefinition RatMouth = head.addOrReplaceChild("RatMouth", CubeListBuilder.create().texOffs(0, 11).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, -3.0F));

		PartDefinition RatNose = RatMouth.addOrReplaceChild("RatNose", CubeListBuilder.create().texOffs(0, 16).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.8F, -1.8F));

		PartDefinition RatWhiskers = RatNose.addOrReplaceChild("RatWhiskers", CubeListBuilder.create().texOffs(26, 20).addBox(-1.5F, -2.2F, 0.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.5F, -0.3F));

		PartDefinition RatWhiskers3 = RatWhiskers.addOrReplaceChild("RatWhiskers3", CubeListBuilder.create().texOffs(20, 24).addBox(0.0F, -2.5F, 0.0F, 2.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.4F, -0.7F, 0.0F, 0.0F, -0.3491F, 0.0F));

		PartDefinition RatWhiskers2 = RatWhiskers.addOrReplaceChild("RatWhiskers2", CubeListBuilder.create().texOffs(20, 24).addBox(-2.0F, -2.5F, 0.0F, 2.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -0.7F, 0.0F, 0.0F, 0.3491F, 0.0F));

		PartDefinition RatEarR = head.addOrReplaceChild("RatEarR", CubeListBuilder.create().texOffs(26, 29).addBox(0.0F, -2.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.7F, -0.7F, -1.0F));

		PartDefinition RatEarL = head.addOrReplaceChild("RatEarL", CubeListBuilder.create().texOffs(26, 29).mirror().addBox(-2.0F, -2.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-0.7F, -0.7F, -1.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(1, 23).addBox(-2.0F, -2.0F, -5.0F, 4.0F, 4.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(0, 22).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.0F, 0.0F));

		PartDefinition RatTail1 = body.addOrReplaceChild("RatTail1", CubeListBuilder.create().texOffs(18, 0).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 4.8F));

		PartDefinition RatTail2 = RatTail1.addOrReplaceChild("RatTail2", CubeListBuilder.create().texOffs(19, 0).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 4.0F));

		PartDefinition RatTail3 = RatTail2.addOrReplaceChild("RatTail3", CubeListBuilder.create().texOffs(20, 0).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 4.0F));

		PartDefinition fLegRight = root.addOrReplaceChild("fLegRight", CubeListBuilder.create().texOffs(25, 25).addBox(0.0F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 22.0F, -3.0F));

		PartDefinition RatFootFR = fLegRight.addOrReplaceChild("RatFootFR", CubeListBuilder.create().texOffs(20, 29).addBox(-0.5F, -0.5F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 1.5F, -0.5F));

		PartDefinition fLegLeft = root.addOrReplaceChild("fLegLeft", CubeListBuilder.create().texOffs(25, 25).addBox(-1.0F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 22.0F, -3.0F));

		PartDefinition RatFootFL = fLegLeft.addOrReplaceChild("RatFootFL", CubeListBuilder.create().texOffs(20, 29).addBox(-0.5F, -0.5F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 1.5F, -0.5F));

		PartDefinition rLegRight = root.addOrReplaceChild("rLegRight", CubeListBuilder.create().texOffs(24, 23).addBox(0.0F, -0.5F, -1.5F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 20.5F, 2.7F));

		PartDefinition RatFootRR = rLegRight.addOrReplaceChild("RatFootRR", CubeListBuilder.create().texOffs(20, 29).addBox(-0.5F, -0.5F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 3.0F, -1.0F));

		PartDefinition rLegLeft = root.addOrReplaceChild("rLegLeft", CubeListBuilder.create().texOffs(24, 23).addBox(-1.0F, -0.5F, -1.5F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 20.5F, 2.7F));

		PartDefinition RatFootRL = rLegLeft.addOrReplaceChild("RatFootRL", CubeListBuilder.create().texOffs(20, 29).addBox(-0.5F, -0.5F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 3.0F, -1.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(RatEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.applyHeadRotation(netHeadYaw, headPitch);

		this.animateWalk(RatAnimations.walk, limbSwing, limbSwingAmount, 2f, 2.5f);
		this.animate(entity.idleAnimationState, RatAnimations.idle, ageInTicks, 1f);
	}

	private void applyHeadRotation(float headYaw, float headPitch) {
		headYaw = Mth.clamp(headYaw, -30f, 30f);
		headPitch = Mth.clamp(headPitch, -25f, 45);

		this.head.yRot = headYaw * ((float)Math.PI / 180f);
		this.head.xRot = headPitch *  ((float)Math.PI / 180f);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
		root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}

	@Override
	public ModelPart root() {
		return root;
	}
}