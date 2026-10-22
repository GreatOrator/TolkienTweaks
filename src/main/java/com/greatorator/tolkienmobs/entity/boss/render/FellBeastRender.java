package com.greatorator.tolkienmobs.entity.boss.render;

import software.bernie.geckolib.cache.object.BakedGeoModel;

import com.mojang.blaze3d.vertex.VertexConsumer;

import com.greatorator.tolkienmobs.entity.boss.FellBeastEntity;
import com.greatorator.tolkienmobs.entity.boss.model.FellBeastModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class FellBeastRender extends GeoEntityRenderer<FellBeastEntity> {
    public FellBeastRender(EntityRendererProvider.Context context) {
        super(context, new FellBeastModel());
        this.shadowRadius = 2.1f;
    }

    @Override
    public ResourceLocation getTextureLocation(FellBeastEntity goblinEntity) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/fellbeast/fellbeast.png");
    }

    @Override
    public void preRender(PoseStack poseStack, FellBeastEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (!isReRender) {
            poseStack.scale(1.5F, 1.5F, 1.5F);        }


        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}
