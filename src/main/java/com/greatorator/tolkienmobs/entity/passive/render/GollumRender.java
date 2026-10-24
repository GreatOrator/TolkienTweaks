package com.greatorator.tolkienmobs.entity.passive.render;

import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import com.greatorator.tolkienmobs.entity.passive.GollumEntity;
import com.greatorator.tolkienmobs.entity.passive.model.GollumModel;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.util.HashSet;
import java.util.Set;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class GollumRender extends GeoEntityRenderer<GollumEntity, TolkienGeoRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(MODID, "textures/entity/gollum.png");

    public GollumRender(EntityRendererProvider.Context context) {
        super(context, new GollumModel());
        this.shadowRadius = 0.25f;
    }

    @Override
    public TolkienGeoRenderState createRenderState(GollumEntity animatable, Void relatedObject) {
        return new TolkienGeoRenderState();
    }

    @Override
    public Identifier getTextureLocation(TolkienGeoRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public void addRenderData(GollumEntity animatable, Void relatedObject, TolkienGeoRenderState renderState, float partialTick) {
        Set<String> hidden = new HashSet<>();
        if (!animatable.isEating()) hidden.add("fish");
        renderState.hiddenBones = Set.copyOf(hidden);
    }

    /** The bone work the model used to do in setCustomAnimations, done per render pass. */
    @Override
    public void adjustModelBonesForRender(RenderPassInfo<TolkienGeoRenderState> renderPassInfo, BoneSnapshots snapshots) {
        TolkienGeoRenderState renderState = renderPassInfo.renderState();

        for (String bone : renderState.hiddenBones) {
            snapshots.ifPresent(bone, snapshot -> snapshot.skipRender(true).skipChildrenRender(true));
        }

        float pitch = renderState.getOrDefaultGeckolibData(DataTickets.ENTITY_PITCH, 0f);
        float yaw = renderState.getOrDefaultGeckolibData(DataTickets.ENTITY_YAW, 0f);

        snapshots.ifPresent("GollumHead", bone -> bone.setRotation(pitch * Mth.DEG_TO_RAD, yaw * 0.5f * Mth.DEG_TO_RAD, 0f));
    }
}
