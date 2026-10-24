package com.greatorator.tolkienmobs.entity.ambient.render;

import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import com.google.common.collect.Maps;
import com.greatorator.tolkienmobs.entity.ambient.FrogEntity;
import com.greatorator.tolkienmobs.entity.ambient.model.FrogModel;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class FrogRender extends GeoEntityRenderer<FrogEntity, TolkienGeoRenderState> {
    public static final Map<TolkienVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(TolkienVariant.class), map -> {
                map.put(TolkienVariant.DEFAULT,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/toaddle/toaddle_black.png"));
                map.put(TolkienVariant.RED,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/toaddle/toaddle_black.png"));
                map.put(TolkienVariant.ORANGE,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/toaddle/toaddle_green.png"));
                map.put(TolkienVariant.YELLOW,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/toaddle/toaddle_rainbow.png"));
                map.put(TolkienVariant.GREEN,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/toaddle/toaddle_red.png"));
                map.put(TolkienVariant.BLUE,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/toaddle/toaddle_white.png"));
                map.put(TolkienVariant.INDIGO,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/toaddle/toaddle_yellow.png"));
                map.put(TolkienVariant.VIOLET,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/toaddle/toaddle_blue.png"));
                map.put(TolkienVariant.MAGENTA,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/toaddle/toaddle_black.png"));
                map.put(TolkienVariant.PINK,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/toaddle/toaddle_green.png"));
                map.put(TolkienVariant.GRAY,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/toaddle/toaddle_rainbow.png"));
                map.put(TolkienVariant.AQUA,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/toaddle/toaddle_red.png"));
                map.put(TolkienVariant.BEIGE,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/toaddle/toaddle_white.png"));
                map.put(TolkienVariant.BROWN,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/toaddle/toaddle_yellow.png"));
                map.put(TolkienVariant.CORAL,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/toaddle/toaddle_blue.png"));
                map.put(TolkienVariant.CYAN,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/toaddle/toaddle_green.png"));
                map.put(TolkienVariant.LAVENDER,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/toaddle/murderfrog.png"));
            });

    public FrogRender(EntityRendererProvider.Context context) {
        super(context, new FrogModel());
        this.shadowRadius = 0.25f;
    }

    @Override
    public TolkienGeoRenderState createRenderState(FrogEntity animatable, Void relatedObject) {
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
    public void addRenderData(FrogEntity animatable, Void relatedObject, TolkienGeoRenderState renderState, float partialTick) {
        renderState.variant = animatable.getVariant();
        Set<String> hidden = new HashSet<>();
        if (!animatable.isCatching()) hidden.add("insect");
        if (!animatable.isCatching()) hidden.add("tongue");
        renderState.hiddenBones = Set.copyOf(hidden);
    }

    /** The model's own scaling, which 5 applies here rather than in preRender. */
    @Override
    public void scaleModelForRender(RenderPassInfo<TolkienGeoRenderState> renderPassInfo, float widthScale, float heightScale) {
        TolkienGeoRenderState renderState = renderPassInfo.renderState();

        if (renderState.isBaby) {
            super.scaleModelForRender(renderPassInfo, 0.15F, 0.15F);
        } else if (renderState.variant.getId() == 16) {
            super.scaleModelForRender(renderPassInfo, 1.3F, 1.3F);
        } else {
            super.scaleModelForRender(renderPassInfo, 0.3F, 0.3F);
        }
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

        snapshots.ifPresent("head", bone -> bone.setRotation(pitch * Mth.DEG_TO_RAD, yaw * 0.5f * Mth.DEG_TO_RAD, 0f));
    }
}
