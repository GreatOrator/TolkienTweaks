package com.greatorator.tolkienmobs.entity.npc;

import com.greatorator.tolkienmobs.entity.TolkienVillagerEntity;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import net.minecraft.Util;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;

public class DwarfEntity extends TolkienVillagerEntity implements GeoEntity {
    public DwarfEntity(EntityType<? extends TolkienVillagerEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isSleeping()) {
            return null;
        } else {
            return this.isTrading() ? SoundEvents.VILLAGER_TRADE : TolkienSounds.IDLE_DWARF.get();
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return TolkienSounds.HURT_DWARF.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TolkienSounds.DEATH_DWARF.get();
    }

    @Nullable
    @Override
    public Villager getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        super.getBreedOffspring(level, otherParent);
        TolkienVariant variant = Util.getRandom(TolkienVariant.values(), this.random);
        DwarfEntity baby = TolkienEntities.ENTITY_TTM_DWARF.get().create(level);
        baby.setVariant(variant);

        return baby;
    }
}
