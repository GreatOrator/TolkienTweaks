package com.greatorator.tolkienmobs.entity.ambient.model;// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.greatorator.tolkienmobs.entity.ambient.CrebainEntity;
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

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class CrebainModel<T extends CrebainEntity> extends HierarchicalModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MODID, "entityttmcrebain"), "main");
	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart neck;
	private final ModelPart head;
	private final ModelPart beak;
	private final ModelPart wing_right;
	private final ModelPart base1_right;
	private final ModelPart plane1_right;
	private final ModelPart base2_right;
	private final ModelPart plane2_right;
	private final ModelPart wing_left;
	private final ModelPart base1_left;
	private final ModelPart plane1_left;
	private final ModelPart base2_left;
	private final ModelPart plane2_left;
	private final ModelPart tail;
	private final ModelPart nadhvost;
	private final ModelPart feathers;
	private final ModelPart feather1;
	private final ModelPart feather2;
	private final ModelPart feather3;
	private final ModelPart feather4;
	private final ModelPart leg_right;
	private final ModelPart noga_right;
	private final ModelPart foot_right;
	private final ModelPart leg_left;
	private final ModelPart noga_left;
	private final ModelPart foot_left;

	public CrebainModel(ModelPart root) {
		this.root = root.getChild("root");
		this.body = this.root.getChild("body");
		this.neck = this.body.getChild("neck");
		this.head = this.neck.getChild("head");
		this.beak = this.head.getChild("beak");
		this.wing_right = this.body.getChild("wing_right");
		this.base1_right = this.wing_right.getChild("base1_right");
		this.plane1_right = this.base1_right.getChild("plane1_right");
		this.base2_right = this.base1_right.getChild("base2_right");
		this.plane2_right = this.base2_right.getChild("plane2_right");
		this.wing_left = this.body.getChild("wing_left");
		this.base1_left = this.wing_left.getChild("base1_left");
		this.plane1_left = this.base1_left.getChild("plane1_left");
		this.base2_left = this.base1_left.getChild("base2_left");
		this.plane2_left = this.base2_left.getChild("plane2_left");
		this.tail = this.body.getChild("tail");
		this.nadhvost = this.tail.getChild("nadhvost");
		this.feathers = this.nadhvost.getChild("feathers");
		this.feather1 = this.feathers.getChild("feather1");
		this.feather2 = this.feathers.getChild("feather2");
		this.feather3 = this.feathers.getChild("feather3");
		this.feather4 = this.feathers.getChild("feather4");
		this.leg_right = this.root.getChild("leg_right");
		this.noga_right = this.leg_right.getChild("noga_right");
		this.foot_right = this.noga_right.getChild("foot_right");
		this.leg_left = this.root.getChild("leg_left");
		this.noga_left = this.leg_left.getChild("noga_left");
		this.foot_left = this.noga_left.getChild("foot_left");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, -6.25F, 0.5F));

		PartDefinition body_r1 = body.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(0, 7).addBox(-3.0F, -3.5F, -4.5F, 6.0F, 5.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

		PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, -3.5F));

		PartDefinition neck_r1 = neck.addOrReplaceChild("neck_r1", CubeListBuilder.create().texOffs(27, 20).addBox(-2.0F, 0.0F, -1.25F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, -3.0F, 0.6981F, 0.0F, 0.0F));

		PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 31).addBox(-2.0F, -4.0F, -4.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(30, 30).addBox(0.0F, 0.0F, -4.0F, 0.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.5F, -0.75F));

		PartDefinition beak = head.addOrReplaceChild("beak", CubeListBuilder.create(), PartPose.offset(0.0F, 8.75F, 5.25F));

		PartDefinition beak_r1 = beak.addOrReplaceChild("beak_r1", CubeListBuilder.create().texOffs(17, 31).addBox(-0.5F, -0.25F, 0.0F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.75F, -11.5F, 0.1848F, 0.298F, 0.0681F));

		PartDefinition beak_r2 = beak.addOrReplaceChild("beak_r2", CubeListBuilder.create().texOffs(49, 26).addBox(-0.5F, -0.25F, 0.0F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.75F, -11.5F, 0.1848F, -0.298F, -0.0681F));

		PartDefinition beak_r3 = beak.addOrReplaceChild("beak_r3", CubeListBuilder.create().texOffs(49, 5).addBox(-0.5F, -1.1F, -0.75F, 1.0F, 2.1F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.75F, -11.5F, 0.1745F, 0.0F, 0.0F));

		PartDefinition wing_right = body.addOrReplaceChild("wing_right", CubeListBuilder.create(), PartPose.offset(-3.0F, -1.75F, -3.0F));

		PartDefinition base1_right = wing_right.addOrReplaceChild("base1_right", CubeListBuilder.create(), PartPose.offset(0.0F, 0.5F, 0.0F));

		PartDefinition base1_right_r1 = base1_right.addOrReplaceChild("base1_right_r1", CubeListBuilder.create().texOffs(48, 21).addBox(-4.0F, -1.0754F, -1.1378F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

		PartDefinition plane1_right = base1_right.addOrReplaceChild("plane1_right", CubeListBuilder.create(), PartPose.offset(0.0F, -0.5F, 0.5F));

		PartDefinition plane1_right_r1 = plane1_right.addOrReplaceChild("plane1_right_r1", CubeListBuilder.create().texOffs(21, 8).addBox(-6.0F, 0.0038F, -0.5434F, 6.0F, 0.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

		PartDefinition base2_right = base1_right.addOrReplaceChild("base2_right", CubeListBuilder.create(), PartPose.offset(-3.75F, -0.4312F, -0.5716F));

		PartDefinition base2_right_r1 = base2_right.addOrReplaceChild("base2_right_r1", CubeListBuilder.create().texOffs(45, 12).addBox(-8.0F, -0.5F, -0.5F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.176F, -0.1289F, 0.0229F));

		PartDefinition plane2_right = base2_right.addOrReplaceChild("plane2_right", CubeListBuilder.create(), PartPose.offset(0.25F, -0.0516F, 0.7326F));

		PartDefinition plane2_right_r1 = plane2_right.addOrReplaceChild("plane2_right_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-12.7952F, -0.072F, -0.3491F, 13.0F, 0.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.176F, -0.1289F, 0.0229F));

		PartDefinition wing_left = body.addOrReplaceChild("wing_left", CubeListBuilder.create(), PartPose.offset(3.0F, -1.75F, -3.0F));

		PartDefinition base1_left = wing_left.addOrReplaceChild("base1_left", CubeListBuilder.create(), PartPose.offset(0.0F, 0.5F, 0.0F));

		PartDefinition base1_left_r1 = base1_left.addOrReplaceChild("base1_left_r1", CubeListBuilder.create().texOffs(48, 21).mirror().addBox(0.0F, -1.0754F, -1.1378F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

		PartDefinition plane1_left = base1_left.addOrReplaceChild("plane1_left", CubeListBuilder.create(), PartPose.offset(0.0F, -0.5F, 0.5F));

		PartDefinition plane1_left_r1 = plane1_left.addOrReplaceChild("plane1_left_r1", CubeListBuilder.create().texOffs(21, 8).mirror().addBox(0.0F, 0.0038F, -0.5434F, 6.0F, 0.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

		PartDefinition base2_left = base1_left.addOrReplaceChild("base2_left", CubeListBuilder.create(), PartPose.offset(3.75F, -0.4312F, -0.5716F));

		PartDefinition base2_left_r1 = base2_left.addOrReplaceChild("base2_left_r1", CubeListBuilder.create().texOffs(45, 12).mirror().addBox(0.0F, -0.5F, -0.5F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.176F, 0.1289F, -0.0229F));

		PartDefinition plane2_left = base2_left.addOrReplaceChild("plane2_left", CubeListBuilder.create(), PartPose.offset(-0.75F, -0.0516F, 0.7326F));

		PartDefinition plane2_left_r1 = plane2_left.addOrReplaceChild("plane2_left_r1", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0.291F, -0.072F, -0.2838F, 13.0F, 0.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.176F, 0.1289F, -0.0229F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0F, -0.75F, 3.5F));

		PartDefinition nadhvost = tail.addOrReplaceChild("nadhvost", CubeListBuilder.create(), PartPose.offset(0.0F, 0.4015F, 0.1617F));

		PartDefinition nadhvost_r1 = nadhvost.addOrReplaceChild("nadhvost_r1", CubeListBuilder.create().texOffs(48, 15).addBox(-2.0F, -1.0F, -1.25F, 4.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 2.0F, -0.3491F, 0.0F, 0.0F));

		PartDefinition feathers = nadhvost.addOrReplaceChild("feathers", CubeListBuilder.create(), PartPose.offset(0.0F, 0.2548F, 0.1277F));

		PartDefinition feather1 = feathers.addOrReplaceChild("feather1", CubeListBuilder.create(), PartPose.offset(1.25F, 0.0F, 0.25F));

		PartDefinition feather1_r1 = feather1.addOrReplaceChild("feather1_r1", CubeListBuilder.create().texOffs(12, 23).addBox(-1.0F, 0.25F, -1.5F, 2.0F, 0.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.75F, 0.25F, 2.25F, -0.212F, 0.6003F, -0.121F));

		PartDefinition feather2 = feathers.addOrReplaceChild("feather2", CubeListBuilder.create(), PartPose.offset(0.75F, 0.25F, 0.5F));

		PartDefinition feather2_r1 = feather2.addOrReplaceChild("feather2_r1", CubeListBuilder.create().texOffs(8, 23).addBox(-2.0F, 0.25F, -0.5F, 2.0F, 0.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.25F, 0.0F, 2.0F, -0.1752F, 0.0859F, -0.0152F));

		PartDefinition feather3 = feathers.addOrReplaceChild("feather3", CubeListBuilder.create(), PartPose.offset(-0.75F, 0.25F, 0.5F));

		PartDefinition feather3_r1 = feather3.addOrReplaceChild("feather3_r1", CubeListBuilder.create().texOffs(0, 23).addBox(0.0F, 0.25F, -0.5F, 2.0F, 0.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.25F, 0.0F, 2.0F, -0.1752F, -0.0859F, 0.0152F));

		PartDefinition feather4 = feathers.addOrReplaceChild("feather4", CubeListBuilder.create(), PartPose.offset(-1.0F, 0.25F, 0.25F));

		PartDefinition feather4_r1 = feather4.addOrReplaceChild("feather4_r1", CubeListBuilder.create().texOffs(4, 23).addBox(-1.0F, 0.25F, -1.5F, 2.0F, 0.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 2.25F, -0.212F, -0.6003F, 0.121F));

		PartDefinition leg_right = root.addOrReplaceChild("leg_right", CubeListBuilder.create(), PartPose.offset(-1.75F, -3.75F, 1.5F));

		PartDefinition noga_right = leg_right.addOrReplaceChild("noga_right", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(34, 1).mirror().addBox(-0.75F, 0.0F, -0.75F, 1.5F, 2.0F, 1.5F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, -0.25F, 0.0F));

		PartDefinition foot_right = noga_right.addOrReplaceChild("foot_right", CubeListBuilder.create().texOffs(47, 0).addBox(-1.5F, 0.0F, -2.5F, 3.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition leg_left = root.addOrReplaceChild("leg_left", CubeListBuilder.create(), PartPose.offset(1.75F, -3.75F, 1.5F));

		PartDefinition noga_left = leg_left.addOrReplaceChild("noga_left", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(34, 1).mirror().addBox(-0.75F, 0.0F, -0.75F, 1.5F, 2.0F, 1.5F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, -0.25F, 0.0F));

		PartDefinition foot_left = noga_left.addOrReplaceChild("foot_left", CubeListBuilder.create().texOffs(47, 0).mirror().addBox(-1.5F, 0.0F, -2.5F, 3.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 4.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(CrebainEntity entity, float limbSwing, float limbSwingAmount, float age, float headYaw, float headPitch) {
		root.getAllParts().forEach(ModelPart::resetPose);

		this.applyHeadRotation(headYaw, headPitch);

		float partialTicks = age - entity.tickCount;
		float flightAnimation = entity.getFlightAnimation(partialTicks);
		float walkAnimation = 1.0f - flightAnimation;

		if (flightAnimation > 0.0f) {
			if (flightAnimation > 0.0f) {
				body.xRot += Mth.DEG_TO_RAD * flightAnimation;
				head.xRot -= Mth.DEG_TO_RAD * flightAnimation;

				tail.xRot -= Mth.DEG_TO_RAD * flightAnimation;


				leg_left.xRot += 40.0f * Mth.DEG_TO_RAD * flightAnimation;
				leg_right.xRot += 40.0f * Mth.DEG_TO_RAD * flightAnimation;

				wing_left.xRot += Mth.DEG_TO_RAD * flightAnimation;
				wing_right.xRot += Mth.DEG_TO_RAD * flightAnimation;

				try (ModelAnimator.Cycle fly = ModelAnimator.cycle(age * 0.15f, flightAnimation)) {
					body.y += fly.eval(1.0f, 0.2f, 0.06f, 0.0f);
					body.xRot += fly.eval(1.0f, -0.04f, 0.06f, -0.04f);

					tail.xRot += fly.eval(1.0f, 0.1f, -0.2f, 0.0f);

					wing_left.zRot += fly.eval(1.0f, 1.3f, -0.1f, 1.4f);
					wing_right.zRot += fly.eval(1.0f, -1.3f, -0.1f, -1.4f);
				}
			}
		}

		if (walkAnimation > 0.0f) {
			this.animate(entity.idleAnimationState, CrebainAnimations.idle, age, 1f);
		}
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