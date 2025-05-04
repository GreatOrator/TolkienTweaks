package com.greatorator.tolkienmobs.block.custom.entity.renderer;

import com.greatorator.tolkienmobs.block.custom.MilestoneBlock;
import com.greatorator.tolkienmobs.block.custom.entity.MilestoneBlockEntity;
import com.greatorator.tolkienmobs.handler.MilestoneHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;

import static com.greatorator.tolkienmobs.block.custom.MilestoneBlock.LIT;

public class MilestoneRender implements BlockEntityRenderer<MilestoneBlockEntity> {
    public MilestoneRender(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(MilestoneBlockEntity te, float partialTicks, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay) {
        BlockState state = te.getBlockState();
        Direction facing = state.getValue(MilestoneBlock.FACING);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();

        mStack.pushPose();
        mStack.translate(0.5, 0.5, 0.5);
        mStack.mulPose(new Quaternionf().rotationXYZ(90, 0, 0));

        switch (facing) {
            case NORTH, WEST:
                mStack.mulPose(new Quaternionf().rotationXYZ(0, 0, 90));
                break;
            case SOUTH:
                mStack.mulPose(new Quaternionf().rotationXYZ(0, 0, -90));
                break;
            case EAST:
                mStack.mulPose(new Quaternionf().rotationXYZ(0, 0, 180));
                break;
        }
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        mStack.popPose();

        if (MilestoneHandler.isKnownByClient(te.getUUID(), Minecraft.getInstance().player.getUUID())) {
        Component.nullToEmpty(te.milestoneSettings.name);
            if (te.getBlockState().getValue(LIT)) {
                drawNameString(mStack, getter, Component.nullToEmpty(te.getMilestoneSettings().name), packedLight);
            }
        }
    }

    private void drawNameString(PoseStack poseStack, MultiBufferSource buffer, Component message, int light) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        float scale = 0.015F;
        int alpha = (int) (0.4F * 255.0F) << 24;
        float width = ((float) -font.width(message) / 2);


        poseStack.pushPose();
        poseStack.translate(0.5D, 2.0D, 0.5D);
        poseStack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(scale, -scale, scale);

        font.drawInBatch(
                message,
                width,
                0.0F,
                553648127,
                false,
                poseStack.last().pose(),
                buffer,
                Font.DisplayMode.SEE_THROUGH,
                alpha,
                light
        );
        font.drawInBatch(message,
                width,
                0.0F,
                65535,
                true,
                poseStack.last().pose(),
                buffer,
                Font.DisplayMode.NORMAL,
                0,
                light);

        poseStack.popPose();
    }
}
