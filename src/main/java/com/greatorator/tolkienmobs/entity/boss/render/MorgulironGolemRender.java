package com.greatorator.tolkienmobs.entity.boss.render;

import com.greatorator.tolkienmobs.entity.boss.MorgulironGolemEntity;
import com.greatorator.tolkienmobs.entity.boss.model.MorgulironGolemModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class MorgulironGolemRender extends GeoEntityRenderer<MorgulironGolemEntity> {
    public MorgulironGolemRender(EntityRendererProvider.Context context) {
        super(context, new MorgulironGolemModel());
        this.shadowRadius = 1.0f;
    }

    @Override
    public ResourceLocation getTextureLocation(MorgulironGolemEntity goblinEntity) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/elementalgolem/elemental_golem_morgul.png");
    }

    @Override
    public void render(MorgulironGolemEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
