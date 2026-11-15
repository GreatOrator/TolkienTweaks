package com.greatorator.tolkienmobs.block.custom.entity.renderer;

import com.greatorator.tolkienmobs.block.custom.SignPostArmBlock;
import com.greatorator.tolkienmobs.block.custom.entity.SignPostArmBlockEntity;
import com.greatorator.tolkienmobs.block.custom.entity.renderer.state.SignPostArmRenderState;
import com.greatorator.tolkienmobs.faction.Places;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Writes an arm's destinations and their distances on the boards themselves, in black, the way a fingerpost is
 * lettered rather than as a label hung in the air in front of the camera.
 * <p>
 * A board is four pixels tall and nineteen and a half long, so lettering is set on its two broad faces at a scale
 * small enough for two lines to fit the height, and shrunk further for a long name so a line never runs off the
 * end. Both faces are lettered, since which one a traveller reads depends on which way along the road they came.
 * A double arm's second board is lettered the same way, four pixels higher.
 */
public class SignPostArmRenderer implements BlockEntityRenderer<SignPostArmBlockEntity, SignPostArmRenderState> {
    /** Blocks per font pixel: two lines of eight pixels come to four board pixels. */
    private static final float SCALE = 0.013F;
    /** How much of the board's length the lettering may use, in blocks, leaving a little at each end. */
    private static final float REACH = 1.15F;
    /** The board's own ink: black, as asked for, rather than the white of a floating label. */
    private static final int INK = 0xFF000000;
    /** The board's shaft end sits eight pixels behind the cell's middle, and the lettering starts two in. */
    private static final float PLANK_START = -8.0F / 16.0F;
    private static final float MARGIN = 2.0F / 16.0F;
    /** Just clear of each broad face of the one pixel thick board, which spans x -1 to 0 of the cell. */
    private static final float EAST_FACE = 0.002F;
    private static final float WEST_FACE = 0.0625F + 0.002F;

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
        state.destination2 = null;
        state.distance2 = null;
        state.level = blockEntity.getBlockState().getValue(SignPostArmBlock.LEVEL);
        state.facing = blockEntity.getBlockState().getValue(SignPostArmBlock.FACING);

        state.destination = label(blockEntity.getDestination());
        if (state.destination != null && blockEntity.getDistance() >= 0) {
            state.distance = Component.translatable("tolkienmobs.signpost.blocks", blockEntity.getDistance());
        }

        state.destination2 = label(blockEntity.getDestination(1));
        if (state.destination2 != null && blockEntity.getDistance(1) >= 0) {
            state.distance2 = Component.translatable("tolkienmobs.signpost.blocks", blockEntity.getDistance(1));
        }
    }

    /** A place's name, or null when the board points at nothing. */
    private static @Nullable Component label(String id) {
        if (id == null || id.isEmpty()) return null;
        Places.Name name = Places.known(id);
        return name == null ? Component.literal(id) : Component.translatable(name.key());
    }

    @Override
    public void submit(SignPostArmRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        float height = (state.level * 4.0F + 2.0F) / 16.0F;
        if (state.destination != null) {
            this.paint(state, poseStack, submitNodeCollector, height, lines(state.destination, state.distance));
        }

        // A double arm can have its upper board lettered on its own, so each board is drawn for itself.
        if (state.destination2 != null) {
            // The upper board of a double arm: the same board, four pixels up.
            this.paint(state, poseStack, submitNodeCollector, height + 4.0F / 16.0F,
                    lines(state.destination2, state.distance2));
        }
    }

    private static List<Component> lines(Component destination, @Nullable Component distance) {
        List<Component> lines = new ArrayList<>(2);
        lines.add(destination);
        if (distance != null) lines.add(distance);
        return lines;
    }

    /** Letters one face of a board: the pose is taken to the board's middle, turned onto that face, then scaled. */
    private void paint(SignPostArmRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
                       float height, List<Component> lines) {
        List<FormattedCharSequence> set = new ArrayList<>(lines.size());
        float widest = 0.0F;
        for (Component line : lines) {
            FormattedCharSequence text = line.getVisualOrderText();
            set.add(text);
            widest = Math.max(widest, (float) this.font.width(text));
        }
        // A long name shrinks until it fits the board; a short one keeps the fixed scale.
        float scale = widest <= 0.0F ? SCALE : Math.min(SCALE, REACH / widest);

        this.paintFace(state, poseStack, submitNodeCollector, set, height, scale, 90.0F, EAST_FACE, true);
        this.paintFace(state, poseStack, submitNodeCollector, set, height, scale, -90.0F, WEST_FACE, false);
    }

    /**
     * Letters one face of a board. The pose is set down at the board's shaft end, so the words start where the
     * board meets the post and run out toward the arrow rather than sitting in the middle of it; the two faces
     * read along the board in opposite directions, so the one whose letters run back toward the shaft is drawn
     * from the end of the string instead, and both then hug the same end of the wood.
     */
    private void paintFace(SignPostArmRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
                           List<FormattedCharSequence> set, float height, float scale, float turn, float offset,
                           boolean fromEnd) {
        poseStack.pushPose();
        poseStack.translate(0.5D, height, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw(state.facing)));
        poseStack.translate(0.0D, 0.0D, PLANK_START + MARGIN);
        poseStack.mulPose(Axis.YP.rotationDegrees(turn));
        poseStack.translate(0.0D, 0.0D, offset);
        poseStack.scale(scale, -scale, scale);

        float top = set.size() > 1 ? -4.5F : 0.0F;
        for (int i = 0; i < set.size(); i++) {
            FormattedCharSequence text = set.get(i);
            float start = fromEnd ? (float) -this.font.width(text) : 0.0F;
            submitNodeCollector.submitText(poseStack, start, top + i * 9.0F,
                    text, false, Font.DisplayMode.NORMAL, state.lightCoords, INK, 0, 0);
        }

        poseStack.popPose();
    }

    /** The turn the block state gives the model, so the lettering turns with the board. */
    private static float yaw(Direction facing) {
        return switch (facing) {
            case EAST -> 90.0F;
            case SOUTH -> 0.0F;
            case WEST -> 270.0F;
            default -> 180.0F;
        };
    }
}
