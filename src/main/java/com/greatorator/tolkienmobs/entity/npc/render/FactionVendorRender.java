package com.greatorator.tolkienmobs.entity.npc.render;

import com.geckolib.renderer.GeoEntityRenderer;
import com.greatorator.tolkienmobs.entity.npc.FactionVendorEntity;
import com.greatorator.tolkienmobs.entity.npc.model.FactionVendorModel;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * An envoy's skin is picked by two things: the alliance it serves, which chooses the folder, and the
 * livery it rolled, which chooses the file inside it. Every side paints the same number, so the file
 * number runs one past the livery and is wrapped to the count - which means the model never has to know
 * how many skins any side painted.
 */
public class FactionVendorRender extends GeoEntityRenderer<FactionVendorEntity, TolkienGeoRenderState> {

    public FactionVendorRender(EntityRendererProvider.Context context) {
        super(context, new FactionVendorModel());
        this.shadowRadius = 0.50F;
    }

    /** The skin an envoy wears: its alliance's folder, its livery's numbered file. */
    public static Identifier textureFor(TolkienGeoRenderState renderState) {
        int file = Math.floorMod(renderState.skin, FactionVendorEntity.SKINS) + 1;
        return Identifier.fromNamespaceAndPath(MODID,
                "textures/entity/faction_vendor_" + renderState.alliance.id() + "/vendor" + file + ".png");
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
        renderState.skin = animatable.skin();
    }
}
