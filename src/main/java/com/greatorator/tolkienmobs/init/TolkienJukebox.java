package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;

public class TolkienJukebox {
    public static final ResourceKey<JukeboxSong> EREBOR = registerKey("allthatglitterserebor");
    public static final ResourceKey<JukeboxSong> HOBBITS = registerKey("concerninghobbits");
    public static final ResourceKey<JukeboxSong> MINASTIRITH = registerKey("minastirith");
    public static final ResourceKey<JukeboxSong> MURDERFROG = registerKey("murderfrog");
    public static final ResourceKey<JukeboxSong> BOMBADIL = registerKey("mysteryoftombombadil");
    public static final ResourceKey<JukeboxSong> REDER = registerKey("rederssong");
    public static final ResourceKey<JukeboxSong> RIVENDELL = registerKey("ridersofrivendell");
    public static final ResourceKey<JukeboxSong> LOTHLORIEN = registerKey("thelightoflothlorien");
    public static final ResourceKey<JukeboxSong> SHADE = registerKey("touchofshade");
    public static final ResourceKey<JukeboxSong> FUMBLE = registerKey("trollfumble");
    public static final ResourceKey<JukeboxSong> EDORAS = registerKey("wakeofedoras");
    public static final ResourceKey<JukeboxSong> WITCHBATTLE = registerKey("witchbattle");

    private static ResourceKey<JukeboxSong> registerKey(String name) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, TolkienMobsMain.prefix(name));
    }

    public static void bootstrap(BootstrapContext<JukeboxSong> context) {
        register(context, EREBOR, TolkienSounds.MUSIC_DISC_ALLTHATGLITTERS, 179, 15);
        register(context, HOBBITS, TolkienSounds.MUSIC_DISC_CONCERNINGHOBBITS, 154, 15);
        register(context, MINASTIRITH, TolkienSounds.MUSIC_DISC_MINASTIRITH, 122, 15);
        register(context, MURDERFROG, TolkienSounds.MUSIC_DISC_MURDERFROG, 76, 15);
        register(context, BOMBADIL, TolkienSounds.MUSIC_DISC_TOMBOMBADIL, 100, 15);
        register(context, REDER, TolkienSounds.MUSIC_DISC_REDERSSONG, 47, 15);
        register(context, RIVENDELL, TolkienSounds.MUSIC_DISC_RIDERSOFRIVENDELL, 142, 15);
        register(context, LOTHLORIEN, TolkienSounds.MUSIC_DISC_LIGHTOFLOTHLORIEN, 255, 15);
        register(context, SHADE, TolkienSounds.MUSIC_DISC_TOUCHOFSHADE, 60, 15);
        register(context, FUMBLE, TolkienSounds.MUSIC_DISC_TROLLFUMBLE, 65, 15);
        register(context, EDORAS, TolkienSounds.MUSIC_DISC_WAKEOFEDORAS, 109, 15);
        register(context, WITCHBATTLE, TolkienSounds.MUSIC_DISC_WITCHBATTLE, 119, 15);
    }

    private static void register(BootstrapContext<JukeboxSong> context, ResourceKey<JukeboxSong> key, Holder<SoundEvent> sound, float length, int output) {
        context.register(key, new JukeboxSong(sound, Component.translatable(Util.makeDescriptionId("jukebox_song", key.location())), length, output));
    }

}
