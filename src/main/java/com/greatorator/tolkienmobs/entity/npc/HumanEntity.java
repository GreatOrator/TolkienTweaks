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
import net.minecraft.util.Util;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class HumanEntity extends TolkienVillagerEntity implements GeoEntity {
    private static final EntityDataAccessor<Boolean> WINTER = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HERALD = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.BOOLEAN);

    /** Server-side only: whether the spawn biome has been looked at yet. */
    private boolean winterResolved;

    public HumanEntity(EntityType<? extends TolkienVillagerEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(WINTER, false);
        builder.define(HERALD, false);
    }

    public boolean isWinter() {
        return this.entityData.get(WINTER);
    }

    protected void setWinter(boolean winter) {
        this.entityData.set(WINTER, winter);
    }

    /**
     * Whether this human is one of the mainline's own, which is what decides the clothes it wears.
     * <p>
     * The answer lives in a scoreboard tag, and a scoreboard tag is a server fact - the client is never
     * sent one - so it is copied into synched data here for a renderer to read on the far side. It is
     * read on every tick rather than once at spawn, so tagging a giver by hand puts the clothes on them
     * where they stand, and untagging takes them off again.
     */
    public boolean isHerald() {
        return this.entityData.get(HERALD);
    }

    private void setHerald(boolean herald) {
        this.entityData.set(HERALD, herald);
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
            boolean herald = this.entityTags().contains(QuestGivers.CAMPAIGN_TAG);
            if (herald != this.isHerald()) {
                this.setHerald(herald);
            }
        }
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
