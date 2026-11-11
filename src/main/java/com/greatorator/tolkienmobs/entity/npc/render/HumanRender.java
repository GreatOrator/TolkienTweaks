package com.greatorator.tolkienmobs.entity.npc.render;

import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.builtin.BlockAndItemGeoLayer;
import com.geckolib.util.RenderUtil;
import com.google.common.collect.Maps;
import com.greatorator.tolkienmobs.entity.TolkienVillagerEntity;
import com.greatorator.tolkienmobs.entity.npc.HumanEntity;
import com.greatorator.tolkienmobs.entity.npc.model.HumanModel;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.text.Normalizer;
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
 * <p>
 * A hall's herald is also a person with a name, so before the alliance's cloth is asked for, the name he
 * is wearing is looked for as a file of its own under {@code textures/entity/middle-earth/}. Those files
 * are optional: drawn, Maelgrin wears his own face; not drawn, he wears the West's, and nothing is said
 * about it, because a missing named skin is the ordinary state of a mod whose art arrives by degrees.
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
            Identifier.fromNamespaceAndPath(MODID, "textures/entity/faction_west/quest_giver_west.png"),
            Identifier.fromNamespaceAndPath(MODID, "textures/entity/faction_east/quest_giver_east.png"),
            Identifier.fromNamespaceAndPath(MODID, "textures/entity/faction_harad/quest_giver_harad.png")
    };

    /**
     * The one skin a camp keeper wears, if the pack has drawn it: a ranger of the West sitting at a
     * fire the campaign built. It is a file on its own rather than an alliance's cloth, because a
     * keeper holds no work and serves nobody's hall - and it is optional, so a pack without the art
     * gets an ordinary human at the fire rather than a missing-texture checker.
     */
    private static final Identifier KEEPER = Identifier.fromNamespaceAndPath(MODID,
            "textures/entity/middle-earth/ranger.png");

    /**
     * Where the art for one named giver is looked for, if anybody has drawn it. The mod ships the three
     * alliance skins and names its heralds, so the named files are for the artist rather than for the
     * build: {@code middle-earth/maelgrin.png} is the West's herald's own face.
     */
    private static final Identifier NAMED_GIVER =
            Identifier.fromNamespaceAndPath(MODID, "textures/entity/middle-earth/");
    /** One identifier per name, so a nameplate is not turned into a texture path every frame. */
    private static final Map<String, Identifier> NAMED_BY_SLUG = new ConcurrentHashMap<>();

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
        // A keeper is the first case: not a giver, not a season. One file for the job - unless the pack has
        // drawn the face of the one who keeps that camp. Elladan sits at Thorenhad for most of the chapter,
        // and an Elf-lord's son is not a Ranger at a fire: the named file is asked for first, and the
        // Ranger's face is what a keeper nobody has drawn falls back to.
        if (renderState.campKeeper) {
            Identifier own = namedGiverSkin(renderState.giverName);
            if (own != null && skinIsLoaded(own, false)) return own;
            if (skinIsLoaded(KEEPER, false)) return KEEPER;
        }
        // Asked before the variant and the weather: a giver's clothes are not a face and not a season.
        if (renderState.giverSkin > 0 && renderState.giverSkin < GIVER_BY_ALLIANCE.length) {
            // A herald with art of his own wears it, and his alliance's cloth is what he falls back
            // to. The named file is asked for quietly: not being there is the ordinary case, not a
            // fault worth a line in the log on every load.
            Identifier own = namedGiverSkin(renderState.giverName);
            if (own != null && skinIsLoaded(own, false)) return own;
            Identifier giver = GIVER_BY_ALLIANCE[renderState.giverSkin];
            if (giver != null && skinIsLoaded(giver, true)) return giver;
        }
        Map<TolkienVariant, Identifier> byVariant = renderState.winter ? WINTER_LOCATION_BY_VARIANT : LOCATION_BY_VARIANT;
        return byVariant.get(renderState.variant);
    }

    /**
     * The file a named giver's own art would be in, or null for a body wearing no name or a name a
     * filesystem cannot spell. {@code Maelgrin} is looked for as {@code maelgrin.png}; a name with a space
     * in it keeps the space as an underscore, so two names cannot collide by being run together.
     */
    @Nullable
    private static Identifier namedGiverSkin(String name) {
        if (name.isEmpty()) return null;
        String slug = slugOf(name);
        if (slug.isEmpty()) return null;
        return NAMED_BY_SLUG.computeIfAbsent(slug, found -> NAMED_GIVER.withSuffix(found + ".png"));
    }

    /**
     * A name as a file name: lower case, spaces to underscores, and nothing a path cannot hold.
     * <p>
     * Accents are flattened to the letter under them before anything else happens, which is the
     * difference between a named file being found and never being found at all: the old slug dropped
     * whatever was not a to z, so {@code Glóin} was asked for as {@code glin.png}, and so would
     * {@code Théoden} and {@code Gríma}. The same goes for the pack's own spelling: a name written plain
     * and a name written with its accent look for one file.
     */
    private static String slugOf(String name) {
        String flat = Normalizer.normalize(name, Normalizer.Form.NFD);
        StringBuilder slug = new StringBuilder(flat.length());
        for (int i = 0; i < flat.length(); i++) {
            char c = Character.toLowerCase(flat.charAt(i));
            if (Character.getType(c) == Character.NON_SPACING_MARK) continue;
            if (c >= 'a' && c <= 'z' || c >= '0' && c <= '9') {
                slug.append(c);
            } else if (c == ' ' || c == '-' || c == '_') {
                slug.append('_');
            }
        }
        return slug.toString();
    }

    /**
     * Whether the pack actually holds a skin, asked once per file per reload. A client that cannot find
     * one draws the missing-texture checker, so a giver whose art is absent keeps the clothes of its own
     * folk instead. Only the alliance's file is worth a warning - that one is the mod's own promise - and
     * a named file that nobody has drawn is passed over in silence.
     */
    private static boolean skinIsLoaded(Identifier texture, boolean warn) {
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        if (manager != seenResourceManager) {
            seenResourceManager = manager;
            GIVER_SKIN_PRESENT.clear();
        }
        return GIVER_SKIN_PRESENT.computeIfAbsent(texture, id -> {
            boolean present = manager != null && manager.getResource(id).isPresent();
            if (!present && warn) {
                LOGGER.warn("No giver skin at {}; the giver keeps the clothes of its own folk.", id);
            }
            return present;
        });
    }

    /**
     * The file a named body's own art would be in, for a renderer that draws another of the pack's
     * folk. A dwarf keeps his face in a file of his own, looked for by the same name a named giver is
     * looked for by, so the lookup lives here rather than being written twice.
     */
    @Nullable
    static Identifier namedSkin(String name) {
        return namedGiverSkin(name);
    }

    /** A name as a file name, for a renderer looking for a model or a skin of its own. */
    static String slugOfName(String name) {
        return slugOf(name);
    }

    /** Whether the pack holds a resource, asked once per file per reload. */
    static boolean resourcePresent(Identifier id) {
        return skinIsLoaded(id, false);
    }

    /** How far a body of a scene will look for the player before it stops turning. */
    private static final double SCENE_LOOK = 24.0D;
    /** Which way a positive head-bone rotation turns a face. Flip to -1 if a head looks away instead. */
    private static final float HEAD_SIGN = 1.0F;
    /** Radians of stride per tick: a step every six ticks, which is the pace the walk is stepped at. */
    private static final double STRIDE_RATE = 0.52D;
    /** How far a walking leg swings either way, in degrees. */
    private static final float STRIDE_SWING = 26.0F;
    /** How much of the legs' swing the arms take up, which is less because a swinging arm reads as a march. */
    private static final float ARM_SWING = 0.55F;
    /** How far a speaking arm comes up when the body is greeting somebody with it, in degrees. */
    private static final float GREET_RAISE = 80.0F;
    /** How far an arm with a scroll in its hand comes up: less, because paper is read close to the face. */
    private static final float READ_RAISE = 44.0F;
    /** How far a reading arm turns across the body, so the scroll ends up in front of it rather than beside. */
    private static final float READ_ACROSS = -20.0F;
    /** How far a reading head drops towards the paper, in degrees. */
    private static final float READ_PITCH = -12.0F;

    /**
     * The scene's own facts, read off a body as it is extracted: whether it belongs to a scene, which way the
     * player is from its face, and how far its arm is up.
     * <p>
     * The turning and the lifting are eased here rather than on the server, so the only thing the scene ever
     * has to say is "this body is the one talking" - one synched flag, sixty times a second of smooth motion
     * on the far side.
     */
    public static void sceneData(TolkienVillagerEntity body, TolkienGeoRenderState renderState, float partialTick) {
        renderState.sceneBody = body.isSceneBody();
        if (!renderState.sceneBody) return;
        renderState.sceneSpeaking = body.isSpeaking();
        // A body on its way out of the scene is moved rather than navigating, so nothing about its motion
        // reaches the client. The stride is made in the renderer off the body's own age instead, which also
        // means nine of them are never in step with each other.
        renderState.sceneWalking = body.isSceneWalking();
        renderState.sceneStride = body.tickCount + partialTick;
        // Reading is not speaking with a hand up: a body holding paper brings it to its face. The item is
        // synched with the body, so the client can see which of the two it is doing.
        renderState.sceneReading = body.getMainHandItem().is(TolkienItems.ITEM_SCROLL.get());
        float want = 0.0F;
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            double dx = player.getX() - body.getX();
            double dz = player.getZ() - body.getZ();
            if (dx * dx + dz * dz < SCENE_LOOK * SCENE_LOOK) {
                // Relative to the body's own facing, because that is the frame the head bone lives in.
                want = Mth.wrapDegrees(yawTowards(dx, dz) - body.getYRot());
                want = Mth.clamp(want, -70.0F, 70.0F);
            }
        }
        renderState.headYaw += (want - renderState.headYaw) * 0.25F;
        float target = renderState.sceneSpeaking ? 1.0F : 0.0F;
        renderState.armRaise += (target - renderState.armRaise) * 0.2F;
    }

    /**
     * Moves a scene body's bones, and answers whether it did: the head turns to the player while the body
     * stands where it was put, and the right arm comes up while that body's line is being said. The rigs were
     * asked what they hold - {@code head}, {@code rightArm} - and the arm's rest pose is no rotation at all,
     * so a raise is a rotation of its own rather than a correction to one.
     */
    public static boolean sceneBones(TolkienGeoRenderState renderState, BoneSnapshots snapshots) {
        if (!renderState.sceneBody) return false;
        // A body reading from a scroll looks down at it while the paper is up; everybody else's head stays
        // level and only turns.
        float headPitch = renderState.sceneReading && renderState.armRaise > 0.01F ? READ_PITCH : 0.0F;
        snapshots.ifPresent("head", bone -> bone.setRotation(headPitch * Mth.DEG_TO_RAD,
                renderState.headYaw * HEAD_SIGN * Mth.DEG_TO_RAD, 0f));
        // The legs carry the walk: hips forward and back on the body's own beat, arms against them. Nothing
        // else about the body moves, so the ground it was stood on stays where it was put.
        float swing = renderState.sceneWalking
                ? (float) Math.sin(renderState.sceneStride * STRIDE_RATE) * STRIDE_SWING * Mth.DEG_TO_RAD : 0.0F;
        if (renderState.sceneWalking) {
            snapshots.ifPresent("rightLeg", bone -> bone.setRotation(swing, 0f, 0f));
            snapshots.ifPresent("leftLeg", bone -> bone.setRotation(-swing, 0f, 0f));
            snapshots.ifPresent("leftArm", bone -> bone.setRotation(swing * ARM_SWING, 0f, 0f));
        }
        if (renderState.armRaise > 0.01F) {
            // A greeting comes forward and up, out in front of the shoulder. Reading does not: the arm comes up
            // less far and turns across the body, which brings the scroll in front of the face where a person
            // reading aloud actually holds it. The turn is eased with the lift, so the two arrive together.
            float raised = (renderState.sceneReading ? READ_RAISE : GREET_RAISE) * renderState.armRaise;
            float across = renderState.sceneReading ? READ_ACROSS * renderState.armRaise : 0.0F;
            snapshots.ifPresent("rightArm", bone ->
                    bone.setRotation(-raised * Mth.DEG_TO_RAD, across * Mth.DEG_TO_RAD, 0f));
        } else if (renderState.sceneWalking) {
            snapshots.ifPresent("rightArm", bone -> bone.setRotation(-swing * ARM_SWING, 0f, 0f));
        }
        return true;
    }

    /** The yaw that faces along a direction, in the game's own reckoning: nought is south, ninety is west. */
    private static float yawTowards(double dx, double dz) {
        return (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
    }

    /**
     * The hand: whatever one of the mod's folk is holding is drawn at its right arm. The monsters have had
     * this layer all along - a guard's blade, a wight's sword - but a villager never held anything, so the
     * three folk renderers had no layer to draw it with, and an item in a villager's hand was in the entity
     * and seen by nobody.
     * <p>
     * The pose is the fist by the numbers the guard's blade sits in. An item's own third-person transform is
     * what turns it in the hand, so a staff hangs the way its model was drawn to hang, and a blade sits
     * forward of the arm the way the guard's does.
     */
    static <E extends TolkienVillagerEntity> void holdItem(GeoEntityRenderer<E, TolkienGeoRenderState> renderer,
                                                          EntityRendererProvider.Context context) {
        renderer.withRenderLayer(new BlockAndItemGeoLayer<E, Void, TolkienGeoRenderState>(context, renderer) {
            @Override
            protected List<RenderData> getRelevantBones(E animatable, @Nullable Void relatedObject,
                                                        TolkienGeoRenderState renderState, float partialTick) {
                ItemStack held = animatable.getMainHandItem();
                if (held.isEmpty()) return List.of();
                ItemDisplayContext hand = ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                return List.of(RenderData.item("rightArm", hand, RenderUtil.createRenderStateForItem(held,
                        this.itemModelResolver, hand, animatable)));
            }

            @Override
            public void addRenderData(E animatable, @Nullable Void relatedObject,
                                      TolkienGeoRenderState renderState, float partialTick) {
                renderState.addGeckolibData(CONTENTS,
                        getRelevantBones(animatable, relatedObject, renderState, partialTick));
            }

            @Override
            protected void submitItemStackRender(PoseStack poseStack, GeoBone bone, ItemStackRenderState stackState,
                                                 ItemDisplayContext displayContext,
                                                 TolkienGeoRenderState renderState,
                                                 SubmitNodeCollector renderTasks, int packedLight) {
                poseStack.pushPose();
                poseStack.translate(0.45D, -0.4D, 0.0D);
                super.submitItemStackRender(poseStack, bone, stackState, displayContext, renderState, renderTasks,
                        packedLight);
                poseStack.popPose();
            }
        });
    }

    public HumanRender(EntityRendererProvider.Context context) {
        super(context, new HumanModel());
        holdItem(this, context);
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
        renderState.campKeeper = animatable.isCampKeeper();
        // Only a giver or a keeper has any use for a name - the two are the humans the pack names - and
        // asking every human in a village for one would be a string a frame for something no texture
        // lookup will ever ask about.
        var name = renderState.giverSkin > 0 || renderState.campKeeper ? animatable.getCustomName() : null;
        renderState.giverName = name == null ? "" : name.getString();
        sceneData(animatable, renderState, partialTick);
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

        // A scene's body is drawn by its own rules: a head that tracks the player and an arm that comes up,
        // both relative to where it was stood rather than to the world.
        if (sceneBones(renderState, snapshots)) return;
        snapshots.ifPresent("head", bone -> bone.setRotation(pitch * Mth.DEG_TO_RAD, yaw * 0.5f * Mth.DEG_TO_RAD, 0f));
    }
}
