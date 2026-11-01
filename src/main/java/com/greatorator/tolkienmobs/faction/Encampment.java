package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The campaign's own ground: where one player's camp is pitched, and the building of it.
 * <p>
 * The mod has always carried its buildings as templates - the halls, the towers, the arena - and has
 * never once put one down itself: they are all the world generator's, or an operator's with
 * {@code /place structure}. A camp cannot wait for either. It has to stand somewhere chosen for a
 * particular player at the moment the page is taken, and be there when they arrive without being there
 * for anybody who has not been sent.
 * <p>
 * So the spot is chosen when the work is taken and written down against the player, and the tent itself
 * is not built until they are close enough for the chunk to be loaded anyway - sixty-four blocks, which
 * is a little over four chunks. That is the same courtesy {@link Campaign} does the Rider and the quarry:
 * nothing is dropped into ground nobody is looking at, and no chunk is ever force-loaded to hold a tent
 * that a player may never visit.
 * <p>
 * The footprint is tested before anything is placed, because a tent dropped on a slope clips and a tent
 * dropped in a river floats. A site that will not take one is searched for a little way around the spot
 * that was written down, and if none of them will, nothing is built and the next tick tries again -
 * ground is not a thing to be argued with.
 */
public final class Encampment extends SavedData {
    private static final String SAVE_DATA_NAME = "encampment";

    /** The tent the campaign pitches, which is a template the mod already ships. */
    public static final String WILD_CAMP = "tolkienmobs:wild_tent";

    /** How far out a camp is pitched, in blocks, and how much of that is left to chance. */
    private static final int PITCH_MIN = 48;
    private static final int PITCH_SPREAD = 48;
    /** How far off the pitch the spot is nudged sideways, so a camp is not always dead ahead. */
    private static final int PITCH_ACROSS = 8;
    /** How close the player has to be before the tent is built, in blocks. */
    private static final int BUILD_REACH = 64;
    /** How close they have to be before they have found it, in blocks. */
    private static final int FOUND_REACH = 12;
    /** How far from the written spot a site is looked for, and in what steps. */
    private static final int[] SHIFT = {0, 4, -4, 8, -8, 12, -12, 16, -16};

    /**
     * The template's own size, as the file is written: eighteen long, seven wide and six high. It is
     * read here rather than from the template because the ground has to be judged before anything is
     * loaded, and a footprint that is guessed wrong is a tent with a hillside through it.
     */
    private static final int TENT_LONG = 18;
    private static final int TENT_WIDE = 7;
    /** How much rise and fall across the footprint a tent will still sit on. */
    private static final int TENT_DROP = 2;

    /** The tag the body minding a camp wears, so it can be told from any other of its folk. */
    public static final String KEEPER_TAG = "tolkienmobs:camp_keeper";

    private static final Map<UUID, Camp> CAMPS = new HashMap<>();
    private static final Encampment NO_SERVER = new Encampment(false);

    /** One player's camp: where it is pitched, from what, and how far along it is. */
    public record Camp(UUID player, ResourceKey<Level> dimension, BlockPos centre, String structure,
                       boolean built, boolean found) {
        static final Codec<Camp> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("player").forGetter(Camp::player),
                ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(Camp::dimension),
                BlockPos.CODEC.fieldOf("centre").forGetter(Camp::centre),
                Codec.STRING.fieldOf("structure").forGetter(Camp::structure),
                Codec.BOOL.optionalFieldOf("built", false).forGetter(Camp::built),
                Codec.BOOL.optionalFieldOf("found", false).forGetter(Camp::found)
        ).apply(instance, Camp::new));

        Camp pitched(BlockPos site) {
            return new Camp(player, dimension, site, structure, true, found);
        }

        Camp searched() {
            return new Camp(player, dimension, centre, structure, built, true);
        }
    }

    public static final Codec<Encampment> CODEC = Camp.CODEC.listOf()
            .xmap(Encampment::from, Encampment::toList);

    public static final SavedDataType<Encampment> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath("tolkienmobs", SAVE_DATA_NAME), Encampment::new, CODEC);

    /** A world with no camp file starts with nothing pitched, and forgets whatever the last world did. */
    private Encampment() {
        this(true);
    }

    /** A stand-in for a level with no server behind it, which neither reads nor clears anything. */
    private Encampment(boolean fresh) {
        super();
        if (fresh) CAMPS.clear();
    }

    // ---- pitching ----

    /**
     * Chooses where this player's camp will stand, from the template it will be built from. It is
     * chosen in the direction they are facing rather than at random, so a player who takes a page about
     * a camp and walks on will walk toward it.
     *
     * @return whether a spot could be written down at all
     */
    public static boolean plan(ServerPlayer player, String structure) {
        if (!(player.level() instanceof ServerLevel level)) return false;
        Encampment store = instance(level);
        RandomSource random = level.getRandom();
        Direction facing = player.getDirection();
        Direction across = facing.getClockWise();
        int reach = PITCH_MIN + random.nextInt(PITCH_SPREAD);
        int side = random.nextInt(PITCH_ACROSS * 2 + 1) - PITCH_ACROSS;
        BlockPos centre = player.blockPosition().offset(
                facing.getStepX() * reach + across.getStepX() * side,
                0,
                facing.getStepZ() * reach + across.getStepZ() * side);
        CAMPS.put(player.getUUID(), new Camp(player.getUUID(), level.dimension(), centre, structure,
                false, false));
        store.setDirty();
        return true;
    }

    /** Where this player's camp is, or null when they have not been sent to one. */
    @Nullable
    public static Camp campOf(ServerPlayer player) {
        instance(player.level());
        return CAMPS.get(player.getUUID());
    }

    /** Whether the player is standing in the camp they were sent to find, tent and all. */
    public static boolean reached(ServerPlayer player) {
        Camp camp = campOf(player);
        if (camp == null || !camp.built()) return false;
        if (!player.level().dimension().equals(camp.dimension())) return false;
        return away(player, camp) <= FOUND_REACH;
    }

    /**
     * Looks at where the player is, and builds their camp if they have come close enough to see it, then
     * has the body minding it say his piece once they are inside it. Called from the same clock that
     * names the ground they are walking over.
     */
    public static void tick(ServerPlayer player) {
        Camp camp = campOf(player);
        if (camp == null) return;
        if (!player.level().dimension().equals(camp.dimension())) return;
        double away = away(player, camp);
        if (!camp.built()) {
            if (away <= BUILD_REACH) pitch(player, camp);
            return;
        }
        if (!camp.found() && away <= FOUND_REACH && player.level() instanceof ServerLevel level) {
            CAMPS.put(player.getUUID(), camp.searched());
            instance(level).setDirty();
            player.sendSystemMessage(Component.translatable("quest.tolkienmobs.camp.found",
                    keeperName(player, camp)).withStyle(ChatFormatting.GOLD));
        }
    }

    /** Builds the camp: the template on tested ground, and the body who keeps it. */
    private static void pitch(ServerPlayer player, Camp camp) {
        if (!(player.level() instanceof ServerLevel level)) return;
        if (!level.hasChunkAt(camp.centre())) return;
        BlockPos site = locate(level, camp.centre());
        if (site == null) return;
        Identifier id = Identifier.tryParse(camp.structure());
        if (id == null) return;
        StructureTemplate template = level.getStructureManager().get(id).orElse(null);
        // A template that is not in the pack builds nothing, and a camp nobody can stand in is not a
        // camp: it is left unbuilt rather than counted as built, so a pack that adds the file later
        // still gets one.
        if (template == null || template.getSize().getX() <= 0) return;
        template.placeInWorld(level, site, site, new StructurePlaceSettings(), level.getRandom(), 2);
        standKeeper(level, site);
        CAMPS.put(player.getUUID(), camp.pitched(site));
        instance(level).setDirty();
    }

    /** The nearest ground to the written spot that will take a tent, or null when none of it will. */
    @Nullable
    private static BlockPos locate(ServerLevel level, BlockPos written) {
        for (int dx : SHIFT) {
            for (int dz : SHIFT) {
                BlockPos site = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        written.offset(dx, 0, dz));
                if (!level.getFluidState(site).isEmpty()) continue;
                if (!level.getBlockState(site.above()).isAir()) continue;
                if (footprintFits(level, site)) return site;
            }
        }
        return null;
    }

    /**
     * Whether the ground will take the tent lying where it is written: every column of the footprint
     * within a couple of blocks of the site's own height, over solid ground, with no water standing in
     * any of them.
     */
    private static boolean footprintFits(ServerLevel level, BlockPos site) {
        for (int dx = 0; dx < TENT_LONG; dx++) {
            for (int dz = 0; dz < TENT_WIDE; dz++) {
                BlockPos column = site.offset(dx, 0, dz);
                int height = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        column.getX(), column.getZ());
                if (Math.abs(height - site.getY()) > TENT_DROP) return false;
                if (!level.getBlockState(new BlockPos(column.getX(), height - 1, column.getZ())).isSolid()) {
                    return false;
                }
                if (!level.getFluidState(new BlockPos(column.getX(), height, column.getZ())).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Puts somebody at the fire. He is an ordinary body of his folk with a name and a tag, not a giver:
     * a camp is a place a player is sent to, and the man minding it is what makes it one rather than
     * what makes it another bench to take work from.
     */
    private static void standKeeper(ServerLevel level, BlockPos site) {
        Entity entity = TolkienEntities.ENTITY_TTM_HUMAN.get().create(level, EntitySpawnReason.EVENT);
        if (!(entity instanceof Mob keeper)) return;
        BlockPos stand = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                site.offset(TENT_LONG / 2, 0, TENT_WIDE + 2));
        keeper.snapTo(stand.getX() + 0.5D, stand.getY(), stand.getZ() + 0.5D,
                level.getRandom().nextFloat() * 360.0F, 0.0F);
        keeper.setPersistenceRequired();
        keeper.addTag(KEEPER_TAG);
        keeper.setCustomName(QuestPool.giverName(level.getRandom()));
        keeper.setCustomNameVisible(true);
        level.addFreshEntity(keeper);
    }

    /** The keeper's name, if he is still standing at a camp this player can see. */
    private static Component keeperName(ServerPlayer player, Camp camp) {
        var found = player.level().getEntities(player, player.getBoundingBox().inflate(BUILD_REACH),
                entity -> entity.entityTags().contains(KEEPER_TAG));
        return found.isEmpty() ? Component.empty() : found.getFirst().getName();
    }

    /**
     * How far the player is from the camp's fire, across the ground. The fire is the middle of the
     * tent's own footprint rather than the corner the template is placed from: a player who walked up to
     * the open end of a tent is at the tent, and should not have to reach the far corner of it.
     */
    private static double away(ServerPlayer player, Camp camp) {
        double dx = player.getX() - (camp.centre().getX() + TENT_LONG / 2.0D);
        double dz = player.getZ() - (camp.centre().getZ() + TENT_WIDE / 2.0D);
        return Math.sqrt(dx * dx + dz * dz);
    }

    // ---- plumbing ----

    private static Encampment instance(Level level) {
        if (level instanceof ServerLevel serverLevel && serverLevel.getServer() != null) {
            ServerLevel overworld = serverLevel.getServer().getLevel(Level.OVERWORLD);
            if (overworld != null) return overworld.getDataStorage().computeIfAbsent(TYPE);
        }
        return NO_SERVER;
    }

    private static Encampment from(List<Camp> camps) {
        CAMPS.clear();
        for (Camp camp : camps) {
            CAMPS.put(camp.player(), camp);
        }
        // Read without clearing: the map was just filled from the file.
        return new Encampment(false);
    }

    private List<Camp> toList() {
        return new ArrayList<>(CAMPS.values());
    }
}
