package com.greatorator.tolkienmobs.entity.npc.render;

import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import com.google.common.collect.Maps;
import com.greatorator.tolkienmobs.entity.npc.HumanEntity;
import com.greatorator.tolkienmobs.entity.npc.model.HumanModel;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.greatorator.tolkienmobs.TolkienMobsMain.LOGGER;
import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The human's variant and its winter clothing both ride on the render state: the model only ever sees the
 * state, so the two choices it used to read off the entity are captured during extraction and looked up
 * again when the texture is asked for.
 * <p>
 * The folk who carry work are the third case: one skin per alliance, one file each, no variant and no
 * winter clothing. Which of the three is worn is keyed on the same scoreboard tag that says whose work a
 * giver holds, so the art is a drop-in - overwrite {@code faction_west/quest_giver_west.png} and every
 * giver of the West's work changes clothes.
 */
public class HumanRender extends GeoEntityRenderer<HumanEntity, TolkienGeoRenderState> {
    private static final Identifier WINTER_HUMAN = Identifier.fromNamespaceAndPath(MODID, "textures/entity/human_winter/winter");
    private static final Identifier HUMAN = Identifier.fromNamespaceAndPath(MODID, "textures/entity/human/human");

    /**
     * The giver skins, indexed by an alliance's ordinal plus one; slot 0 is an ordinary human and is never
     * asked for. The order matches {@link com.greatorator.tolkienmobs.faction.Alliance}, which is what the
     * entity's synched skin index encodes.
     */
    private static final Identifier[] GIVER_BY_ALLIANCE = {
            null,
            Identifier.fromNamespaceAndPath(MODID, "textures/entity/faction_west/quest_giver_west"),
            Identifier.fromNamespaceAndPath(MODID, "textures/entity/faction_east/quest_giver_east"),
            Identifier.fromNamespaceAndPath(MODID, "textures/entity/faction_harad/quest_giver_harad")
    };

    /** What the pack held the last time each giver skin was asked for, dropped when resources reload. */
    private static final Map<Identifier, Boolean> GIVER_SKIN_PRESENT = new ConcurrentHashMap<>();
    private static ResourceManager seenResourceManager;

    public static final Map<TolkienVariant, Identifier> LOCATION_BY_VARIANT = textures(HUMAN);
    public static final Map<TolkienVariant, Identifier> WINTER_LOCATION_BY_VARIANT = textures(WINTER_HUMAN);

    /**
     * Builds the variant-to-texture list for one folder. Every variant uses the file matching its id
     * plus one (human1.png for DEFAULT, human2.png for RED and so on); LAVENDER has no file of its
     * own and shares DEFAULT's. Giving both folders the same numbering is what lets a human keep its
     * face when it swaps to winter clothing.
     */
    private static Map<TolkienVariant, Identifier> textures(Identifier base) {
        return Util.make(Maps.newEnumMap(TolkienVariant.class), map -> {
            for (TolkienVariant variant : TolkienVariant.values()) {
                int file = variant == TolkienVariant.LAVENDER ? 1 : variant.getId() + 1;
                map.put(variant, base.withSuffix(file + ".png"));
            }
        });
    }

    /** The variant's texture, for the model and for the base class's texture lookup. */
    public static Identifier textureFor(TolkienGeoRenderState renderState) {
        // Asked before the variant and the weather: a giver's clothes are not a face and not a season.
        if (renderState.giverSkin > 0 && renderState.giverSkin < GIVER_BY_ALLIANCE.length) {
            Identifier giver = GIVER_BY_ALLIANCE[renderState.giverSkin];
            if (giver != null && giverSkinIsLoaded(giver)) return giver;
        }
        Map<TolkienVariant, Identifier> byVariant = renderState.winter ? WINTER_LOCATION_BY_VARIANT : LOCATION_BY_VARIANT;
        return byVariant.get(renderState.variant);
    }

    /**
     * Whether the pack actually holds a giver skin, asked once per file per reload. A client that cannot
     * find one draws the missing-texture checker, so a giver whose art is absent keeps the clothes of its
     * own folk instead, and one warning names the file that was looked for.
     */
    private static boolean giverSkinIsLoaded(Identifier texture) {
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        if (manager != seenResourceManager) {
            seenResourceManager = manager;
            GIVER_SKIN_PRESENT.clear();
        }
        return GIVER_SKIN_PRESENT.computeIfAbsent(texture, id -> {
            Identifier file = id.withSuffix(".png");
            boolean present = manager != null && manager.getResource(file).isPresent();
            if (!present) {
                LOGGER.warn("No giver skin at {}; the giver keeps the clothes of its own folk.", file);
            }
            return present;
        });
    }

    public HumanRender(EntityRendererProvider.Context context) {
        super(context, new HumanModel());
        this.shadowRadius = 0.3f;
    }

    @Override
    public TolkienGeoRenderState createRenderState(HumanEntity animatable, Void relatedObject) {
        return new TolkienGeoRenderState();
    }

    @Override
    public Identifier getTextureLocation(TolkienGeoRenderState renderState) {
        return textureFor(renderState);
    }

    @Override
    public void addRenderData(HumanEntity animatable, Void relatedObject, TolkienGeoRenderState renderState, float partialTick) {
        renderState.variant = animatable.getTolkienVariant();
        renderState.winter = animatable.isWinter();
        renderState.giverSkin = animatable.giverSkin();
    }

    /** A human child is drawn at half size; 5 applies that here rather than in preRender. */
    @Override
    public void scaleModelForRender(RenderPassInfo<TolkienGeoRenderState> renderPassInfo, float widthScale, float heightScale) {
        float scale = renderPassInfo.renderState().isBaby ? 0.50F : 1F;
        super.scaleModelForRender(renderPassInfo, scale, scale);
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
