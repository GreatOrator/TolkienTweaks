package com.greatorator.tolkienmobs.block.custom.entity.renderer;

import com.greatorator.tolkienmobs.block.custom.SignPostArmBlock;
import com.greatorator.tolkienmobs.block.custom.entity.SignPostArmBlockEntity;
import com.greatorator.tolkienmobs.block.custom.entity.renderer.state.SignPostArmRenderState;
import com.greatorator.tolkienmobs.faction.Places;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Writes an arm's destination and its distance on the board. The two lines are turned to face
 * whoever is looking, as a fingerpost's lettering reads from the road rather than from one side,
 * and they straddle the four pixel board they belong to.
 */
public class SignPostArmRenderer implements BlockEntityRenderer<SignPostArmBlockEntity, SignPostArmRenderState> {
    private static final float SCALE = 0.014F;
    private static final float LINE_HEIGHT = 9.0F;
    private static final int SEE_THROUGH_COLOR = 553648127;
    private static final int SEE_THROUGH_BACKGROUND = 0x20000000;
    private static final int INK_COLOR = 0x00FFFFFF;

    private final Font font;

    public SignPostArmRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.font();
    }

    @Override
    public SignPostArmRenderState createRenderState() {
        return new SignPostArmRenderState();
    }

    @Override
    public void extractRenderState(
            SignPostArmBlockEntity blockEntity,
            SignPostArmRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.destination = null;
        state.distance = null;
        state.level = blockEntity.getBlockState().getValue(SignPostArmBlock.LEVEL);

        String id = blockEntity.getDestination();
        if (id == null || id.isEmpty()) return;

        Places.Name name = Places.known(id);
        state.destination = name == null ? Component.literal(id) : Component.translatable(name.key());

        int distance = blockEntity.getDistance();
        if (distance >= 0) state.distance = Component.translatable("tolkienmobs.signpost.blocks", distance);
    }

    @Override
    public void submit(SignPostArmRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.destination == null) return;

        double height = (state.level * 4.0D + 2.0D) / 16.0D + 0.06D;
        poseStack.pushPose();
        poseStack.translate(0.5D, height, 0.5D);
        poseStack.mulPose(camera.orientation);
        poseStack.scale(SCALE, -SCALE, SCALE);

        float top = state.distance == null ? -LINE_HEIGHT / 2.0F : -LINE_HEIGHT;
        this.write(state.destination, top, state, poseStack, submitNodeCollector);
        if (state.distance != null) this.write(state.distance, 0.0F, state, poseStack, submitNodeCollector);

        poseStack.popPose();
    }

    private void write(Component line, float y, SignPostArmRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        FormattedCharSequence text = line.getVisualOrderText();
        float x = (float) -this.font.width(text) / 2.0F;

        submitNodeCollector.submitText(poseStack, x, y, text, false, Font.DisplayMode.SEE_THROUGH,
                state.lightCoords, SEE_THROUGH_COLOR, SEE_THROUGH_BACKGROUND, 0);
        submitNodeCollector.submitText(poseStack, x, y, text, true, Font.DisplayMode.NORMAL,
                state.lightCoords, INK_COLOR, 0, 0);
    }
}
