package com.greatorator.tolkienmobs.entity.passive.render;

import com.geckolib.cache.model.BakedGeoModel;

import com.mojang.blaze3d.vertex.VertexConsumer;

import com.greatorator.tolkienmobs.entity.passive.NazgulSteedEntity;
import com.greatorator.tolkienmobs.entity.passive.model.NazgulSteedModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.renderer.GeoEntityRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class NazgulSteedRender extends GeoEntityRenderer<NazgulSteedEntity> {
    public NazgulSteedRender(EntityRendererProvider.Context context) {
        super(context, new NazgulSteedModel());
        this.shadowRadius = 1.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(NazgulSteedEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/horse/nazgulsteed.png");
    }

    @Override
    public void preRender(PoseStack poseStack, NazgulSteedEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (!isReRender) {
            if(animatable.isBaby()) {
                poseStack.scale(0.5F, 0.5F, 0.5F);
            }
        }


        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    public RenderType getRenderType(NazgulSteedEntity livingEntity, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        ResourceLocation resourcelocation = this.getTextureLocation(livingEntity);

        return RenderType.entityCutoutNoCull(resourcelocation);
    }
}
