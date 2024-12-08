package com.greatorator.tolkienmobs.entity.ambient;

import com.greatorator.tolkienmobs.entity.TolkienAmbient;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class SquirrelEntity extends TolkienAmbient {
    public SquirrelEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

//    @Override
//    protected void registerGoals() {
//        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25, stack -> stack.is(TolkienItems.TREE_ACORN), false));
//    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(TolkienItems.TREE_ACORN.get());
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return TolkienEntities.ENTITY_TTM_GECKO.get().create(serverLevel);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    /** Sounds */
    @Override
    protected SoundEvent getAmbientSound() {
        return TolkienSounds.IDLE_SQUIRREL.get();
    }

    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSourceIn) {
        return TolkienSounds.HURT_SQUIRREL.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TolkienSounds.DEATH_SQUIRREL.get();
    }
}
