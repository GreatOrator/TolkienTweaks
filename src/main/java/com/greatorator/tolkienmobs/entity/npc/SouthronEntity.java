package com.greatorator.tolkienmobs.entity.npc;

import com.greatorator.tolkienmobs.entity.TolkienWanderingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;

public class SouthronEntity extends TolkienWanderingEntity implements GeoEntity {
    public SouthronEntity(EntityType<? extends TolkienWanderingEntity> entityType, Level level) {
        super(entityType, level);
    }
}
