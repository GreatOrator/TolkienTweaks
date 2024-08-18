package com.greatorator.tolkienmobs.handler;

import com.greatorator.tolkienmobs.handler.interfaces.IRegistryObject;
import java.util.Collection;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface TolkienRegistry<T> {

  static <T> TolkienRegistry<T> get(ResourceKey<? extends Registry<T>> resourceKey, String modId) {
    return TolkienServices.REGISTRY_FACTORY.create(resourceKey, modId);
  }

  static <T> TolkienRegistry<T> get(Registry<T> registry, String modId) {
    return TolkienServices.REGISTRY_FACTORY.create(registry, modId);
  }

  <I extends T> IRegistryObject<I> register(String name, Supplier<? extends I> supplier);

  Collection<IRegistryObject<T>> getEntries();

  String getModId();
}
