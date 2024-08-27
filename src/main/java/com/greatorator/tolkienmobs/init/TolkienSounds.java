package com.greatorator.tolkienmobs.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, MODID);

    // Music Discs
    public static final Supplier<SoundEvent> RIDERSOFRIVENDELL = registerSoundEvent("music.ridersofrivendell");
    public static final ResourceKey<JukeboxSong> RIDERSOFRIVENDELL_KEY = createSong("music.ridersofrivendell");

    // Ambient
    public static final Supplier<SoundEvent> LIGHTNINGBUG_AMBIENT = registerSoundEvent("lightningbug_ambient");
    public static final Supplier<SoundEvent> LOCUST_AMBIENT = registerSoundEvent("locust_ambient");

    // Block & Item
    public static final Supplier<SoundEvent> HYPE_HORN = registerSoundEvent("hype_horn");

    private static ResourceKey<JukeboxSong> createSong(String name) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(MODID, name));
    }

    private static Supplier<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}