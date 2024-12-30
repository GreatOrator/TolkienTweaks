package com.greatorator.tolkienmobs.entity.monster.render;

import com.greatorator.tolkienmobs.entity.monster.HuronEntity;
import com.greatorator.tolkienmobs.entity.monster.model.HuronModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class HuronRender extends GeoEntityRenderer<HuronEntity> {
    public HuronRender(EntityRendererProvider.Context context) {
        super(context, new HuronModel());
        this.shadowRadius = 0.25f;
    }

    @Override
    public ResourceLocation getTextureLocation(HuronEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/huron.png");
    }

    @Override
    public RenderType getRenderType(HuronEntity livingEntity, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        ResourceLocation resourcelocation = this.getTextureLocation(livingEntity);

        return RenderType.itemEntityTranslucentCull(resourcelocation);
    }

    @Override
    public void render(HuronEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}