package com.greatorator.tolkienmobs.block.custom.entity.renderer;

import com.greatorator.tolkienmobs.block.custom.MilestoneBlock;
import com.greatorator.tolkienmobs.block.custom.entity.MilestoneBlockEntity;
import com.greatorator.tolkienmobs.block.custom.entity.renderer.state.MilestoneRenderState;
import com.greatorator.tolkienmobs.handler.MilestoneHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import static com.greatorator.tolkienmobs.block.custom.MilestoneBlock.LIT;

public class MilestoneRender implements BlockEntityRenderer<MilestoneBlockEntity, MilestoneRenderState> {
    private static final float SCALE = 0.015F;
    private static final int SEE_THROUGH_COLOR = 553648127;
    private static final int SEE_THROUGH_BACKGROUND = 0x20000000;
    private static final int NAME_COLOR = 65535;

    private final Font font;

    public MilestoneRender(BlockEntityRendererProvider.Context context) {
        this.font = context.font();
    }

    @Override
    public MilestoneRenderState createRenderState() {
        return new MilestoneRenderState();
    }

    @Override
    public void extractRenderState(
            MilestoneBlockEntity blockEntity,
            MilestoneRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.facing = blockEntity.getBlockState().getValue(MilestoneBlock.FACING);
        state.name = null;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !blockEntity.getBlockState().getValue(LIT)) return;
        if (!MilestoneHandler.isKnownByClient(blockEntity.getUUID(), player.getUUID())) return;

        state.name = Component.nullToEmpty(blockEntity.getMilestoneSettings().name);
    }

    @Override
    public void submit(MilestoneRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.name == null) return;

        FormattedCharSequence name = state.name.getVisualOrderText();
        float width = (float) -this.font.width(name) / 2.0F;

        poseStack.pushPose();
        poseStack.translate(0.5D, 2.0D, 0.5D);
        poseStack.mulPose(camera.orientation);
        poseStack.scale(SCALE, -SCALE, SCALE);

        submitNodeCollector.submitText(
                poseStack,
                width,
                0.0F,
                name,
                false,
                Font.DisplayMode.SEE_THROUGH,
                state.lightCoords,
                SEE_THROUGH_COLOR,
                SEE_THROUGH_BACKGROUND,
                0
        );
        submitNodeCollector.submitText(
                poseStack,
                width,
                0.0F,
                name,
                true,
                Font.DisplayMode.NORMAL,
                state.lightCoords,
                NAME_COLOR,
                0,
                0
        );

        poseStack.popPose();
    }
}
