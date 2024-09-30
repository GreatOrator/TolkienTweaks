package com.greatorator.tolkienmobs.block.custom.entity.renderer;


import com.greatorator.tolkienmobs.block.custom.TrinketTableBlock;
import com.greatorator.tolkienmobs.block.custom.entity.TrinketTableEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;

public class TrinketTableRenderer implements BlockEntityRenderer<TrinketTableEntity> {
    public TrinketTableRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(TrinketTableEntity te, float partialTicks, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay) {
        ItemStack stack1 = te.itemHandler.getStackInSlot(0);
        ItemStack stack2 = te.itemHandler.getStackInSlot(1);
        ItemStack stack3 = te.itemHandler.getStackInSlot(2);
        ItemStack stack4 = te.itemHandler.getStackInSlot(3);
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        if (stack1.isEmpty() && stack2.isEmpty() && stack3.isEmpty() && stack4.isEmpty()) return;
        BlockState state = te.getBlockState();
        Direction facing = state.getValue(TrinketTableBlock.FACING);
        mStack.pushPose();

        switch (facing) {
            case NORTH:
                mStack.mulPose(new Quaternionf().rotationXYZ(0, 0, 90));
                break;
            case SOUTH:
                mStack.mulPose(new Quaternionf().rotationXYZ(0, 0, -90));
                break;
            case WEST:
                mStack.mulPose(new Quaternionf().rotationXYZ(0, 0, 90));
                break;
            case EAST:
                mStack.mulPose(new Quaternionf().rotationXYZ(0, 0, 180));
                break;
        }

        mStack.scale(0.2F, 0.2F, 0.2F);

        if (!stack1.isEmpty()) {
            mStack.pushPose();
            mStack.translate(2.00, -5.28, 3.41);
            mStack.mulPose(new Quaternionf().rotationXYZ(0,0,135));
            mStack.mulPose(Axis.XP.rotationDegrees(te.getRenderingRotation()));
            itemRenderer.renderStatic(stack1, ItemDisplayContext.FIXED, getLightLevel(te.getLevel(), te.getBlockPos()), packedOverlay, mStack, getter, te.getLevel(), 1);
            mStack.popPose();
        }

        if (!stack2.isEmpty()) {
            mStack.pushPose();
            mStack.translate(2.00, -5.28, 1.49);
            mStack.mulPose(new Quaternionf().rotationXYZ(0,0,-20));
            mStack.mulPose(Axis.YP.rotationDegrees(te.getRenderingRotation()));
            itemRenderer.renderStatic(stack2, ItemDisplayContext.FIXED, getLightLevel(te.getLevel(), te.getBlockPos()), packedOverlay, mStack, getter, te.getLevel(), 0);
            mStack.popPose();
        }

        if (!stack3.isEmpty()) {
            mStack.pushPose();
            mStack.translate(1.95,-5.10,2.49);
            mStack.mulPose(Axis.XP.rotationDegrees(te.getRenderingRotation()));
            itemRenderer.renderStatic(stack3, ItemDisplayContext.FIXED, getLightLevel(te.getLevel(), te.getBlockPos()), packedOverlay, mStack, getter, te.getLevel(), 0);
            mStack.popPose();
        }

        if (!stack4.isEmpty()) {
            mStack.scale(1.5F, 1.5F, 1.5F);
            mStack.translate(0.00, 0.00, 0.00);
            itemRenderer.renderStatic(stack4, ItemDisplayContext.FIXED, getLightLevel(te.getLevel(), te.getBlockPos()), packedOverlay, mStack, getter, te.getLevel(), 0);
            mStack.popPose();
        }
        mStack.popPose();
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }
}