package com.greatorator.tolkienmobs.block.custom.entity.renderer;


import com.greatorator.tolkienmobs.block.custom.FireplaceBlock;
import com.greatorator.tolkienmobs.block.custom.entity.FireplaceBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
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
        ItemStack stack3 = te.itemHandler.getStackInSlot(3);
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        if (stack1.isEmpty() && stack2.isEmpty() && stack3.isEmpty()) return;

        BlockState state = te.getBlockState();
        Direction facing = state.getValue(FireplaceBlock.FACING);
        ItemStack bars = new ItemStack(Blocks.IRON_BARS);

        mStack.pushPose();
        mStack.translate(0.5, 0.5, 0.5);
        mStack.mulPose(new Quaternionf().rotationXYZ(29.8F, (float) 0, 9.885F));

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

        mStack.scale(0.85F, 0.85F, 0.85F);
        itemRenderer.renderStatic(bars, ItemDisplayContext.NONE, getLightLevel(te.getLevel(), te.getBlockPos()), packedOverlay, mStack, getter, te.getLevel(), 1);

        mStack.translate(0, 0, 0.04);
        mStack.translate(0.187, -0.3, 0);
        if (!stack1.isEmpty()) {
            itemRenderer.renderStatic(stack1, ItemDisplayContext.GROUND, getLightLevel(te.getLevel(), te.getBlockPos()), packedOverlay, mStack, getter, te.getLevel(), 1);
        }

        mStack.translate(-0.2, 0.4, 0);
        if (!stack2.isEmpty()) {
            itemRenderer.renderStatic(stack2, ItemDisplayContext.GROUND, getLightLevel(te.getLevel(), te.getBlockPos()), packedOverlay, mStack, getter, te.getLevel(), 1);
        }

        mStack.translate(-0.2, -0.4, 0.5);
        mStack.mulPose(new Quaternionf().rotationXYZ(0, 0, 90));
        if (!stack3.isEmpty()) {
            itemRenderer.renderStatic(stack3, ItemDisplayContext.GROUND, getLightLevel(te.getLevel(), te.getBlockPos()), packedOverlay, mStack, getter, te.getLevel(), 1);
        }

        mStack.popPose();
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }
}