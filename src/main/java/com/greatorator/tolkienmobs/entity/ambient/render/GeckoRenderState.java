package com.greatorator.tolkienmobs.entity.ambient.render;

import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;

/**
 * Render state for the gecko.
 *
 * <p>The idle animation lives on the entity as an {@link AnimationState}; 1.21.2 renders from a
 * per-frame snapshot instead, so a copy of it travels with the state.</p>
 */
public class GeckoRenderState extends LivingEntityRenderState {
    public final AnimationState idleAnimationState = new AnimationState();
    public TolkienVariant variant = TolkienVariant.DEFAULT;
}
