package com.greatorator.tolkienmobs.block.custom.entity.renderer.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class SignPostArmRenderState extends BlockEntityRenderState {
    /** The place's name, or null when the arm points at nothing and nothing is drawn. */
    public @Nullable Component destination;
    /** How far away it is, or null when the arm has no distance to show. */
    public @Nullable Component distance;
    /** Which four pixel step of the cell the board hangs in. */
    public int level;
}
