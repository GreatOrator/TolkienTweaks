package com.greatorator.tolkienmobs.block.custom.entity.renderer.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class MilestoneRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public @Nullable Component name;
}
