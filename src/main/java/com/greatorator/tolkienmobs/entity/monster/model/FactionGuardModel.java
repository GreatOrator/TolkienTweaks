package com.greatorator.tolkienmobs.entity.monster.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.entity.monster.FactionGuardEntity;
import com.greatorator.tolkienmobs.entity.monster.render.FactionGuardRender;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * A guard is built on the same humanoid rig the brigand uses, so it only differs in which skin it reads -
 * the alliance and the variant both come off the render state the renderer fills during extraction.
 */
public class FactionGuardModel extends GeoModel<FactionGuardEntity> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "monster/base");
    private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "monster/base");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return FactionGuardRender.textureFor((TolkienGeoRenderState) renderState);
    }

    @Override
    public Identifier getAnimationResource(FactionGuardEntity animatable) {
        return ANIMATIONS;
    }
}
