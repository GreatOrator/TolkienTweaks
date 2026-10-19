package com.greatorator.tolkienmobs.entity.npc;

import com.greatorator.tolkienmobs.entity.TolkienVillagerEntity;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;

public class HumanEntity extends TolkienVillagerEntity implements GeoEntity {
    /**
     * At or below this temperature a spawning human is dressed for winter; it is the same 0.15
     * threshold vanilla uses to decide a biome is cold enough to snow. Forochel sits at -0.5, while
     * the cool-but-grassy biomes (Barrow Downs at 0.25, Iron Hills at 0.2) stay in normal clothes.
     */
    private static final float WINTER_TEMPERATURE = 0.15F;
    private static final EntityDataAccessor<Boolean> WINTER = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.BOOLEAN);

    /** Server-side only: whether the spawn biome has been looked at yet. */
    private boolean winterResolved;

    public HumanEntity(EntityType<? extends TolkienVillagerEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(WINTER, false);
    }

    public boolean isWinter() {
        return this.entityData.get(WINTER);
    }

    protected void setWinter(boolean winter) {
        this.entityData.set(WINTER, winter);
    }

    @Override
    public void tick() {
        super.tick();
        // Resolved once, in the biome the human was first seen in, so that it keeps the clothes it
        // spawned with even if it later walks somewhere warmer. Doing this on the first tick rather
        // than in finalizeSpawn also catches humans placed from structure NBT, which never run it.
        if (!this.level().isClientSide && !this.winterResolved) {
            this.winterResolved = true;
            this.setWinter(this.level().getBiome(this.blockPosition()).value().getBaseTemperature() < WINTER_TEMPERATURE);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Winter", this.isWinter());
        compound.putBoolean("WinterResolved", this.winterResolved);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setWinter(compound.getBoolean("Winter"));
        this.winterResolved = compound.getBoolean("WinterResolved");
    }

    @Nullable
    @Override
    public Villager getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        super.getBreedOffspring(level, otherParent);
        TolkienVariant variant = Util.getRandom(TolkienVariant.values(), this.random);
        HumanEntity baby = TolkienEntities.ENTITY_TTM_HUMAN.get().create(level);
        baby.setVariant(variant);
        baby.setWinter(this.isWinter());
        baby.winterResolved = true;

        return baby;
    }
}
