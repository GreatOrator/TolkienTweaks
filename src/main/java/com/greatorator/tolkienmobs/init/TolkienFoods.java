package com.greatorator.tolkienmobs.init;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

public class TolkienFoods {
    // Foods
    public static final FoodProperties LEMBAS = new FoodProperties.Builder().nutrition(3).saturationModifier(0.25f).build();
    public static final Consumable LEMBAS_CONSUMABLE = food()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.ABSORPTION, 12000, 5), 0.3F))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 5), 0.3F))
            .build();

    public static final FoodProperties HONEY_CAKE = new FoodProperties.Builder().nutrition(15).saturationModifier(15.0F).build();
    public static final Consumable HONEY_CAKE_CONSUMABLE = food()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.ABSORPTION, 6000, 3), 0.3F))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.REGENERATION, 50, 3), 0.3F))
            .build();

    public static final FoodProperties CRAM = new FoodProperties.Builder().nutrition(10).saturationModifier(10.0F).build();
    public static final Consumable CRAM_CONSUMABLE = food()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.ABSORPTION, 3000, 1), 0.3F))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.REGENERATION, 25, 5), 0.3F))
            .build();

    public static final FoodProperties MONSTER_FLESH = new FoodProperties.Builder().nutrition(5).saturationModifier(2.0F).build();
    public static final Consumable MONSTER_FLESH_CONSUMABLE = food()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 2), 0.3F))
            .build();

    public static final FoodProperties INSECT = new FoodProperties.Builder().nutrition(1).saturationModifier(1.0F).build();
    public static final Consumable INSECT_CONSUMABLE = food()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.HUNGER, 10, 1), 0.3F))
            .build();

    public static final FoodProperties GOLDEN_INSECT = new FoodProperties.Builder().nutrition(2).saturationModifier(3.0F).build();
    public static final Consumable GOLDEN_INSECT_CONSUMABLE = food()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.HUNGER, 40, 2), 0.3F))
            .build();

    public static final FoodProperties TREE_ACORN = new FoodProperties.Builder().nutrition(1).saturationModifier(1.0F).build();
    public static final Consumable TREE_ACORN_CONSUMABLE = food()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 10, 1), 0.3F))
            .build();

    public static final FoodProperties GOLDEN_TREE_ACORN = new FoodProperties.Builder().nutrition(2).saturationModifier(3.0F).build();
    public static final Consumable GOLDEN_TREE_ACORN_CONSUMABLE = food()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 2), 0.3F))
            .build();

    public static final FoodProperties FOOD_HONEY = new FoodProperties.Builder().nutrition(2).saturationModifier(3.0F).build();
    public static final Consumable FOOD_HONEY_CONSUMABLE = food()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 1), 0.3F))
            .build();

    public static final FoodProperties FOOD_BRAMBLES_BERRY = new FoodProperties.Builder().nutrition(4).saturationModifier(0.1F).build();
    public static final Consumable FOOD_BRAMBLES_BERRY_CONSUMABLE = food()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.8F))
            .build();

    // Drinks
    public static final FoodProperties MIRUVOR = new FoodProperties.Builder().nutrition(1).saturationModifier(1.0F).alwaysEdible().build();
    public static final Consumable MIRUVOR_CONSUMABLE = drink()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 3000, 3), 0.3F))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 3), 0.3F))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.CONFUSION, 40, 3), 0.3F))
            .build();

    public static final FoodProperties GROG = new FoodProperties.Builder().nutrition(10).saturationModifier(8.0F).alwaysEdible().build();
    public static final Consumable GROG_CONSUMABLE = drink()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1500, 1), 0.3F))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.REGENERATION, 300, 3), 0.3F))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 3), 0.3F))
            .build();

    public static final FoodProperties DRINK_ENT_DRAUGHT = new FoodProperties.Builder().nutrition(1).saturationModifier(1.0F).alwaysEdible().build();
    public static final Consumable DRINK_ENT_DRAUGHT_CONSUMABLE = drink()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(TolkienMobEffects.ENT_STANCE, 600, 0), 0.3F))
            .build();

    public static final FoodProperties DRINK_PERSONAL_BLACKSMITH = new FoodProperties.Builder().nutrition(1).saturationModifier(1.0F).alwaysEdible().build();
    public static final Consumable DRINK_PERSONAL_BLACKSMITH_CONSUMABLE = drink()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(TolkienMobEffects.PERSONAL_BLACKSMITH, 600, 0), 0.3F))
            .build();

    public static final FoodProperties DRINK_ELF_FLEETFOOT = new FoodProperties.Builder().nutrition(1).saturationModifier(1.0F).alwaysEdible().build();
    public static final Consumable DRINK_ELF_FLEETFOOT_CONSUMABLE = drink()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(TolkienMobEffects.ELF_NIMBLENESS, 600, 0), 0.3F))
            .build();

    public static final FoodProperties DRINK_ELF_VITALITY = new FoodProperties.Builder().nutrition(1).saturationModifier(1.0F).alwaysEdible().build();
    public static final Consumable DRINK_ELF_VITALITY_CONSUMABLE = drink()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(TolkienMobEffects.ELF_VITALITY, 600, 0), 0.3F))
            .build();

    public static final FoodProperties DRINK_ERU_BLESSING = new FoodProperties.Builder().nutrition(10).saturationModifier(8.0F).alwaysEdible().build();
    public static final Consumable DRINK_ERU_BLESSING_CONSUMABLE = drink()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(TolkienMobEffects.BLESSING_OF_ERU, 100, 0), 0.3F))
            .build();

    private static Consumable.Builder food() {
        return Consumable.builder();
    }

    private static Consumable.Builder drink() {
        return Consumable.builder().animation(ItemUseAnimation.DRINK);
    }
}
