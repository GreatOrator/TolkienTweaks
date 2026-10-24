package com.greatorator.tolkienmobs.block.custom.entity.renderer;

import com.greatorator.tolkienmobs.block.custom.FireplaceBlock;
import com.greatorator.tolkienmobs.block.custom.entity.FireplaceBlockEntity;
import com.greatorator.tolkienmobs.block.custom.entity.renderer.state.FireplaceRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

public class FireplaceRenderer implements BlockEntityRenderer<FireplaceBlockEntity, FireplaceRenderState> {
    private static final int SEED = 1;

    private final ItemModelResolver itemModelResolver;

    public FireplaceRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public FireplaceRenderState createRenderState() {
        return new FireplaceRenderState();
    }

    @Override
    public void extractRenderState(
            FireplaceBlockEntity blockEntity,
            FireplaceRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.facing = blockEntity.getBlockState().getValue(FireplaceBlock.FACING);

        ItemStack[] stacks = {
                blockEntity.itemHandler.getStackInSlot(0),
                blockEntity.itemHandler.getStackInSlot(1),
                blockEntity.outputHandler.getStackInSlot(0)
        };

        for (int slot = 0; slot < FireplaceRenderState.SLOTS; slot++) {
            this.itemModelResolver.updateForTopItem(
                    state.items[slot],
                    stacks[slot],
                    ItemDisplayContext.GROUND,
                    blockEntity.getLevel(),
                    null,
                    SEED
            );
        }
    }

    @Override
    public void submit(FireplaceRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        ItemStackRenderState[] items = state.items;
        if (items[0].isEmpty() && items[1].isEmpty() && items[2].isEmpty()) return;

        poseStack.pushPose();

        switch (state.facing) {
            case NORTH:
                poseStack.mulPose(new Quaternionf().rotationXYZ(55, 0, 180));
                poseStack.translate(-0.5, 0.1, 1.155);
                break;
            case SOUTH:
                poseStack.mulPose(new Quaternionf().rotationXYZ(55, 0, -180));
                poseStack.translate(-1.2, -0.6, 1.155);
                break;
            case EAST:
                poseStack.mulPose(new Quaternionf().rotationXYZ(-55, 0, 135));
                poseStack.translate(-0.975, -1.05, 0);
                break;
            case WEST:
                poseStack.mulPose(new Quaternionf().rotationXYZ(-55, 0, 0));
                break;
        }

        submitItem(items[0], 0.975, 0.35, poseStack, submitNodeCollector, state.lightCoords);
        submitItem(items[1], 0.975, 1.0, poseStack, submitNodeCollector, state.lightCoords);
        submitItem(items[2], 0.375, 0.775, poseStack, submitNodeCollector, state.lightCoords);

        poseStack.popPose();
    }

    private static void submitItem(
            ItemStackRenderState item,
            double x,
            double y,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int lightCoords
    ) {
        if (item.isEmpty()) return;

        poseStack.pushPose();
        poseStack.scale(0.6F, 0.6F, 0.6F);
        poseStack.translate(x, y, -0.95);
        item.submit(poseStack, submitNodeCollector, lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }
}
