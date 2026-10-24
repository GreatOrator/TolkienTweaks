package com.greatorator.tolkienmobs.entity.passive.render;

import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import com.greatorator.tolkienmobs.entity.passive.NazgulSteedEntity;
import com.greatorator.tolkienmobs.entity.passive.model.NazgulSteedModel;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.util.HashSet;
import java.util.Set;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class NazgulSteedRender extends GeoEntityRenderer<NazgulSteedEntity, TolkienGeoRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(MODID, "textures/entity/horse/nazgulsteed.png");

    public NazgulSteedRender(EntityRendererProvider.Context context) {
        super(context, new NazgulSteedModel());
        this.shadowRadius = 1.0F;
    }

    @Override
    public TolkienGeoRenderState createRenderState(NazgulSteedEntity animatable, Void relatedObject) {
        return new TolkienGeoRenderState();
    }

    @Override
    public Identifier getTextureLocation(TolkienGeoRenderState renderState) {
        return TEXTURE;
    }

    @Override
    public void addRenderData(NazgulSteedEntity animatable, Void relatedObject, TolkienGeoRenderState renderState, float partialTick) {
        Set<String> hidden = new HashSet<>();
        if (!animatable.isSaddled()) hidden.add("saddle");
        if (!animatable.isSaddled()) hidden.add("headpiece");
        if (!animatable.hasChest()) hidden.add("chest");
        renderState.hiddenBones = Set.copyOf(hidden);
    }

    /** The model's own scaling, which 5 applies here rather than in preRender. */
    @Override
    public void scaleModelForRender(RenderPassInfo<TolkienGeoRenderState> renderPassInfo, float widthScale, float heightScale) {
        float scale = renderPassInfo.renderState().isBaby ? 0.5F : 1F;
        super.scaleModelForRender(renderPassInfo, scale, scale);
    }

    @Override
    public RenderType getRenderType(TolkienGeoRenderState renderState, Identifier texture) {
        return RenderTypes.entityCutout(texture);
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

        snapshots.ifPresent("head", bone -> bone.setRotY(yaw * 0.5f * Mth.DEG_TO_RAD));
    }
}
