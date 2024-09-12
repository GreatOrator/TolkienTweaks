package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC = createEvent("music.tolkienmobs.tolkien_mobs");

    // Music Discs
    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_DISC_ALLTHATGLITTERS = createEvent("music_disc.tolkienmobs.allthatglitterserebor");
    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_DISC_CONCERNINGHOBBITS = createEvent("music_disc.tolkienmobs.concerninghobbits");
    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_DISC_MINASTIRITH = createEvent("music_disc.tolkienmobs.minastirith");
    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_DISC_MURDERFROG = createEvent("music_disc.tolkienmobs.murderfrog");
    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_DISC_TOMBOMBADIL = createEvent("music_disc.tolkienmobs.mysteryoftombombadil");
    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_DISC_REDERSSONG = createEvent("music_disc.tolkienmobs.rederssong");
    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_DISC_RIDERSOFRIVENDELL = createEvent("music_disc.tolkienmobs.ridersofrivendell");
    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_DISC_LIGHTOFLOTHLORIEN = createEvent("music_disc.tolkienmobs.thelightoflothlorien");
    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_DISC_TOUCHOFSHADE = createEvent("music_disc.tolkienmobs.touchofshade");
    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_DISC_TROLLFUMBLE = createEvent("music_disc.tolkienmobs.trollfumble");
    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_DISC_WAKEOFEDORAS = createEvent("music_disc.tolkienmobs.wakeofedoras");
    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_DISC_WITCHBATTLE = createEvent("music_disc.tolkienmobs.witchbattle");

        // Blocks
    public static final DeferredHolder<SoundEvent, SoundEvent> CATHEDRALBELL = createEvent("block.tolkienmobs.cathedralbell");
    public static final DeferredHolder<SoundEvent, SoundEvent> CHEST_CLOSE = createEvent("block.tolkienmobs.chest_close");
    public static final DeferredHolder<SoundEvent, SoundEvent> CHEST_OPEN = createEvent("block.tolkienmobs.chest_open");
    public static final DeferredHolder<SoundEvent, SoundEvent> LIGHTNINGBUG_AMBIENT = createEvent("block.tolkienmobs.lightningbug_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> LOCUST_AMBIENT = createEvent("block.tolkienmobs.locust_ambient");

        // Items
    public static final DeferredHolder<SoundEvent, SoundEvent> HYPE_HORN = createEvent("item.tolkienmobs.hype_horn");

        // Ambient
    public static final DeferredHolder<SoundEvent, SoundEvent> ARDA_PORTAL = createEvent("ambient.tolkienmobs.arda_portal");
    public static final DeferredHolder<SoundEvent, SoundEvent> KHAZAD_DUM = createEvent("ambient.tolkienmobs.khazaddum");
    public static final DeferredHolder<SoundEvent, SoundEvent> MEDIEVAL_CITY = createEvent("ambient.tolkienmobs.medievalcity");
    public static final DeferredHolder<SoundEvent, SoundEvent> MEDIEVAL_MARKET = createEvent("ambient.tolkienmobs.medievalcitymarket");
    public static final DeferredHolder<SoundEvent, SoundEvent> MEDIEVAL_SEAPORT = createEvent("ambient.tolkienmobs.medievalseaport");
    public static final DeferredHolder<SoundEvent, SoundEvent> MEDIEVAL_TAVERN = createEvent("ambient.tolkienmobs.medievaltavern");
    public static final DeferredHolder<SoundEvent, SoundEvent> PATH_OF_UNDEAD = createEvent("ambient.tolkienmobs.pathsofthedead");
    public static final DeferredHolder<SoundEvent, SoundEvent> UNDERWORLD = createEvent("ambient.tolkienmobs.underworld");
    public static final DeferredHolder<SoundEvent, SoundEvent> WATERWORKS = createEvent("ambient.tolkienmobs.waterworks");

    // Entities
        // Ambient

    private static DeferredHolder<SoundEvent, SoundEvent> createEvent(String sound) {
        return SOUND_EVENTS.register(sound, () -> SoundEvent.createVariableRangeEvent(TolkienMobsMain.prefix(sound)));
    }
}