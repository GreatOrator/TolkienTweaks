package com.greatorator.tolkienmobs.block.custom.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;

public class LocustModel extends Model.Simple {

	private final ModelPart legs;
	private final ModelPart fatbody;
	private final ModelPart skinnybody;
	private final ModelPart eye1;
	private final ModelPart eye2;
	private final ModelPart wings;

	public LocustModel(ModelPart root) {
		super(root, RenderTypes::entityCutout);

		this.legs = root.getChild("legs");
		this.fatbody = root.getChild("fat_body");
		this.skinnybody = root.getChild("skinny_body");
		this.eye1 = root.getChild("eye_1");
		this.eye2 = root.getChild("eye_2");
		this.wings = root.getChild("wings");
	}

	public static LayerDefinition create() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild("legs", CubeListBuilder.create()
						.texOffs(0, 21)
						.addBox(-4.0F, 7.9F, -5.0F, 8.0F, 1.0F, 9.0F),
				PartPose.ZERO);

		partdefinition.addOrReplaceChild("fat_body", CubeListBuilder.create()
						.texOffs(0, 11)
						.addBox(-2.0F, 6.0F, -4.0F, 4.0F, 2.0F, 6.0F),
				PartPose.ZERO);

		partdefinition.addOrReplaceChild("skinny_body", CubeListBuilder.create()
						.texOffs(0, 0)
						.addBox(-1.0F, 7.0F, -5.0F, 2.0F, 1.0F, 8.0F),
				PartPose.ZERO);

		partdefinition.addOrReplaceChild("eye_1", CubeListBuilder.create()
						.texOffs(20, 15)
						.addBox(1.0F, 5.0F, 2.0F, 2.0F, 2.0F, 2.0F),
				PartPose.ZERO);

		partdefinition.addOrReplaceChild("eye_2", CubeListBuilder.create()
						.texOffs(20, 15)
						.addBox(-3.0F, 5.0F, 2.0F, 2.0F, 2.0F, 2.0F),
				PartPose.ZERO);

		partdefinition.addOrReplaceChild("wings", CubeListBuilder.create()
						.texOffs(20, 0)
						.addBox(-4.0F, 5.0F, -7.0F, 8.0F, 1.0F, 8.0F),
				PartPose.ZERO);

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	public void submitBody(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, RenderType renderType, int light, int overlay, int color) {
		submitNodeCollector.submitModelPart(this.legs, poseStack, renderType, light, overlay, null, color, null);
		submitNodeCollector.submitModelPart(this.fatbody, poseStack, renderType, light, overlay, null, color, null);
		submitNodeCollector.submitModelPart(this.skinnybody, poseStack, renderType, light, overlay, null, color, null);
		submitNodeCollector.submitModelPart(this.eye1, poseStack, renderType, light, overlay, null, color, null);
		submitNodeCollector.submitModelPart(this.eye2, poseStack, renderType, light, overlay, null, color, null);
		submitNodeCollector.submitModelPart(this.wings, poseStack, renderType, light, overlay, null, color, null);
	}
}
