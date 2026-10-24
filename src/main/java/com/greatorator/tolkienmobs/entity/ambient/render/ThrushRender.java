package com.greatorator.tolkienmobs.entity.ambient.render;

import com.geckolib.cache.model.BakedGeoModel;

import com.mojang.blaze3d.vertex.VertexConsumer;

import com.greatorator.tolkienmobs.entity.ambient.ThrushEntity;
import com.greatorator.tolkienmobs.entity.ambient.model.ThrushModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.renderer.GeoEntityRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class ThrushRender extends GeoEntityRenderer<ThrushEntity> {
    public ThrushRender(EntityRendererProvider.Context context) {
        super(context, new ThrushModel());
        this.shadowRadius = 0.25f;
    }

    @Override
    public ResourceLocation getTextureLocation(ThrushEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/birds/thrush.png");
    }

    @Override
    public void preRender(PoseStack poseStack, ThrushEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (!isReRender) {
            if(animatable.isBaby()) {
                poseStack.scale(0.45f, 0.45f, 0.45f);
            } else {
                poseStack.scale(1f, 1f, 1f);
            }
        }


        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}
