package com.greatorator.tolkienmobs.entity.passive.render;

import com.greatorator.tolkienmobs.entity.passive.GollumEntity;
import com.greatorator.tolkienmobs.entity.passive.model.GollumModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class GollumRender extends GeoEntityRenderer<GollumEntity> {
    public GollumRender(EntityRendererProvider.Context context) {
        super(context, new GollumModel());
        this.shadowRadius = 0.25f;
    }

    @Override
    public ResourceLocation getTextureLocation(GollumEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/gollum.png");
    }

    @Override
    public void render(GollumEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}