package com.greatorator.tolkienmobs.handler.interfaces;

import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public interface IRegistryObject<T> extends Supplier<T> {

  ResourceKey<T> getResourceKey();

  ResourceLocation getId();

  @Override
  T get();

  Holder<T> asHolder();
}