package com.greatorator.tolkienmobs.entity.npc.render;

import com.geckolib.renderer.GeoEntityRenderer;
import com.greatorator.tolkienmobs.entity.npc.FactionVendorEntity;
import com.greatorator.tolkienmobs.entity.npc.model.FactionVendorModel;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * An envoy's skin is the alliance it serves: one texture per side, drawn under faction_trader.
 */
public class FactionVendorRender extends GeoEntityRenderer<FactionVendorEntity, TolkienGeoRenderState> {

    public FactionVendorRender(EntityRendererProvider.Context context) {
        super(context, new FactionVendorModel());
        this.shadowRadius = 0.50F;
    }

    /** The skin an envoy wears: the one texture its side was drawn with. */
    public static Identifier textureFor(TolkienGeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(MODID,
                "textures/entity/faction_trader/trader_" + renderState.alliance.id() + ".png");
    }

    @Override
    public TolkienGeoRenderState createRenderState(FactionVendorEntity animatable, Void relatedObject) {
        return new TolkienGeoRenderState();
    }

    @Override
    public Identifier getTextureLocation(TolkienGeoRenderState renderState) {
        return textureFor(renderState);
    }

    @Override
    public void addRenderData(FactionVendorEntity animatable, Void relatedObject, TolkienGeoRenderState renderState,
                              float partialTick) {
        renderState.alliance = animatable.alliance();
    }
}
