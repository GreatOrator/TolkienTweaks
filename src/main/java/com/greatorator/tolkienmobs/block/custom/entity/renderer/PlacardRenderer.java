package com.greatorator.tolkienmobs.block.custom.entity.renderer;


import com.greatorator.tolkienmobs.block.custom.PlacardBlock;
import com.greatorator.tolkienmobs.block.custom.entity.PlacardEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;

public class PlacardRenderer implements BlockEntityRenderer<PlacardEntity> {
    public PlacardRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(PlacardEntity te, float partialTicks, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay) {
        BlockState state = te.getBlockState();
        Direction facing = state.getValue(PlacardBlock.FACING);

        mStack.pushPose();
        mStack.translate(0.5, 0.5, 0.5);
        mStack.mulPose(new Quaternionf().rotationXYZ(90, 0, 0));

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

        mStack.popPose();
    }

}