package com.greatorator.tolkienmobs.entity.npc.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.entity.npc.ElvesEntity;
import com.greatorator.tolkienmobs.entity.npc.render.ElvesRender;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/** Rendered from the render state, which is the only thing a GeckoLib 5 model or renderer sees during a pass. */
public class ElvesModel extends GeoModel<ElvesEntity> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "merchant/elf");
    private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "merchant/elf");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return ElvesRender.textureFor((TolkienGeoRenderState) renderState);
    }

    @Override
    public Identifier getAnimationResource(ElvesEntity animatable) {
        return ANIMATIONS;
    }
}
