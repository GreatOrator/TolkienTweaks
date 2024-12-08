package com.greatorator.tolkienmobs.entity.ambient.model;// Made with Blockbench 4.11.1
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.greatorator.tolkienmobs.entity.ambient.RatEntity;
import com.greatorator.tolkienmobs.entity.ambient.SquirrelEntity;
import com.greatorator.tolkienmobs.entity.ambient.animations.RatAnimations;
import com.greatorator.tolkienmobs.entity.ambient.animations.SquirrelAnimations;
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

public class SquirrelModel<T extends SquirrelEntity> extends HierarchicalModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MODID, "entityttmsquirrel"), "main");
	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart neck;
	private final ModelPart head;
	private final ModelPart headmain2;
	private final ModelPart headmain3;
	private final ModelPart headmain4;
	private final ModelPart eyel;
	private final ModelPart eyer;
	private final ModelPart earpart1l;
	private final ModelPart earpart1l2;
	private final ModelPart earpart2l;
	private final ModelPart earpart2l2;
	private final ModelPart earpart1l3;
	private final ModelPart earpart1l22;
	private final ModelPart earpart2l3;
	private final ModelPart earpart2l22;
	private final ModelPart snout;
	private final ModelPart snout3;
	private final ModelPart snout1;
	private final ModelPart snout_1;
	private final ModelPart snout4;
	private final ModelPart nose;
	private final ModelPart bodyback;
	private final ModelPart bodyback2;
	private final ModelPart bodyback22;
	private final ModelPart bodyfront;
	private final ModelPart armleft;
	private final ModelPart armleft2;
	private final ModelPart armright;
	private final ModelPart armright2;
	private final ModelPart tail;
	private final ModelPart tail2;
	private final ModelPart tail3;
	private final ModelPart tail4;
	private final ModelPart tail5;
	private final ModelPart legl;
	private final ModelPart feetl;
	private final ModelPart legr;
	private final ModelPart feetr;

	public SquirrelModel(ModelPart root) {
		this.root = root.getChild("root");
		this.body = this.root.getChild("body");
		this.neck = this.body.getChild("neck");
		this.head = this.neck.getChild("head");
		this.headmain2 = this.head.getChild("headmain2");
		this.headmain3 = this.head.getChild("headmain3");
		this.headmain4 = this.head.getChild("headmain4");
		this.eyel = this.head.getChild("eyel");
		this.eyer = this.head.getChild("eyer");
		this.earpart1l = this.head.getChild("earpart1l");
		this.earpart1l2 = this.earpart1l.getChild("earpart1l2");
		this.earpart2l = this.earpart1l.getChild("earpart2l");
		this.earpart2l2 = this.earpart2l.getChild("earpart2l2");
		this.earpart1l3 = this.head.getChild("earpart1l3");
		this.earpart1l22 = this.earpart1l3.getChild("earpart1l22");
		this.earpart2l3 = this.earpart1l3.getChild("earpart2l3");
		this.earpart2l22 = this.earpart2l3.getChild("earpart2l22");
		this.snout = this.head.getChild("snout");
		this.snout3 = this.snout.getChild("snout3");
		this.snout1 = this.snout.getChild("snout1");
		this.snout_1 = this.snout.getChild("snout_1");
		this.snout4 = this.snout.getChild("snout4");
		this.nose = this.snout.getChild("nose");
		this.bodyback = this.body.getChild("bodyback");
		this.bodyback2 = this.body.getChild("bodyback2");
		this.bodyback22 = this.bodyback2.getChild("bodyback22");
		this.bodyfront = this.body.getChild("bodyfront");
		this.armleft = this.bodyfront.getChild("armleft");
		this.armleft2 = this.armleft.getChild("armleft2");
		this.armright = this.bodyfront.getChild("armright");
		this.armright2 = this.armright.getChild("armright2");
		this.tail = this.body.getChild("tail");
		this.tail2 = this.tail.getChild("tail2");
		this.tail3 = this.tail2.getChild("tail3");
		this.tail4 = this.tail3.getChild("tail4");
		this.tail5 = this.tail4.getChild("tail5");
		this.legl = this.root.getChild("legl");
		this.feetl = this.legl.getChild("feetl");
		this.legr = this.root.getChild("legr");
		this.feetr = this.legr.getChild("feetr");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(5.0F, 24.0F, 2.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(80, 82).addBox(-5.0F, -7.0F, -7.0F, 10.0F, 14.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -26.8245F, -0.8999F, -0.9599F, 0.0F, 0.0F));

		PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(87, 89).addBox(-4.0F, -5.3852F, -7.661F, 8.0F, 8.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.3568F, -11.0881F, 0.48F, 0.0F, 0.0F));

		PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(87, 89).addBox(-5.0F, -5.2679F, -9.4641F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.5551F, -6.1251F, 0.5236F, 0.0F, 0.0F));

		PartDefinition headmain2 = head.addOrReplaceChild("headmain2", CubeListBuilder.create().texOffs(92, 93).addBox(-12.0F, 0.0F, -6.0F, 12.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, -4.2679F, -1.6641F));

		PartDefinition headmain3 = head.addOrReplaceChild("headmain3", CubeListBuilder.create().texOffs(93, 91).addBox(-8.0F, 0.0F, -6.0F, 8.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -3.7679F, -4.9641F));

		PartDefinition headmain4 = head.addOrReplaceChild("headmain4", CubeListBuilder.create().texOffs(96, 88).addBox(-8.0F, 0.0F, -6.0F, 8.0F, 12.0F, 8.0F, new CubeDeformation(-0.1F)), PartPose.offset(4.0F, -6.2679F, -2.3641F));

		PartDefinition eyel = head.addOrReplaceChild("eyel", CubeListBuilder.create().texOffs(0, 115).mirror().addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(4.5F, -0.2679F, -8.9641F));

		PartDefinition eyer = head.addOrReplaceChild("eyer", CubeListBuilder.create().texOffs(0, 115).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.5F, -0.2679F, -8.9641F));

		PartDefinition earpart1l = head.addOrReplaceChild("earpart1l", CubeListBuilder.create().texOffs(97, 93).addBox(-0.6305F, -2.9915F, -1.5F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.4653F, -5.4161F, -4.4641F, 0.0F, 0.0F, 0.1309F));

		PartDefinition earpart1l2 = earpart1l.addOrReplaceChild("earpart1l2", CubeListBuilder.create().texOffs(96, 88).addBox(-1.0F, -4.0F, 0.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.0305F, 1.3086F, -1.0F, 0.0F, 0.0F, -0.0349F));

		PartDefinition earpart2l = earpart1l.addOrReplaceChild("earpart2l", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.3305F, 0.5085F, -1.0F, 0.0F, 0.0F, -0.1571F));

		PartDefinition earpart2l_r1 = earpart2l.addOrReplaceChild("earpart2l_r1", CubeListBuilder.create().texOffs(96, 88).addBox(-4.2F, -21.6F, -21.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 16.6F, 21.0F, 0.0F, 0.0F, 0.1745F));

		PartDefinition earpart2l2 = earpart2l.addOrReplaceChild("earpart2l2", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.5F, -0.6F, 0.5F, 0.0F, 0.0F, -0.1344F));

		PartDefinition earpart2l2_r1 = earpart2l2.addOrReplaceChild("earpart2l2_r1", CubeListBuilder.create().texOffs(96, 88).addBox(-6.5F, -21.8F, -20.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, 17.2F, 20.5F, 0.0F, 0.0F, 0.3054F));

		PartDefinition earpart1l3 = head.addOrReplaceChild("earpart1l3", CubeListBuilder.create().texOffs(96, 88).addBox(-0.4128F, -2.9962F, -1.5F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.6724F, -5.3168F, -4.4641F, 0.0F, 0.0F, -0.0873F));

		PartDefinition earpart1l22 = earpart1l3.addOrReplaceChild("earpart1l22", CubeListBuilder.create().texOffs(96, 88).addBox(-1.0F, -4.0F, 0.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.9872F, 1.3038F, -1.0F, 0.0F, 0.0F, 0.0349F));

		PartDefinition earpart2l3 = earpart1l3.addOrReplaceChild("earpart2l3", CubeListBuilder.create().texOffs(96, 88).addBox(-1.0F, -5.0F, 0.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4872F, 0.5038F, -1.0F, 0.0F, 0.0F, 0.0262F));

		PartDefinition earpart2l22 = earpart2l3.addOrReplaceChild("earpart2l22", CubeListBuilder.create().texOffs(96, 88).addBox(-1.0F, -5.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1F, -1.0F, 0.5F, 0.0F, 0.0F, 0.0035F));

		PartDefinition snout = head.addOrReplaceChild("snout", CubeListBuilder.create().texOffs(14, 102).addBox(-2.0F, -1.5F, -4.0F, 4.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.2321F, -10.4641F));

		PartDefinition snout3 = snout.addOrReplaceChild("snout3", CubeListBuilder.create().texOffs(16, 103).addBox(-3.0F, 0.0F, -6.0F, 3.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, 1.5F, 3.0F, -0.3114F, 0.0F, 0.0F));

		PartDefinition snout1 = snout.addOrReplaceChild("snout1", CubeListBuilder.create().texOffs(15, 102).addBox(-4.0F, 0.0F, 0.0F, 4.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -1.5F, -4.0F, 0.0F, 0.2182F, 0.0F));

		PartDefinition snout_1 = snout.addOrReplaceChild("snout_1", CubeListBuilder.create().texOffs(15, 102).addBox(-4.0F, 0.0F, 0.0F, 4.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.9F, -1.5F, -3.0F, 0.0F, -0.2182F, 0.0F));

		PartDefinition snout4 = snout.addOrReplaceChild("snout4", CubeListBuilder.create().texOffs(16, 102).addBox(-3.0F, 0.0F, 0.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -1.5F, -4.0F, 0.4245F, 0.0F, 0.0F));

		PartDefinition nose = snout.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(0, 121).addBox(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.4F, -3.7F));

		PartDefinition bodyback = body.addOrReplaceChild("bodyback", CubeListBuilder.create().texOffs(76, 85).addBox(-16.0F, 0.0F, 0.0F, 16.0F, 12.0F, 10.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(8.0F, -6.2F, 6.0F, -0.1396F, 0.0F, 0.0F));

		PartDefinition bodyback2 = body.addOrReplaceChild("bodyback2", CubeListBuilder.create().texOffs(82, 87).addBox(-16.0F, 0.0F, 0.0F, 16.0F, 15.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, -5.8F, 15.6F, -0.7076F, 0.0F, 0.0F));

		PartDefinition bodyback22 = bodyback2.addOrReplaceChild("bodyback22", CubeListBuilder.create().texOffs(83, 88).addBox(-16.0F, 0.0F, 0.0F, 16.0F, 15.0F, 6.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 0.0F, 6.6F, -0.1981F, 0.0F, 0.0F));

		PartDefinition bodyfront = body.addOrReplaceChild("bodyfront", CubeListBuilder.create().texOffs(84, 86).addBox(-12.0F, 0.0F, 0.0F, 12.0F, 14.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -1.5F, -15.5F, 0.5236F, 0.0F, 0.0F));

		PartDefinition armleft = bodyfront.addOrReplaceChild("armleft", CubeListBuilder.create().texOffs(29, 110).addBox(0.0F, 0.0F, 0.0F, 4.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 7.0F, 2.0F));

		PartDefinition armleft2 = armleft.addOrReplaceChild("armleft2", CubeListBuilder.create().texOffs(31, 115).addBox(1.0F, 0.0F, 0.0F, 3.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 9.0F, 2.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition armright = bodyfront.addOrReplaceChild("armright", CubeListBuilder.create().texOffs(29, 110).addBox(-4.0F, 0.0F, 0.0F, 4.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-10.0F, 7.0F, 2.0F));

		PartDefinition armright2 = armright.addOrReplaceChild("armright2", CubeListBuilder.create().texOffs(31, 115).addBox(-3.0F, 0.0F, 0.0F, 3.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 9.0F, 2.0F, -0.7926F, 0.0F, 0.0F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(78, 0).addBox(-12.0F, 0.0F, -7.0F, 12.0F, 16.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, 0.8F, 13.0F, 3.0892F, 0.0F, 0.0F));

		PartDefinition tail2 = tail.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(80, 1).addBox(-10.0F, 0.0F, 0.0F, 10.0F, 16.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 16.0F, -6.5F, 0.9599F, 0.0F, 0.0F));

		PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(92, 5).addBox(-8.0F, 0.0F, 0.0F, 8.0F, 13.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 16.0F, 0.5F, 0.5236F, 0.0F, 0.0F));

		PartDefinition tail4 = tail3.addOrReplaceChild("tail4", CubeListBuilder.create().texOffs(81, 7).addBox(-7.0F, 0.0F, 0.0F, 7.0F, 9.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 7.0F, 2.5F, -0.5934F, 0.0F, 0.0F));

		PartDefinition tail5 = tail4.addOrReplaceChild("tail5", CubeListBuilder.create().texOffs(88, 8).addBox(-6.0F, 0.0F, 0.0F, 6.0F, 9.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 4.0F, 1.0F, -0.5934F, 0.0F, 0.0F));

		PartDefinition legl = root.addOrReplaceChild("legl", CubeListBuilder.create().texOffs(0, 113).addBox(-2.5F, -2.5F, -2.0F, 5.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -7.8F, 3.7F, -1.5708F, 0.0F, 0.0F));

		PartDefinition feetl = legl.addOrReplaceChild("feetl", CubeListBuilder.create().texOffs(0, 103).addBox(-4.0F, 0.0F, 0.0F, 4.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 0.5F, 5.0F));

		PartDefinition legr = root.addOrReplaceChild("legr", CubeListBuilder.create().texOffs(0, 113).addBox(-2.5F, -2.5F, -3.0F, 5.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.5F, -6.8F, 3.7F, -1.5708F, 0.0F, 0.0F));

		PartDefinition feetr = legr.addOrReplaceChild("feetr", CubeListBuilder.create().texOffs(0, 103).addBox(-4.0F, 0.0F, 0.0F, 4.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 0.5F, 4.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(SquirrelEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.applyHeadRotation(netHeadYaw, headPitch);

		this.animateWalk(SquirrelAnimations.walk, limbSwing, limbSwingAmount, 2f, 2.5f);
		this.animate(entity.idleAnimationState, SquirrelAnimations.idle, ageInTicks, 1f);

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