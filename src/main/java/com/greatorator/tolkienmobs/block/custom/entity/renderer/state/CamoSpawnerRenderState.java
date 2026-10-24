package com.greatorator.tolkienmobs.block.custom.entity.renderer.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jetbrains.annotations.Nullable;

public class CamoSpawnerRenderState extends BlockEntityRenderState {
    public @Nullable EntityRenderState displayEntity;
    public float spin;
    public float scale;
}
