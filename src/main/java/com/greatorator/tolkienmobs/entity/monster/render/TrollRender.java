package com.greatorator.tolkienmobs.entity.monster.render;

import com.google.common.collect.Maps;
import com.greatorator.tolkienmobs.entity.monster.TrollEntity;
import com.greatorator.tolkienmobs.entity.monster.model.TrollModel;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

import javax.annotation.Nullable;
import java.util.Map;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TrollRender extends GeoEntityRenderer<TrollEntity> {
    public static final Map<TolkienVariant, ResourceLocation> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(TolkienVariant.class), map -> {
                map.put(TolkienVariant.DEFAULT,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll1.png"));
                map.put(TolkienVariant.RED,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll1.png"));
                map.put(TolkienVariant.ORANGE,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll2.png"));
                map.put(TolkienVariant.YELLOW,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll3.png"));
                map.put(TolkienVariant.GREEN,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll4.png"));
                map.put(TolkienVariant.BLUE,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll1.png"));
                map.put(TolkienVariant.INDIGO,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll2.png"));
                map.put(TolkienVariant.VIOLET,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll3.png"));
                map.put(TolkienVariant.MAGENTA,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll4.png"));
                map.put(TolkienVariant.PINK,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll1.png"));
                map.put(TolkienVariant.GRAY,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll2.png"));
                map.put(TolkienVariant.AQUA,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll3.png"));
                map.put(TolkienVariant.BEIGE,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll4.png"));
                map.put(TolkienVariant.BROWN,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll1.png"));
                map.put(TolkienVariant.CORAL,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll2.png"));
                map.put(TolkienVariant.CYAN,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll3.png"));
                map.put(TolkienVariant.LAVENDER,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll4.png"));
            });
    
    public TrollRender(EntityRendererProvider.Context context) {
        super(context, new TrollModel());
        this.shadowRadius = 0.50f;
        addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @Nullable
            @Override
            protected ItemStack getStackForBone(GeoBone bone, TrollEntity animatable) {
                return switch (bone.getName()) { case "rightArm" -> new ItemStack(TolkienItems.CLUB_WOODEN.get()); default -> null; };
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, TrollEntity animatable) {
                return switch (bone.getName()) { default -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND; };
            }

            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, TrollEntity animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                poseStack.mulPose(Axis.XP.rotationDegrees(0));
                poseStack.mulPose(Axis.YP.rotationDegrees(-90));
                poseStack.mulPose(Axis.ZP.rotationDegrees(90));
                poseStack.translate(-1.1D, 0.3D, 0.05D);
                poseStack.scale(1.5F, 1.5F, 1.5F);
                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(TrollEntity entity) {
        return LOCATION_BY_VARIANT.get(entity.getVariant());
    }

    @Override
    public void render(TrollEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.scale(1.6F, 1.6F, 1.6F);

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}