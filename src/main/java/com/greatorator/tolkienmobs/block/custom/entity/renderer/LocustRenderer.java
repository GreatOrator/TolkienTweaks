package com.greatorator.tolkienmobs.block.custom.entity.renderer;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.block.custom.entity.LocustEntity;
import com.greatorator.tolkienmobs.block.custom.entity.layers.TolkienModelLayers;
import com.greatorator.tolkienmobs.block.custom.entity.model.LocustModel;
import com.greatorator.tolkienmobs.util.TolkienAnimationHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.DirectionalBlock;
import org.jetbrains.annotations.Nullable;

public class LocustRenderer implements BlockEntityRenderer<LocustEntity> {

	private final LocustModel locustModel;
	private static final ResourceLocation TEXTURE = TolkienMobsMain.getBlockModelTexture("locust.png");

	public LocustRenderer(BlockEntityRendererProvider.Context context) {
		this.locustModel = new LocustModel(context.bakeLayer(TolkienModelLayers.LOCUST));
	}

	@Override
	public void render(@Nullable LocustEntity entity, float partialTicks, PoseStack stack, MultiBufferSource buffer, int light, int overlay) {
		int yaw = entity != null ? entity.currentYaw : TolkienAnimationHelper.currentYaw;

		stack.pushPose();
		Direction facing = entity != null ? entity.getBlockState().getValue(DirectionalBlock.FACING) : Direction.NORTH;
		float randRot = entity != null ? entity.randRot : 0.0F;

		stack.translate(0.5F, 0.5F, 0.5F);
		stack.mulPose(facing.getRotation());
		stack.mulPose(Axis.ZP.rotationDegrees(180.0F));
		stack.mulPose(Axis.YP.rotationDegrees(180.0F + randRot));
		stack.mulPose(Axis.YN.rotationDegrees(yaw));

		VertexConsumer consumer = buffer.getBuffer(this.locustModel.renderType(TEXTURE));
		this.locustModel.renderToBuffer(stack, consumer, light, overlay);
		stack.popPose();
	}
}
