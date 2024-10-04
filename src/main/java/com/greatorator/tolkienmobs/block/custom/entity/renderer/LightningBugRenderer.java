package com.greatorator.tolkienmobs.block.custom.entity.renderer;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.block.custom.entity.LightningBugEntity;
import com.greatorator.tolkienmobs.block.custom.entity.layers.TolkienModelLayers;
import com.greatorator.tolkienmobs.block.custom.entity.model.LightningBugModel;
import com.greatorator.tolkienmobs.util.TolkienAnimationHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.DirectionalBlock;
import org.jetbrains.annotations.Nullable;

public class LightningBugRenderer implements BlockEntityRenderer<LightningBugEntity> {

	private final LightningBugModel lightningBugModel;
	private static final ResourceLocation TEXTURE = TolkienMobsMain.getBlockModelTexture("lightningbug.png");

	public LightningBugRenderer(BlockEntityRendererProvider.Context context) {
		this.lightningBugModel = new LightningBugModel(context.bakeLayer(TolkienModelLayers.LIGHTNINGBUG));
	}

	@Override
	public void render(@Nullable LightningBugEntity entity, float partialTicks, PoseStack stack, MultiBufferSource buffer, int light, int overlay) {
		int yaw = entity != null ? entity.currentYaw : TolkienAnimationHelper.currentYaw;
		float glow = entity != null ? entity.glowIntensity : TolkienAnimationHelper.glowIntensity;
		float randRot = entity != null ? entity.randRot : 0.0F;

		stack.pushPose();
		Direction facing = entity != null ? entity.getBlockState().getValue(DirectionalBlock.FACING) : Direction.NORTH;

		stack.translate(0.5F, 0.5F, 0.5F);
		stack.mulPose(facing.getRotation());
		stack.mulPose(Axis.ZP.rotationDegrees(180.0F));
		stack.mulPose(Axis.YP.rotationDegrees(180.0F + randRot));
		stack.mulPose(Axis.YN.rotationDegrees(yaw));

		stack.pushPose();

		VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(TEXTURE));
		this.lightningBugModel.renderToBuffer(stack, consumer, light, OverlayTexture.NO_OVERLAY);

		consumer = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE));
		this.lightningBugModel.renderGlow(stack, consumer, glow);

		stack.popPose();
		stack.popPose();
	}
}
