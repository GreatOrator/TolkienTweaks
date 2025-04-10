package com.greatorator.tolkienmobs.block.custom.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.FastColor;

public class LightningBugModel extends Model {

	private final ModelPart legs;
	private final ModelPart fatbody;
	private final ModelPart skinnybody;
	private final ModelPart glow;

	public LightningBugModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull);

		this.legs = root.getChild("legs");
		this.fatbody = root.getChild("fat_body");
		this.skinnybody = root.getChild("skinny_body");
		this.glow = root.getChild("glow");
	}

	public static LayerDefinition create() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		partdefinition.addOrReplaceChild("legs",
				CubeListBuilder.create()
						.texOffs(0, 21)
						.addBox(-4.0F, 7.9F, -5.0F, 8.0F, 0.0F, 10.0F),
				PartPose.ZERO);
		partdefinition.addOrReplaceChild("fat_body",
				CubeListBuilder.create()
						.texOffs(0, 11)
						.addBox(-2.0F, 6.0F, -4.0F, 4.0F, 2.0F, 6.0F),
				PartPose.ZERO);
		partdefinition.addOrReplaceChild("skinny_body",
				CubeListBuilder.create()
						.texOffs(0, 0)
						.addBox(-1.0F, 6.9F, -5.0F, 2.0F, 1.0F, 8.0F),
				PartPose.ZERO);
		partdefinition.addOrReplaceChild("glow",
				CubeListBuilder.create()
						.texOffs(20, 0)
						.addBox(-5.0F, 5.9F, -9.0F, 10.0F, 0.0F, 10.0F),
				PartPose.ZERO);

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		this.legs.render(stack, consumer, light, overlay, color);
		this.fatbody.render(stack, consumer, light, overlay, color);
		this.skinnybody.render(stack, consumer, light, overlay, color);
	}

	public void renderGlow(PoseStack stack, VertexConsumer consumer, float alpha) {
		this.glow.render(stack, consumer, 0xF000F0, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.colorFromFloat(alpha, 1.0F, 1.0F, 1.0F));
	}
}
