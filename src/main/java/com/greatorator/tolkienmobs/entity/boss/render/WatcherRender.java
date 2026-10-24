package com.greatorator.tolkienmobs.entity.boss.render;

import com.geckolib.renderer.GeoEntityRenderer;
import com.greatorator.tolkienmobs.entity.boss.WatcherEntity;
import com.greatorator.tolkienmobs.entity.boss.model.WatcherModel;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/** The Watcher's model never touched the entity, so the renderer only supplies its texture. */
public class WatcherRender extends GeoEntityRenderer<WatcherEntity, TolkienGeoRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(MODID, "textures/entity/tmwatcher.png");

    public WatcherRender(EntityRendererProvider.Context context) {
        super(context, new WatcherModel());
        this.shadowRadius = 2.1f;
    }

    @Override
    public TolkienGeoRenderState createRenderState(WatcherEntity animatable, Void relatedObject) {
        return new TolkienGeoRenderState();
    }

    @Override
    public Identifier getTextureLocation(TolkienGeoRenderState renderState) {
        return TEXTURE;
    }
}
