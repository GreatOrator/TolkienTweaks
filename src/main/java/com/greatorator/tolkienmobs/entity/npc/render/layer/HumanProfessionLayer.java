package com.greatorator.tolkienmobs.entity.npc.render.layer;

import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.layer.builtin.TextureLayerGeoLayer;
import com.greatorator.tolkienmobs.entity.npc.HumanEntity;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * Draws the profession's texture over the human's own model.
 * <p>
 * The whole-model second pass that this used to do by hand with {@code reRender} is what 5's texture
 * layer is for: it re-submits the same model after the main pass, so only the render type changes.
 * The armour cutout type is the one the old layer used, and it keeps the texture's transparency.
 * <p>
 * Nothing attaches this layer to a renderer, here or in the 1.21 source this file comes from.
 */
public class HumanProfessionLayer extends TextureLayerGeoLayer<HumanEntity, Void, TolkienGeoRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(MODID, "textures/entity/villager/profession/cleric.png");

    public HumanProfessionLayer(GeoRenderer<HumanEntity, Void, TolkienGeoRenderState> renderer) {
        super(renderer, TEXTURE, RenderTypes::armorCutoutNoCull);
    }
}
