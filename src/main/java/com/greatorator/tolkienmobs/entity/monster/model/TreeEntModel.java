package com.greatorator.tolkienmobs.entity.monster.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.greatorator.tolkienmobs.entity.monster.TreeEntEntity;
import com.greatorator.tolkienmobs.entity.monster.render.TreeEntRender;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The variant texture comes off the render state, which the renderer fills during extraction; the held boulder
 * and the head turn both moved to the renderer's {@code adjustModelBonesForRender}, where GeckoLib 5 wants
 * bone work done.
 */
public class TreeEntModel extends GeoModel<TreeEntEntity> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(MODID, "monster/treeent");
    private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(MODID, "monster/treeent");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TreeEntRender.textureFor((TolkienGeoRenderState) renderState);
    }

    @Override
    public Identifier getAnimationResource(TreeEntEntity animatable) {
        return ANIMATIONS;
    }
}
