package com.greatorator.tolkienmobs.block.custom.entity.renderer;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.block.custom.entity.LocustBlockEntity;
import com.greatorator.tolkienmobs.block.custom.entity.layers.TolkienModelLayers;
import com.greatorator.tolkienmobs.block.custom.entity.model.LocustModel;
import com.greatorator.tolkienmobs.block.custom.entity.renderer.state.LocustRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class LocustRenderer implements BlockEntityRenderer<LocustBlockEntity, LocustRenderState> {

	private final LocustModel locustModel;
	private static final Identifier TEXTURE = TolkienMobsMain.getBlockModelTexture("locust.png");

	public LocustRenderer(BlockEntityRendererProvider.Context context) {
		this.locustModel = new LocustModel(context.bakeLayer(TolkienModelLayers.LOCUST));
	}

	@Override
	public LocustRenderState createRenderState() {
		return new LocustRenderState();
	}

	@Override
	public void extractRenderState(
			LocustBlockEntity blockEntity,
			LocustRenderState state,
			float partialTicks,
			Vec3 cameraPosition,
			ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
	) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
		state.facing = blockEntity.getBlockState().getValue(DirectionalBlock.FACING);
		state.yaw = blockEntity.currentYaw;
		state.randRot = blockEntity.randRot;
	}

	@Override
	public void submit(LocustRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		poseStack.pushPose();
		poseStack.translate(0.5F, 0.5F, 0.5F);
		poseStack.mulPose(state.facing.getRotation());
		poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0F + state.randRot));
		poseStack.mulPose(Axis.YN.rotationDegrees(state.yaw));

		this.locustModel.submitBody(
				poseStack, submitNodeCollector, this.locustModel.renderType(TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, -1
		);

		poseStack.popPose();
	}
}
