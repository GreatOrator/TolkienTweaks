package com.greatorator.tolkienmobs.entity.ambient.render;

import com.geckolib.cache.model.BakedGeoModel;

import com.mojang.blaze3d.vertex.VertexConsumer;

import com.greatorator.tolkienmobs.entity.ambient.CrebainEntity;
import com.greatorator.tolkienmobs.entity.ambient.model.CrebainModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.renderer.GeoEntityRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class CrebainRender extends GeoEntityRenderer<CrebainEntity> {
    public CrebainRender(EntityRendererProvider.Context context) {
        super(context, new CrebainModel());
        this.shadowRadius = 0.25f;
    }

    @Override
    public ResourceLocation getTextureLocation(CrebainEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/birds/crebain.png");
    }

    @Override
    public void preRender(PoseStack poseStack, CrebainEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
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
