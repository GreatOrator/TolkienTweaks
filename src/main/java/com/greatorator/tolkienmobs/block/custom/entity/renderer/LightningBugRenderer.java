package com.greatorator.tolkienmobs.block.custom.entity.renderer;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.block.custom.entity.LightningBugBlockEntity;
import com.greatorator.tolkienmobs.block.custom.entity.layers.TolkienModelLayers;
import com.greatorator.tolkienmobs.block.custom.entity.model.LightningBugModel;
import com.greatorator.tolkienmobs.block.custom.entity.renderer.state.LightningBugRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class LightningBugRenderer implements BlockEntityRenderer<LightningBugBlockEntity, LightningBugRenderState> {

	private final LightningBugModel lightningBugModel;
	private static final Identifier TEXTURE = TolkienMobsMain.getBlockModelTexture("lightningbug.png");

	public LightningBugRenderer(BlockEntityRendererProvider.Context context) {
		this.lightningBugModel = new LightningBugModel(context.bakeLayer(TolkienModelLayers.LIGHTNINGBUG));
	}

	@Override
	public LightningBugRenderState createRenderState() {
		return new LightningBugRenderState();
	}

	@Override
	public void extractRenderState(
			LightningBugBlockEntity blockEntity,
			LightningBugRenderState state,
			float partialTicks,
			Vec3 cameraPosition,
			ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
	) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
		state.facing = blockEntity.getBlockState().getValue(DirectionalBlock.FACING);
		state.yaw = blockEntity.currentYaw;
		state.glow = blockEntity.glowIntensity;
		state.randRot = blockEntity.randRot;
	}

	@Override
	public void submit(LightningBugRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		poseStack.pushPose();
		poseStack.translate(0.5F, 0.5F, 0.5F);
		poseStack.mulPose(state.facing.getRotation());
		poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0F + state.randRot));
		poseStack.mulPose(Axis.YN.rotationDegrees(state.yaw));

		this.lightningBugModel.submitBody(
				poseStack, submitNodeCollector, RenderTypes.entityCutoutCull(TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, -1
		);
		this.lightningBugModel.submitGlow(poseStack, submitNodeCollector, RenderTypes.entityTranslucent(TEXTURE), state.glow);

		poseStack.popPose();
	}
}
