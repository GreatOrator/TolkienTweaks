package com.greatorator.tolkienmobs.entity.boss.render;

import com.greatorator.tolkienmobs.entity.boss.GoblinKingEntity;
import com.greatorator.tolkienmobs.entity.boss.model.GoblinKingModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class GoblinKingRender extends GeoEntityRenderer<GoblinKingEntity> {
    public GoblinKingRender(EntityRendererProvider.Context context) {
        super(context, new GoblinKingModel());
        this.shadowRadius = 0.50f;
    }

    @Override
    public ResourceLocation getTextureLocation(GoblinKingEntity goblinEntity) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/goblin/goblinking.png");
    }

    @Override
    public void render(GoblinKingEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
