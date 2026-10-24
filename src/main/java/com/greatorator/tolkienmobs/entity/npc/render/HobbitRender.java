package com.greatorator.tolkienmobs.entity.npc.render;

import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import com.google.common.collect.Maps;
import com.greatorator.tolkienmobs.entity.npc.HobbitEntity;
import com.greatorator.tolkienmobs.entity.npc.model.HobbitModel;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

import java.util.Map;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class HobbitRender extends GeoEntityRenderer<HobbitEntity, TolkienGeoRenderState> {
    public static final Map<TolkienVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(TolkienVariant.class), map -> {
                map.put(TolkienVariant.DEFAULT,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/hobbit/hobbit1.png"));
                map.put(TolkienVariant.RED,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/hobbit/hobbit2.png"));
                map.put(TolkienVariant.ORANGE,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/hobbit/hobbit3.png"));
                map.put(TolkienVariant.YELLOW,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/hobbit/hobbit4.png"));
                map.put(TolkienVariant.GREEN,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/hobbit/hobbit5.png"));
                map.put(TolkienVariant.BLUE,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/hobbit/hobbit6.png"));
                map.put(TolkienVariant.INDIGO,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/hobbit/hobbit7.png"));
                map.put(TolkienVariant.VIOLET,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/hobbit/hobbit8.png"));
                map.put(TolkienVariant.MAGENTA,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/hobbit/hobbit9.png"));
                map.put(TolkienVariant.PINK,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/hobbit/hobbit10.png"));
                map.put(TolkienVariant.GRAY,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/hobbit/hobbit11.png"));
                map.put(TolkienVariant.AQUA,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/hobbit/hobbit12.png"));
                map.put(TolkienVariant.BEIGE,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/hobbit/hobbit13.png"));
                map.put(TolkienVariant.BROWN,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/hobbit/hobbit14.png"));
                map.put(TolkienVariant.CORAL,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/hobbit/hobbit15.png"));
                map.put(TolkienVariant.CYAN,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/hobbit/hobbit16.png"));
                map.put(TolkienVariant.LAVENDER,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/hobbit/hobbit1.png"));
            });

    public HobbitRender(EntityRendererProvider.Context context) {
        super(context, new HobbitModel());
        this.shadowRadius = 0.3f;
    }

    @Override
    public TolkienGeoRenderState createRenderState(HobbitEntity animatable, Void relatedObject) {
        return new TolkienGeoRenderState();
    }

    /** The variant's texture, for the model and for the base class's texture lookup. */
    public static Identifier textureFor(TolkienGeoRenderState renderState) {
        return LOCATION_BY_VARIANT.get(renderState.variant);
    }

    @Override
    public Identifier getTextureLocation(TolkienGeoRenderState renderState) {
        return textureFor(renderState);
    }

    @Override
    public void addRenderData(HobbitEntity animatable, Void relatedObject, TolkienGeoRenderState renderState, float partialTick) {
        renderState.variant = animatable.getTolkienVariant();
    }

    /** The model's own scaling, which 5 applies here rather than in preRender. */
    @Override
    public void scaleModelForRender(RenderPassInfo<TolkienGeoRenderState> renderPassInfo, float widthScale, float heightScale) {
        float scale = renderPassInfo.renderState().isBaby ? 0.50F : 0.65F;
        super.scaleModelForRender(renderPassInfo, scale, scale);
    }

    /** The bone work the model used to do in setCustomAnimations, done per render pass. */
    @Override
    public void adjustModelBonesForRender(RenderPassInfo<TolkienGeoRenderState> renderPassInfo, BoneSnapshots snapshots) {
        TolkienGeoRenderState renderState = renderPassInfo.renderState();

        float pitch = renderState.getOrDefaultGeckolibData(DataTickets.ENTITY_PITCH, 0f);
        float yaw = renderState.getOrDefaultGeckolibData(DataTickets.ENTITY_YAW, 0f);

        snapshots.ifPresent("head", bone -> bone.setRotation(pitch * Mth.DEG_TO_RAD, yaw * 0.5f * Mth.DEG_TO_RAD, 0f));
    }
}
