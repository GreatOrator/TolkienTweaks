package com.greatorator.tolkienmobs.entity.boss.render;

import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import com.greatorator.tolkienmobs.entity.boss.MorgulironGolemEntity;
import com.greatorator.tolkienmobs.entity.boss.model.MorgulironGolemModel;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/** The morgul-iron golem is the elemental golem's model in its own texture, with the head turn in the renderer. */
public class MorgulironGolemRender extends GeoEntityRenderer<MorgulironGolemEntity, TolkienGeoRenderState> {
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(MODID, "textures/entity/elementalgolem/elemental_golem_morgul.png");

    public MorgulironGolemRender(EntityRendererProvider.Context context) {
        super(context, new MorgulironGolemModel());
        this.shadowRadius = 1.0f;
    }

    @Override
    public TolkienGeoRenderState createRenderState(MorgulironGolemEntity animatable, Void relatedObject) {
        return new TolkienGeoRenderState();
    }

    @Override
    public Identifier getTextureLocation(TolkienGeoRenderState renderState) {
        return TEXTURE;
    }

    /** The head turn, which the model used to make in setCustomAnimations. */
    @Override
    public void adjustModelBonesForRender(RenderPassInfo<TolkienGeoRenderState> renderPassInfo, BoneSnapshots snapshots) {
        TolkienGeoRenderState renderState = renderPassInfo.renderState();
        float pitch = renderState.getOrDefaultGeckolibData(DataTickets.ENTITY_PITCH, 0f);
        float yaw = renderState.getOrDefaultGeckolibData(DataTickets.ENTITY_YAW, 0f);

        snapshots.ifPresent("head", bone -> bone.setRotation(pitch * Mth.DEG_TO_RAD, yaw * 0.5f * Mth.DEG_TO_RAD, 0f));
    }
}
