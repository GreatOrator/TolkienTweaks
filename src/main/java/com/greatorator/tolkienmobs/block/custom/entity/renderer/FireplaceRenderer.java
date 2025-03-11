package com.greatorator.tolkienmobs.block.custom.entity.renderer;


import com.greatorator.tolkienmobs.block.custom.FireplaceBlock;
import com.greatorator.tolkienmobs.block.custom.entity.FireplaceBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;

public class FireplaceRenderer implements BlockEntityRenderer<FireplaceBlockEntity> {
    public FireplaceRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(FireplaceBlockEntity te, float partialTicks, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay) {
        ItemStack stack1 = te.itemHandler.getStackInSlot(0);
        ItemStack stack2 = te.itemHandler.getStackInSlot(1);
        ItemStack stack3 = te.outputHandler.getStackInSlot(0);
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        if (stack1.isEmpty() && stack2.isEmpty() && stack3.isEmpty()) return;

        BlockState state = te.getBlockState();
        Direction facing = state.getValue(FireplaceBlock.FACING);

        mStack.pushPose();

        switch (facing) {
            case NORTH:
                mStack.mulPose(new Quaternionf().rotationXYZ(55, 0, 180));
                mStack.translate(-0.5, 0.1, 1.155);
                break;
            case SOUTH:
                mStack.mulPose(new Quaternionf().rotationXYZ(55, 0, -180));
                mStack.translate(-1.2, -0.6, 1.155);
                break;
            case EAST:
                mStack.mulPose(new Quaternionf().rotationXYZ(-55, 0, 135));
                mStack.translate(-0.975, -1.05, 0);
                break;
            case WEST:
                mStack.mulPose(new Quaternionf().rotationXYZ(-55, 0, 0));
                break;
        }


        if (!stack1.isEmpty()) {
            mStack.pushPose();
            mStack.scale(0.6F, 0.6F, 0.6F);
            mStack.translate(0.975, 0.35, -0.95);
            itemRenderer.renderStatic(stack1, ItemDisplayContext.GROUND, getLightLevel(te.getLevel(), te.getBlockPos()), packedOverlay, mStack, getter, te.getLevel(), 1);
            mStack.popPose();
        }

        if (!stack2.isEmpty()) {
            mStack.pushPose();
            mStack.scale(0.6F, 0.6F, 0.6F);
            mStack.translate(0.975, 1.0, -0.95);
            itemRenderer.renderStatic(stack2, ItemDisplayContext.GROUND, getLightLevel(te.getLevel(), te.getBlockPos()), packedOverlay, mStack, getter, te.getLevel(), 1);
            mStack.popPose();
        }

        if (!stack3.isEmpty()) {
            mStack.pushPose();
            mStack.scale(0.6F, 0.6F, 0.6F);
            mStack.translate(0.375, 0.775, -0.95);
            itemRenderer.renderStatic(stack3, ItemDisplayContext.GROUND, getLightLevel(te.getLevel(), te.getBlockPos()), packedOverlay, mStack, getter, te.getLevel(), 1);
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