package com.greatorator.tolkienmobs.entity.projectiles.render;

import net.minecraft.client.renderer.entity.state.EntityRenderState;

/**
 * Render state for the thrown projectiles.
 *
 * <p>1.21.2 moved rendering off live entities: the renderer only sees one of these, filled in once
 * per frame by {@code extractRenderState}. The boulder and the fell beast fireball share the same
 * shape of data, so they share the state.</p>
 */
public class ThrownProjectileRenderState extends EntityRenderState {
    /** True once the projectile has landed, which switches it from spinning to a fixed angle. */
    public boolean grounded;
    /** Degrees of extra spin added for every in-flight frame. */
    public float renderingRotation;
    /** Interpolated yaw, used while in flight. */
    public float yRot;
    /** The two fixed angles used once grounded. */
    public float groundedPitch;
    public float groundedYaw;
}
