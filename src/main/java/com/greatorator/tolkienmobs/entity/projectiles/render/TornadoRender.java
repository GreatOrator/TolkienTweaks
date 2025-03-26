package com.greatorator.tolkienmobs.entity.projectiles.render;

import com.greatorator.tolkienmobs.entity.projectiles.TornadoEntity;
import com.greatorator.tolkienmobs.entity.projectiles.model.BoulderModel;
import com.greatorator.tolkienmobs.entity.projectiles.model.TornadoModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TornadoRender extends GeoEntityRenderer<TornadoEntity> {
    private BoulderModel model;

    public TornadoRender(EntityRendererProvider.Context context) {
        super(context, new TornadoModel());
    }

    @Override
    public void preRender(PoseStack poseStack, TornadoEntity animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (!animatable.isBlast()) {
            float scaleFactor = 1.25F;
            poseStack.scale(scaleFactor, scaleFactor, scaleFactor);
        } else {
            poseStack.mulPose(Axis.YP.rotationDegrees(animatable.getYRot() * ((float) Math.PI / 180F)));
        }

        if (animatable.lifeTime <= 1) {
            float scaleFactor = 0.0F;
            poseStack.scale(scaleFactor, scaleFactor, scaleFactor);
        } else {

        }
    }

    @Override
    public RenderType getRenderType(TornadoEntity livingEntity, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        ResourceLocation resourcelocation = this.getTextureLocation(animatable);

        return RenderType.itemEntityTranslucentCull(resourcelocation);    }

    @Override
    public ResourceLocation getTextureLocation(TornadoEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/ammo_tornado.png");
    }
}