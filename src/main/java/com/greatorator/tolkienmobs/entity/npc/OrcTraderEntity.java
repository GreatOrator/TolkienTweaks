package com.greatorator.tolkienmobs.entity.npc;

import com.greatorator.tolkienmobs.entity.TolkienWanderingEntity;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;

public class OrcTraderEntity extends TolkienWanderingEntity implements GeoEntity {
    public OrcTraderEntity(EntityType<? extends TolkienWanderingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isSleeping()) {
            return null;
        } else {
            return this.isTrading() ? SoundEvents.VILLAGER_TRADE : TolkienSounds.IDLE_ORC.get();
        }
    }
}
