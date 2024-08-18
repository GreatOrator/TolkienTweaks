package com.greatorator.tolkienmobs.handler;

import com.greatorator.tolkienmobs.handler.interfaces.IPlatformHelper;
import com.greatorator.tolkienmobs.handler.interfaces.IRegistryFactory;
import com.greatorator.tolkienmobs.handler.interfaces.IRegistryUtil;
import com.greatorator.tolkienmobs.handler.interfaces.ISleepEvents;

import java.util.ServiceLoader;

public class TolkienServices {
    public static final ISleepEvents SLEEP_EVENTS = load(ISleepEvents.class);
    public static final IRegistryUtil REGISTRY_UTIL = load(IRegistryUtil.class);
    public static final IRegistryFactory REGISTRY_FACTORY = load(IRegistryFactory.class);
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(
                        () -> new NullPointerException("Failed to load service for " + clazz.getName()));
        return loadedService;
    }
}