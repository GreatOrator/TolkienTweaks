package com.greatorator.tolkienmobs.block.custom.entity.renderer.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;

public class FireplaceRenderState extends BlockEntityRenderState {
    public static final int SLOTS = 3;

    public Direction facing = Direction.NORTH;
    public final ItemStackRenderState[] items = new ItemStackRenderState[SLOTS];

    public FireplaceRenderState() {
        for (int slot = 0; slot < SLOTS; slot++) {
            this.items[slot] = new ItemStackRenderState();
        }
    }
}
