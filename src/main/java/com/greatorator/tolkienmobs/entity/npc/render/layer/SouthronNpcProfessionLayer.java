package com.greatorator.tolkienmobs.entity.npc.render.layer;

import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.layer.builtin.TextureLayerGeoLayer;
import com.greatorator.tolkienmobs.entity.npc.SouthronEntity;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * Draws the profession's texture over the southron's own model, which 5's texture layer does by
 * re-submitting the model with a second render type. Nothing attaches it to a renderer.
 */
public class SouthronNpcProfessionLayer extends TextureLayerGeoLayer<SouthronEntity, Void, TolkienGeoRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(MODID, "textures/entity/villager/profession/cleric.png");

    public SouthronNpcProfessionLayer(GeoRenderer<SouthronEntity, Void, TolkienGeoRenderState> renderer) {
        super(renderer, TEXTURE, RenderTypes::armorCutoutNoCull);
    }
}
