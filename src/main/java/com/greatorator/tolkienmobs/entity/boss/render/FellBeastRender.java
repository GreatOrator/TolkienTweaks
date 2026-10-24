package com.greatorator.tolkienmobs.entity.boss.render;

import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import com.greatorator.tolkienmobs.entity.boss.FellBeastEntity;
import com.greatorator.tolkienmobs.entity.boss.model.FellBeastModel;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The beast wears one texture, so the renderer only has to do what the model used to: draw it half again as
 * large, and turn its head towards whatever it is looking at.
 */
public class FellBeastRender extends GeoEntityRenderer<FellBeastEntity, TolkienGeoRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(MODID, "textures/entity/fellbeast/fellbeast.png");

    public FellBeastRender(EntityRendererProvider.Context context) {
        super(context, new FellBeastModel());
        this.shadowRadius = 2.1f;
    }

    @Override
    public TolkienGeoRenderState createRenderState(FellBeastEntity animatable, Void relatedObject) {
        return new TolkienGeoRenderState();
    }

    @Override
    public Identifier getTextureLocation(TolkienGeoRenderState renderState) {
        return TEXTURE;
    }

    /** The beast's model is drawn half again as large; 5 applies that here rather than in preRender. */
    @Override
    public void scaleModelForRender(RenderPassInfo<TolkienGeoRenderState> renderPassInfo, float widthScale, float heightScale) {
        super.scaleModelForRender(renderPassInfo, 1.5F, 1.5F);
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
