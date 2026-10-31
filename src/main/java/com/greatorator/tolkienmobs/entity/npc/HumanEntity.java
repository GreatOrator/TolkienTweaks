package com.greatorator.tolkienmobs.entity.npc;

import com.geckolib.animatable.GeoEntity;
import com.greatorator.tolkienmobs.entity.TolkienVillagerEntity;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import com.greatorator.tolkienmobs.faction.QuestGivers;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.init.TolkienTags;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.util.Util;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class HumanEntity extends TolkienVillagerEntity implements GeoEntity {
    private static final EntityDataAccessor<Boolean> WINTER = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> GIVER_SKIN = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);

    /** How close a player has to be before a hall giver turns to look at them. */
    private static final double GREETING_RANGE = 5.0D;

    /** Server-side only: whether the spawn biome has been looked at yet. */
    private boolean winterResolved;

    public HumanEntity(EntityType<? extends TolkienVillagerEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(WINTER, false);
        builder.define(GIVER_SKIN, 0);
    }

    public boolean isWinter() {
        return this.entityData.get(WINTER);
    }

    protected void setWinter(boolean winter) {
        this.entityData.set(WINTER, winter);
    }

    /**
     * Which of the folk who carry work this human is dressed as: {@code 0} for an ordinary human,
     * otherwise an alliance's ordinal plus one.
     * <p>
     * The answer lives in a scoreboard tag, and a scoreboard tag is a server fact - the client is never
     * sent one - so it is copied into synched data here for a renderer to read on the far side. It is
     * read on every tick rather than once at spawn, so tagging a giver by hand puts the clothes on them
     * where they stand and untagging takes them off again.
     */
    public int giverSkin() {
        return this.entityData.get(GIVER_SKIN);
    }

    private void setGiverSkin(int skin) {
        this.entityData.set(GIVER_SKIN, skin);
    }

    @Override
    public void tick() {
        super.tick();
        // Resolved once, in the biome the human was first seen in, so that it keeps the clothes it
        // spawned with even if it later walks somewhere warmer. Doing this on the first tick rather
        // than in finalizeSpawn also catches humans placed from structure NBT, which never run it.
        // Which biomes count as cold is the tolkienmobs:cold tag's business, so a datapack can move
        // the line without a rebuild.
        if (!this.level().isClientSide() && !this.winterResolved) {
            this.winterResolved = true;
            this.setWinter(this.level().getBiome(this.blockPosition()).is(TolkienTags.Biomes.COLD));
        }
        // Only written when it changes, so a tick is a tag lookup and not a packet.
        if (!this.level().isClientSide()) {
            int skin = QuestGivers.giverSkinOf(this);
            if (skin != this.giverSkin()) {
                this.setGiverSkin(skin);
            }
            // A hall gives its giver a home area, and a room with a body walking about it reads as a
            // place rather than a display case.
            if (this.hasHome()) {
                this.keepInsideHome();
                this.greetNearbyPlayer();
            }
        }
    }

    /**
     * A body at a hall door should notice the player who walks in. The look control is what vanilla turns
     * a head with, so a giver with a home asks it to look at the nearest player within a few blocks and lets
     * it turn at its own pace; nothing about the walk changes, and a player who steps away is forgotten.
     */
    private void greetNearbyPlayer() {
        Player player = this.level().getNearestPlayer(this, GREETING_RANGE);
        if (player != null && player.isAlive() && !player.isSpectator()) {
            this.getLookControl().setLookAt(player, 10.0F, 10.0F);
        }
    }

    /**
     * Keeps a body that has been given a home area inside it. A villager stroll is written for a village
     * and not for a room: with no meeting point it walks toward the nearest one, which from a hall means
     * out of the door and off across the country. Rather than take the stroll away, the target it asks for
     * is pulled back inside the home circle, so the walk keeps its direction and the body keeps its room;
     * one that has already stepped outside is sent back to the middle.
     */
    private void keepInsideHome() {
        Vec3 middle = Vec3.atBottomCenterOf(this.getHomePosition());
        var wanted = this.getBrain().getMemory(MemoryModuleType.WALK_TARGET);
        if (wanted.isPresent()) {
            Vec3 asked = wanted.get().getTarget().currentPosition();
            if (!this.isWithinHome(asked)) {
                this.getBrain().setMemory(MemoryModuleType.WALK_TARGET,
                        new WalkTarget(this.pulledInside(middle, asked), 0.5F, 1));
            }
        }
        if (!this.isWithinHome()) {
            this.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(middle, 0.6F, 1));
        }
    }

    /** A point on the way to the target, stopped short of the home circle edge. */
    private Vec3 pulledInside(Vec3 middle, Vec3 target) {
        double dx = target.x - middle.x;
        double dz = target.z - middle.z;
        double distance = Math.sqrt(dx * dx + dz * dz);
        double limit = Math.max(1.0D, this.getHomeRadius() - 1.0D);
        if (distance <= limit || distance < 1.0E-4D) {
            return target;
        }
        double scale = limit / distance;
        return new Vec3(middle.x + dx * scale, target.y, middle.z + dz * scale);
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Winter", this.isWinter());
        output.putBoolean("WinterResolved", this.winterResolved);
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setWinter(input.getBooleanOr("Winter", false));
        this.winterResolved = input.getBooleanOr("WinterResolved", false);
    }

    @Nullable
    @Override
    public Villager getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        super.getBreedOffspring(level, otherParent);
        TolkienVariant variant = Util.getRandom(TolkienVariant.values(), this.random);
        HumanEntity baby = TolkienEntities.ENTITY_TTM_HUMAN.get().create(level, EntitySpawnReason.BREEDING);
        baby.setVariant(variant);
        baby.setWinter(this.isWinter());
        baby.winterResolved = true;

        return baby;
    }
}
