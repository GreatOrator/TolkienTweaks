package com.greatorator.tolkienmobs.handler.interfaces;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

public interface IdHolder<T> extends Supplier<T> {
    DeferredHolder<? super T, T> holder();

    default ResourceLocation getId()
    {
        return holder().getId();
    }
}
