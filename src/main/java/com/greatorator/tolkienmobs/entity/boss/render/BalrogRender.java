package com.greatorator.tolkienmobs.entity.boss.render;

import com.greatorator.tolkienmobs.entity.boss.BalrogEntity;
import com.greatorator.tolkienmobs.entity.boss.model.BalrogModel;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

import javax.annotation.Nullable;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class BalrogRender extends GeoEntityRenderer<BalrogEntity> {
    public BalrogRender(EntityRendererProvider.Context context) {
        super(context, new BalrogModel());
        this.shadowRadius = 1.0f;
        addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @Nullable
            @Override
            protected ItemStack getStackForBone(GeoBone bone, BalrogEntity animatable) {
                return switch (bone.getName()) { case "rightArm" -> new ItemStack(TolkienItems.WHIP_FIRE.get()); default -> null; };
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, BalrogEntity animatable) {
                return switch (bone.getName()) { default -> ItemDisplayContext.FIRST_PERSON_RIGHT_HAND; };
            }

            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, BalrogEntity animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                poseStack.mulPose(Axis.XP.rotationDegrees(-90));
                poseStack.mulPose(Axis.YP.rotationDegrees(0));
                poseStack.mulPose(Axis.ZP.rotationDegrees(0));
                poseStack.translate(-0.2D, 0.55D, -2.0D);
                poseStack.scale(1.0F, 1.0F, 1.0F);
                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(BalrogEntity goblinEntity) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/balrog.png");
    }

    @Override
    public void render(BalrogEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
