package com.greatorator.tolkienmobs.entity.passive.render;

import com.greatorator.tolkienmobs.entity.passive.NazgulSteedEntity;
import com.greatorator.tolkienmobs.entity.passive.model.NazgulSteedModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

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
    public void render(NazgulSteedEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if(entity.isBaby()) {
            poseStack.scale(0.5F, 0.5F, 0.5F);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public RenderType getRenderType(NazgulSteedEntity livingEntity, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        ResourceLocation resourcelocation = this.getTextureLocation(livingEntity);

        return RenderType.entityCutoutNoCull(resourcelocation);
    }
}