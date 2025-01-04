package com.greatorator.tolkienmobs.effect;

import com.greatorator.tolkienmobs.handler.interfaces.IEffectTooltipDetails;
import com.greatorator.tolkienmobs.util.GeneralUtility;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class NimbleEffect extends TolkienEffect implements IEffectTooltipDetails {
    static ResourceLocation nimbleStep = ResourceLocation.fromNamespaceAndPath(MODID, "nimbleness");

    public NimbleEffect(MobEffectCategory category, int color) {
        super(category, color);
        addAttributeModifier(Attributes.STEP_HEIGHT, nimbleStep, 1, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        AttributeInstance attributeInstance = entity.getAttribute(Attributes.STEP_HEIGHT);
        if (attributeInstance != null) {
            final AttributeModifier STEP_HEIGHT_MODIFIER = new AttributeModifier(nimbleStep, getStepHeight(amplifier), AttributeModifier.Operation.ADD_VALUE);
            attributeInstance.removeModifier(STEP_HEIGHT_MODIFIER);
            attributeInstance.addTransientModifier(STEP_HEIGHT_MODIFIER);
        }

        BlockPos pos = entity.blockPosition();
        if (!entity.onGround() && entity.getDeltaMovement().y < 0.0D) {
            float stepHeight = getStepHeight(amplifier);
            BlockPos belowPos = pos;
            boolean foundGround = false;
            for (int j = 0; j <= stepHeight; j++) {
                belowPos = pos.below(j);
                if (!entity.level().isEmptyBlock(belowPos)) {
                    foundGround = true;
                    break;
                }
            }

            double deltaY = belowPos.getY() - entity.getY();
            if (foundGround && deltaY < 0.0D) {
                Vec3 deltaPos = new Vec3(0, deltaY, 0);
                entity.move(MoverType.SELF, deltaPos);
            }
        }
        return true;
    }

    private static float getStepHeight(int amplifier) {
        return 1.0F * (amplifier + 1);
    }

    @Override
    public List<Component> getTooltipDetails(MobEffectInstance effectInstance) {
        Component stepHeight = GeneralUtility.formatEffectNumber(getStepHeight(effectInstance.getAmplifier()), 0, "");
        return List.of(
                stepHeight,
                Component.translatable("effect.tolkienmobs.elven_nimbleness.tooltip").withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
