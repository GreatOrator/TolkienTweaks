package com.greatorator.tolkienmobs.block.custom.entity.renderer;

import com.greatorator.tolkienmobs.block.custom.entity.NoticeBoardBlockEntity;
import com.greatorator.tolkienmobs.block.custom.entity.renderer.state.NoticeBoardMarkRenderState;
import com.greatorator.tolkienmobs.client.BoardMarks;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Hangs the board's mark in the air over it, the way the same mark hangs over a giver's head.
 * <p>
 * It is submitted as a nameplate rather than as geometry, which is the one thing that makes this cheap:
 * the collector already turns a nameplate to face the camera, scales it with distance and hides it behind
 * the ground, so nothing here has to billboard, cull or fade anything itself. The board's mark is then
 * the same object as the mark on a plate - a glyph of the mod's own font - so the two cannot drift apart.
 * <p>
 * The board's frame stands two blocks tall and slightly over its cell, so the label is set above the
 * wood rather than in it.
 */
public class NoticeBoardMarkRenderer implements BlockEntityRenderer<NoticeBoardBlockEntity, NoticeBoardMarkRenderState> {
    /** How far over the block's own corner the mark hangs: clear of the frame's top rail at two blocks. */
    private static final double LIFT = 2.35D;

    public NoticeBoardMarkRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public NoticeBoardMarkRenderState createRenderState() {
        return new NoticeBoardMarkRenderState();
    }

    @Override
    public void extractRenderState(NoticeBoardBlockEntity blockEntity, NoticeBoardMarkRenderState state,
                                   float partialTicks, Vec3 cameraPosition,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.mark = BoardMarks.mark();
    }

    @Override
    public void submit(NoticeBoardMarkRenderState state, PoseStack poseStack,
                       SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.mark == null) return;
        double distanceSq = camera.pos.distanceToSqr(Vec3.atCenterOf(state.blockPos));
        poseStack.pushPose();
        poseStack.translate(0.5D, LIFT, 0.5D);
        // The zero is the same zero a nameplate passes: it is what every plate in the world is drawn with,
        // so the board's mark and a giver's mark are submitted by the same call with the same arguments.
        submitNodeCollector.submitNameTag(poseStack, Vec3.ZERO, 0, state.mark, false,
                state.lightCoords, distanceSq, camera);
        poseStack.popPose();
    }
}
