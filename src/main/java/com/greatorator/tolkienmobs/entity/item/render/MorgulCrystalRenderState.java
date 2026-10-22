package com.greatorator.tolkienmobs.entity.item.render;

import net.minecraft.client.renderer.entity.state.EntityRenderState;

/** Render state for the morgul crystal: its spin time and whether it shows its base. */
public class MorgulCrystalRenderState extends EntityRenderState {
    public float ticks;
    public boolean showsBottom;
}
