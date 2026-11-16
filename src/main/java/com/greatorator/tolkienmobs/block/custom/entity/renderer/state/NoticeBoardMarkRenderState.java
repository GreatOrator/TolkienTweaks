package com.greatorator.tolkienmobs.block.custom.entity.renderer.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class NoticeBoardMarkRenderState extends BlockEntityRenderState {
    /** The mark this board is wearing for the player reading it, or null when it has nothing to say. */
    public @Nullable Component mark;
}
