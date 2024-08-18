package com.greatorator.tolkienmobs.handler.interfaces;

import com.greatorator.tolkienmobs.handler.TolkienRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface IRegistryFactory {

  <T> TolkienRegistry<T> create(ResourceKey<? extends Registry<T>> resourceKey, String modId);

  default <T> TolkienRegistry<T> create(Registry<T> registry, String modId) {
    return create(registry.key(), modId);
  }
}
