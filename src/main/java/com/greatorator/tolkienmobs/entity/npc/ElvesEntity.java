package com.greatorator.tolkienmobs.entity.npc;

import com.greatorator.tolkienmobs.entity.TolkienVillagerEntity;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import net.minecraft.Util;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;

public class ElvesEntity extends TolkienVillagerEntity implements GeoEntity {
    public ElvesEntity(EntityType<? extends TolkienVillagerEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Nullable
    @Override
    public Villager getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        super.getBreedOffspring(level, otherParent);
        TolkienVariant variant = Util.getRandom(TolkienVariant.values(), this.random);
        ElvesEntity baby = TolkienEntities.ENTITY_TTM_ELVES.get().create(level);
        baby.setVariant(variant);

        return baby;
    }
}
