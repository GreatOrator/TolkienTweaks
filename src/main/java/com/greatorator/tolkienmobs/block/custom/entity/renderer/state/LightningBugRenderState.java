package com.greatorator.tolkienmobs.block.custom.entity.renderer.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class LightningBugRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public int yaw;
    public float glow;
    public float randRot;
}
