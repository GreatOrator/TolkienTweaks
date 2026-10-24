package com.greatorator.tolkienmobs.handler.interfaces;

import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

public interface IdHolder<T> extends Supplier<T> {
    DeferredHolder<? super T, T> holder();

    default Identifier getId()
    {
        return holder().getId();
    }
}
