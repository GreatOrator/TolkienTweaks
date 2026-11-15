package com.greatorator.tolkienmobs.block.custom.entity.renderer.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class SignPostArmRenderState extends BlockEntityRenderState {
    /** The lower or only board's place, or null when it points at nothing and nothing is drawn. */
    public @Nullable Component destination;
    /** How far away that place is, or null when there is no distance to show. */
    public @Nullable Component distance;
    /** The upper board's place, for a double arm, or null on a single board. */
    public @Nullable Component destination2;
    /** How far away the upper board's place is, or null. */
    public @Nullable Component distance2;
    /** Which four pixel step of the cell the board hangs in: 0-3 on a single, 0 or 2 on a double. */
    public int level;
    /** Which way the board points, so its lettering can be turned onto the same face. */
    public Direction facing = Direction.SOUTH;
}
