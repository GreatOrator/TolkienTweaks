package com.greatorator.tolkienmobs.entity.monster.render;

import com.greatorator.tolkienmobs.entity.monster.MinotaurEntity;
import com.greatorator.tolkienmobs.entity.monster.model.MinotaurModel;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

import javax.annotation.Nullable;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class MinotaurRender extends GeoEntityRenderer<MinotaurEntity> {
    public MinotaurRender(EntityRendererProvider.Context context) {
        super(context, new MinotaurModel());
        this.shadowRadius = 0.50f;
        addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @Nullable
            @Override
            protected ItemStack getStackForBone(GeoBone bone, MinotaurEntity animatable) {
                return switch (bone.getName()) { case "rightArm" -> new ItemStack(TolkienItems.AXE_MORGULIRON.get()); default -> null; };
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, MinotaurEntity animatable) {
                return switch (bone.getName()) { default -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND; };
            }

            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, MinotaurEntity animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                poseStack.mulPose(Axis.XP.rotationDegrees(-40));
                poseStack.mulPose(Axis.YP.rotationDegrees(-90));
                poseStack.mulPose(Axis.ZP.rotationDegrees(90));
                poseStack.translate(-0.5D, 0.75D, 0.0D);
                poseStack.scale(2.5F, 2.5F, 2.5F);
                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(MinotaurEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/minotaur.png");
    }

    @Override
    public RenderType getRenderType(MinotaurEntity livingEntity, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        ResourceLocation resourcelocation = this.getTextureLocation(livingEntity);

        return RenderType.itemEntityTranslucentCull(resourcelocation);
    }

    @Override
    public void render(MinotaurEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}