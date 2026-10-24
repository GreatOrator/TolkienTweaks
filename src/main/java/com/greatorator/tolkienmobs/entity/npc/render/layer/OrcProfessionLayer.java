package com.greatorator.tolkienmobs.entity.npc.render.layer;

import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.layer.builtin.TextureLayerGeoLayer;
import com.greatorator.tolkienmobs.entity.npc.OrcEntity;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * Draws the profession's texture over the orc's own model, which 5's texture layer does by
 * re-submitting the model with a second render type. Nothing attaches it to a renderer.
 */
public class OrcProfessionLayer extends TextureLayerGeoLayer<OrcEntity, Void, TolkienGeoRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(MODID, "textures/entity/villager/profession/cleric.png");

    public OrcProfessionLayer(GeoRenderer<OrcEntity, Void, TolkienGeoRenderState> renderer) {
        super(renderer, TEXTURE, RenderTypes::armorCutoutNoCull);
    }
}
