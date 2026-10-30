package com.greatorator.tolkienmobs.entity.npc.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.entity.npc.FactionVendorEntity;
import com.greatorator.tolkienmobs.entity.npc.render.FactionVendorRender;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * An envoy is built on the same humanoid rig the alliance guard wears, so the two differ only in which
 * skins are read - the alliance chooses the folder and the livery chooses the file, both off the render
 * state the renderer fills during extraction.
 */
public class FactionVendorModel extends GeoModel<FactionVendorEntity> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "monster/base");
    private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "monster/base");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return FactionVendorRender.textureFor((TolkienGeoRenderState) renderState);
    }

    @Override
    public Identifier getAnimationResource(FactionVendorEntity animatable) {
        return ANIMATIONS;
    }
}
