package com.greatorator.tolkienmobs.handler.interfaces;

import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Objects;

public interface HolderHolder<R> {
    DeferredHolder<R, ? extends R> holder();

    default ResourceKey<R> key()
    {
        return Objects.requireNonNull(holder().getKey());
    }
}
