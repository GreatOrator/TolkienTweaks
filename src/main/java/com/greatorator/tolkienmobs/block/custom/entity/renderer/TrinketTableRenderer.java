package com.greatorator.tolkienmobs.block.custom.entity.renderer;

import com.greatorator.tolkienmobs.block.custom.TrinketTableBlock;
import com.greatorator.tolkienmobs.block.custom.entity.TrinketTableBlockEntity;
import com.greatorator.tolkienmobs.block.custom.entity.renderer.state.TrinketTableRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

public class TrinketTableRenderer implements BlockEntityRenderer<TrinketTableBlockEntity, TrinketTableRenderState> {
    private static final int[] SEEDS = {1, 0, 0, 0};

    private final ItemModelResolver itemModelResolver;

    public TrinketTableRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public TrinketTableRenderState createRenderState() {
        return new TrinketTableRenderState();
    }

    @Override
    public void extractRenderState(
            TrinketTableBlockEntity blockEntity,
            TrinketTableRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.facing = blockEntity.getBlockState().getValue(TrinketTableBlock.FACING);
        state.rotation = blockEntity.getRenderingRotation();

        for (int slot = 0; slot < TrinketTableRenderState.SLOTS; slot++) {
            this.itemModelResolver.updateForTopItem(
                    state.items[slot],
                    blockEntity.itemHandler.getStackInSlot(slot),
                    ItemDisplayContext.FIXED,
                    blockEntity.getLevel(),
                    null,
                    SEEDS[slot]
            );
        }
    }

    @Override
    public void submit(TrinketTableRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        ItemStackRenderState[] items = state.items;
        if (items[0].isEmpty() && items[1].isEmpty() && items[2].isEmpty() && items[3].isEmpty()) return;

        poseStack.pushPose();

        switch (state.facing) {
            case NORTH, WEST:
                poseStack.mulPose(new Quaternionf().rotationXYZ(0, 0, 90));
                break;
            case SOUTH:
                poseStack.mulPose(new Quaternionf().rotationXYZ(0, 0, -90));
                break;
            case EAST:
                poseStack.mulPose(new Quaternionf().rotationXYZ(0, 0, 180));
                break;
        }

        poseStack.scale(0.2F, 0.2F, 0.2F);

        if (!items[0].isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(2.00, -5.28, 3.41);
            poseStack.mulPose(new Quaternionf().rotationXYZ(0, 0, 135));
            poseStack.mulPose(Axis.XP.rotationDegrees(state.rotation));
            items[0].submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }

        if (!items[1].isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(2.00, -5.28, 1.49);
            poseStack.mulPose(new Quaternionf().rotationXYZ(0, 0, -20));
            poseStack.mulPose(Axis.YP.rotationDegrees(state.rotation));
            items[1].submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }

        if (!items[2].isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(1.95, -5.10, 2.49);
            poseStack.mulPose(Axis.XP.rotationDegrees(state.rotation));
            items[2].submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }

        if (!items[3].isEmpty()) {
            poseStack.pushPose();
            poseStack.scale(1.5F, 1.5F, 1.5F);
            items[3].submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }

        poseStack.popPose();
    }
}
