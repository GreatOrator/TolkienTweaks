package com.greatorator.tolkienmobs.entity.projectiles.render;

import com.geckolib.cache.model.BakedGeoModel;

import com.mojang.blaze3d.vertex.VertexConsumer;

import com.greatorator.tolkienmobs.entity.projectiles.SimpleTrapEntity;
import com.greatorator.tolkienmobs.entity.projectiles.model.SimpleTrapModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.renderer.GeoEntityRenderer;

public class SimpleTrapRender extends GeoEntityRenderer<SimpleTrapEntity> {
    public SimpleTrapRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SimpleTrapModel<SimpleTrapEntity>());
    }

    @Override
    public void preRender(PoseStack poseStack, SimpleTrapEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (!isReRender) {
            float scaleFactor = 2.0F;
            if (animatable.lifeTime <= 1) {
                scaleFactor = 0.0F;
            } else {
                scaleFactor = 2.0F;
            }
            poseStack.scale(scaleFactor, scaleFactor, scaleFactor);        }


        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    public RenderType getRenderType(SimpleTrapEntity livingEntity, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        ResourceLocation resourcelocation = this.getTextureLocation(livingEntity);

        return RenderType.itemEntityTranslucentCull(resourcelocation);
    }
}
