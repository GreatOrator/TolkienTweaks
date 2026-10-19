package com.greatorator.tolkienmobs.entity.monster.render;

import com.google.common.collect.Maps;
import com.greatorator.tolkienmobs.entity.monster.EnragedFellSpiritEntity;
import com.greatorator.tolkienmobs.entity.monster.model.EnragedFellSpiritModel;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.Util;
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
import java.util.Map;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class EnragedFellSpiritRender extends GeoEntityRenderer<EnragedFellSpiritEntity> {
    public static final Map<TolkienVariant, ResourceLocation> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(TolkienVariant.class), map -> {
                map.put(TolkienVariant.DEFAULT,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/enragedfellspirit/fellspirit1.png"));
                map.put(TolkienVariant.RED,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/enragedfellspirit/fellspirit1.png"));
                map.put(TolkienVariant.ORANGE,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/enragedfellspirit/fellspirit2.png"));
                map.put(TolkienVariant.YELLOW,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/enragedfellspirit/fellspirit3.png"));
                map.put(TolkienVariant.GREEN,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/enragedfellspirit/fellspirit4.png"));
                map.put(TolkienVariant.BLUE,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/enragedfellspirit/fellspirit1.png"));
                map.put(TolkienVariant.INDIGO,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/enragedfellspirit/fellspirit2.png"));
                map.put(TolkienVariant.VIOLET,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/enragedfellspirit/fellspirit3.png"));
                map.put(TolkienVariant.MAGENTA,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/enragedfellspirit/fellspirit4.png"));
                map.put(TolkienVariant.PINK,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/enragedfellspirit/fellspirit1.png"));
                map.put(TolkienVariant.GRAY,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/enragedfellspirit/fellspirit2.png"));
                map.put(TolkienVariant.AQUA,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/enragedfellspirit/fellspirit3.png"));
                map.put(TolkienVariant.BEIGE,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/enragedfellspirit/fellspirit4.png"));
                map.put(TolkienVariant.BROWN,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/enragedfellspirit/fellspirit1.png"));
                map.put(TolkienVariant.CORAL,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/enragedfellspirit/fellspirit2.png"));
                map.put(TolkienVariant.CYAN,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/enragedfellspirit/fellspirit3.png"));
                map.put(TolkienVariant.LAVENDER,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/enragedfellspirit/fellspirit4.png"));
            });

    public EnragedFellSpiritRender(EntityRendererProvider.Context context) {
        super(context, new EnragedFellSpiritModel());
        this.shadowRadius = 0.50f;
        addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @Nullable
            @Override
            protected ItemStack getStackForBone(GeoBone bone, EnragedFellSpiritEntity animatable) {
                return switch (bone.getName()) { case "rightArm" -> new ItemStack(TolkienItems.SWORD_MORGULIRON.get()); default -> null; };
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, EnragedFellSpiritEntity animatable) {
                return switch (bone.getName()) { default -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND; };
            }

            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, EnragedFellSpiritEntity animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                poseStack.mulPose(Axis.XP.rotationDegrees(-30));
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
                poseStack.mulPose(Axis.ZP.rotationDegrees(0));
                poseStack.translate(0.45D, -0.4D, 0.0D);
                poseStack.scale(1.0F, 1.0F, 1.0F);
                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(EnragedFellSpiritEntity fellspiritEntity) {
        return LOCATION_BY_VARIANT.get(fellspiritEntity.getVariant());
    }

    @Override
    public RenderType getRenderType(EnragedFellSpiritEntity livingEntity, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        ResourceLocation resourcelocation = this.getTextureLocation(livingEntity);

        return RenderType.itemEntityTranslucentCull(resourcelocation);
    }

    @Override
    public void render(EnragedFellSpiritEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
