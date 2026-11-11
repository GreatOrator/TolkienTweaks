package com.greatorator.tolkienmobs.entity.npc.render;

import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import com.google.common.collect.Maps;
import com.greatorator.tolkienmobs.entity.npc.DwarfEntity;
import com.greatorator.tolkienmobs.entity.npc.model.DwarfModel;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

import java.util.Map;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class DwarfRender extends GeoEntityRenderer<DwarfEntity, TolkienGeoRenderState> {
    public static final Map<TolkienVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(TolkienVariant.class), map -> {
                map.put(TolkienVariant.DEFAULT,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/dwarf/dwarf1.png"));
                map.put(TolkienVariant.RED,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/dwarf/dwarf2.png"));
                map.put(TolkienVariant.ORANGE,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/dwarf/dwarf3.png"));
                map.put(TolkienVariant.YELLOW,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/dwarf/dwarf4.png"));
                map.put(TolkienVariant.GREEN,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/dwarf/dwarf5.png"));
                map.put(TolkienVariant.BLUE,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/dwarf/dwarf6.png"));
                map.put(TolkienVariant.INDIGO,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/dwarf/dwarf7.png"));
                map.put(TolkienVariant.VIOLET,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/dwarf/dwarf8.png"));
                map.put(TolkienVariant.MAGENTA,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/dwarf/dwarf9.png"));
                map.put(TolkienVariant.PINK,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/dwarf/dwarf10.png"));
                map.put(TolkienVariant.GRAY,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/dwarf/dwarf1.png"));
                map.put(TolkienVariant.AQUA,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/dwarf/dwarf2.png"));
                map.put(TolkienVariant.BEIGE,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/dwarf/dwarf3.png"));
                map.put(TolkienVariant.BROWN,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/dwarf/dwarf4.png"));
                map.put(TolkienVariant.CORAL,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/dwarf/dwarf5.png"));
                map.put(TolkienVariant.CYAN,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/dwarf/dwarf6.png"));
                map.put(TolkienVariant.LAVENDER,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/dwarf/dwarf7.png"));
            });

    /** Where a named dwarf's own skin and model are looked for, if anybody has drawn them. */
    private static final Identifier NAMED_SKIN =
            Identifier.fromNamespaceAndPath(MODID, "textures/entity/middle-earth/");
    private static final Identifier NAMED_MODEL =
            Identifier.fromNamespaceAndPath(MODID, "geckolib/models/merchant/");

    /**
     * The model a named dwarf is drawn with, if the pack has drawn him one of his own, or null for the
     * folk's own model. A dwarf's cap is his own file rather than a code path, so the name he is wearing
     * is what the file is looked for by, and a pack without it draws an ordinary dwarf and says nothing
     * about it - a named file is for the artist, and art arrives by degrees.
     */
    @Nullable
    public static Identifier modelFor(TolkienGeoRenderState renderState) {
        if (renderState.giverName.isEmpty()) return null;
        String slug = HumanRender.slugOfName(renderState.giverName);
        if (slug.isEmpty()) return null;
        Identifier own = NAMED_MODEL.withSuffix(slug + ".json");
        return HumanRender.resourcePresent(own) ? own : null;
    }

    /** The file a named dwarf's skin would be in, or null for a body wearing no name. */
    @Nullable
    private static Identifier namedSkin(String name) {
        if (name.isEmpty()) return null;
        String slug = HumanRender.slugOfName(name);
        if (slug.isEmpty()) return null;
        return NAMED_SKIN.withSuffix(slug + ".png");
    }

    public DwarfRender(EntityRendererProvider.Context context) {
        super(context, new DwarfModel());
        this.shadowRadius = 0.3f;
    }

    @Override
    public TolkienGeoRenderState createRenderState(DwarfEntity animatable, Void relatedObject) {
        return new TolkienGeoRenderState();
    }

    /** The variant's texture, for the model and for the base class's texture lookup. */
    public static Identifier textureFor(TolkienGeoRenderState renderState) {
        // Asked before the variant: a dwarf with art of his own is one person, not one of a folk's coats.
        // The file is optional, so a dwarf nobody has drawn keeps the clothes of his folk.
        Identifier own = namedSkin(renderState.giverName);
        if (own != null && HumanRender.resourcePresent(own)) return own;
        return LOCATION_BY_VARIANT.get(renderState.variant);
    }

    @Override
    public Identifier getTextureLocation(TolkienGeoRenderState renderState) {
        return textureFor(renderState);
    }

    @Override
    public void addRenderData(DwarfEntity animatable, Void relatedObject, TolkienGeoRenderState renderState, float partialTick) {
        renderState.variant = animatable.getTolkienVariant();
        // A dwarf is named when he is somebody in particular, which is the only case a file of his own
        // is worth looking for; a village dwarf keeps his folk's clothes.
        Component name = animatable.getCustomName();
        renderState.giverName = name == null ? "" : name.getString();
        HumanRender.sceneData(animatable, renderState);
    }

    /** The model's own scaling, which 5 applies here rather than in preRender. */
    @Override
    public void scaleModelForRender(RenderPassInfo<TolkienGeoRenderState> renderPassInfo, float widthScale, float heightScale) {
        float scale = renderPassInfo.renderState().isBaby ? 0.50F : 0.85F;
        super.scaleModelForRender(renderPassInfo, scale, scale);
    }

    /** The bone work the model used to do in setCustomAnimations, done per render pass. */
    @Override
    public void adjustModelBonesForRender(RenderPassInfo<TolkienGeoRenderState> renderPassInfo, BoneSnapshots snapshots) {
        TolkienGeoRenderState renderState = renderPassInfo.renderState();

        float pitch = renderState.getOrDefaultGeckolibData(DataTickets.ENTITY_PITCH, 0f);
        float yaw = renderState.getOrDefaultGeckolibData(DataTickets.ENTITY_YAW, 0f);

        if (HumanRender.sceneBones(renderState, snapshots)) return;
        snapshots.ifPresent("head", bone -> bone.setRotation(pitch * Mth.DEG_TO_RAD, yaw * 0.5f * Mth.DEG_TO_RAD, 0f));
    }
}
