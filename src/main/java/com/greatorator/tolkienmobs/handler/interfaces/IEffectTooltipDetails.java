package com.greatorator.tolkienmobs.handler.interfaces;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;

public interface IEffectTooltipDetails {
    List<Component> getTooltipDetails(MobEffectInstance effectInstance);
}
