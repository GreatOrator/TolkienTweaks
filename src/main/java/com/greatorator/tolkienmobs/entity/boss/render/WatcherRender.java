package com.greatorator.tolkienmobs.entity.boss.render;

import com.greatorator.tolkienmobs.entity.boss.WatcherEntity;
import com.greatorator.tolkienmobs.entity.boss.model.WatcherModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.renderer.GeoEntityRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class WatcherRender extends GeoEntityRenderer<WatcherEntity> {
    public WatcherRender(EntityRendererProvider.Context context) {
        super(context, new WatcherModel());
        this.shadowRadius = 2.1f;
    }

    @Override
    public ResourceLocation getTextureLocation(WatcherEntity goblinEntity) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/tmwatcher.png");
    }

}
