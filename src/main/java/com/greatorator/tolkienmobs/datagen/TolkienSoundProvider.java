package com.greatorator.tolkienmobs.datagen;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienSoundProvider extends SoundDefinitionsProvider {
    protected TolkienSoundProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, MODID, helper);
    }

    @Override
    public void registerSounds() {
            // Items
        this.generateNewSoundWithSubtitle(TolkienSounds.HYPE_HORN, "item/hype_horn", 1, "BOO YAH!");

            // Blocks
        this.generateNewSoundWithSubtitle(TolkienSounds.CATHEDRALBELL, "block/cathedralbell", 1, "Ringing church bell");
        this.generateNewSoundWithSubtitle(TolkienSounds.CHEST_CLOSE, "block/chest_close", 1, "Closing chest");
        this.generateNewSoundWithSubtitle(TolkienSounds.CHEST_OPEN, "block/chest_open", 1, "Opening chest");
        this.generateNewSoundWithSubtitle(TolkienSounds.LIGHTNINGBUG_AMBIENT, "block/lightningbug", 1, "Lightningbug Buzzing");
        this.generateNewSoundWithSubtitle(TolkienSounds.LOCUST_AMBIENT, "block/locust", 1, "Locust Buzzing");

            // Ambient
        this.generateNewSoundWithSubtitle(TolkienSounds.ARDA_PORTAL, "ambient/arda_portal", 1, "Faint hum of the Portal to Arda");
        this.generateNewSoundWithSubtitle(TolkienSounds.KHAZAD_DUM, "ambient/khazaddum", 1, "Ominous sounds of the realm of Dwarves");
        this.generateNewSoundWithSubtitle(TolkienSounds.MEDIEVAL_CITY, "ambient/medievalcity", 1, "Sounds of the busy city");
        this.generateNewSoundWithSubtitle(TolkienSounds.MEDIEVAL_MARKET, "ambient/medievalcitymarket", 1, "A Bustling city market");
        this.generateNewSoundWithSubtitle(TolkienSounds.MEDIEVAL_SEAPORT, "ambient/medievalseaport", 1, "The great open seaport");
        this.generateNewSoundWithSubtitle(TolkienSounds.MEDIEVAL_TAVERN, "ambient/medievaltavern", 1, "Chattering locals in a tavern");
        this.generateNewSoundWithSubtitle(TolkienSounds.PATH_OF_UNDEAD, "ambient/pathsofthedead", 1, "The way is shut, and the dead keep it");
        this.generateNewSoundWithSubtitle(TolkienSounds.UNDERWORLD, "ambient/underworld", 1, "Dark and dreary underworld");
        this.generateNewSoundWithSubtitle(TolkienSounds.WATERWORKS, "ambient/waterworks", 1, "Lost power room of the dwarves");

            // Records
        this.makeMusicDisc(TolkienSounds.MUSIC_DISC_ALLTHATGLITTERS, "allthatglitterserebor");
        this.makeMusicDisc(TolkienSounds.MUSIC_DISC_CONCERNINGHOBBITS, "concerninghobbits");
        this.makeMusicDisc(TolkienSounds.MUSIC_DISC_MINASTIRITH, "minastirith");
        this.makeMusicDisc(TolkienSounds.MUSIC_DISC_MURDERFROG, "murderfrog");
        this.makeMusicDisc(TolkienSounds.MUSIC_DISC_TOMBOMBADIL, "mysteryoftombombadil");
        this.makeMusicDisc(TolkienSounds.MUSIC_DISC_REDERSSONG, "rederssong");
        this.makeMusicDisc(TolkienSounds.MUSIC_DISC_RIDERSOFRIVENDELL, "ridersofrivendell");
        this.makeMusicDisc(TolkienSounds.MUSIC_DISC_LIGHTOFLOTHLORIEN, "thelightoflothlorien");
        this.makeMusicDisc(TolkienSounds.MUSIC_DISC_TOUCHOFSHADE, "touchofshade");
        this.makeMusicDisc(TolkienSounds.MUSIC_DISC_TROLLFUMBLE, "trollfumble");
        this.makeMusicDisc(TolkienSounds.MUSIC_DISC_WAKEOFEDORAS, "wakeofedoras");
        this.makeMusicDisc(TolkienSounds.MUSIC_DISC_WITCHBATTLE, "witchbattle");


        this.add(TolkienSounds.MUSIC, SoundDefinition.definition().with(
                SoundDefinition.Sound.sound(TolkienMobsMain.prefix("music/allthatglitterserebor"), SoundDefinition.SoundType.SOUND).stream().volume(0.5F),
                SoundDefinition.Sound.sound(TolkienMobsMain.prefix("music/concerninghobbits"), SoundDefinition.SoundType.SOUND).stream().volume(0.5F),
                SoundDefinition.Sound.sound(TolkienMobsMain.prefix("music/minastirith"), SoundDefinition.SoundType.SOUND).stream().volume(0.5F),
                SoundDefinition.Sound.sound(TolkienMobsMain.prefix("music/murderfrog"), SoundDefinition.SoundType.SOUND).stream().volume(0.5F),
                SoundDefinition.Sound.sound(TolkienMobsMain.prefix("music/mysteryoftombombadil"), SoundDefinition.SoundType.SOUND).stream().volume(0.5F),
                SoundDefinition.Sound.sound(TolkienMobsMain.prefix("music/rederssong"), SoundDefinition.SoundType.SOUND).stream().volume(0.5F),
                SoundDefinition.Sound.sound(TolkienMobsMain.prefix("music/ridersofrivendell"), SoundDefinition.SoundType.SOUND).stream().volume(0.5F),
                SoundDefinition.Sound.sound(TolkienMobsMain.prefix("music/thelightoflothlorien"), SoundDefinition.SoundType.SOUND).stream().volume(0.5F),
                SoundDefinition.Sound.sound(TolkienMobsMain.prefix("music/touchofshade"), SoundDefinition.SoundType.SOUND).stream().volume(0.5F),
                SoundDefinition.Sound.sound(TolkienMobsMain.prefix("music/trollfumble"), SoundDefinition.SoundType.SOUND).stream().volume(0.5F),
                SoundDefinition.Sound.sound(TolkienMobsMain.prefix("music/wakeofedoras"), SoundDefinition.SoundType.SOUND).stream().volume(0.5F),
                SoundDefinition.Sound.sound(TolkienMobsMain.prefix("music/witchbattle"), SoundDefinition.SoundType.SOUND).stream().volume(0.5F)));

    }

    public void makeMusicDisc(DeferredHolder<SoundEvent, SoundEvent> event, String discName) {
        this.add(event, SoundDefinition.definition()
                .with(SoundDefinition.Sound.sound(TolkienMobsMain.prefix("music/" + discName), SoundDefinition.SoundType.SOUND)
                        .stream()));
    }

    public void generateNewSoundWithSubtitle(DeferredHolder<SoundEvent, SoundEvent> event, String baseSoundDirectory, int numberOfSounds, String subtitle) {
        generateNewSound(event, baseSoundDirectory, numberOfSounds, subtitle);
    }

    public void generateNewSound(DeferredHolder<SoundEvent, SoundEvent> event, String baseSoundDirectory, int numberOfSounds, @Nullable String subtitle) {
        SoundDefinition definition = SoundDefinition.definition();
        if (subtitle != null) {
            this.createSubtitleAndLangEntry(event, definition, subtitle);
        }
        for (int i = 1; i <= numberOfSounds; i++) {
            definition.with(SoundDefinition.Sound.sound(TolkienMobsMain.prefix(baseSoundDirectory + (numberOfSounds > 1 ? i : "")), SoundDefinition.SoundType.SOUND));
        }
        this.add(event, definition);
    }

    private void createSubtitleAndLangEntry(DeferredHolder<SoundEvent, SoundEvent> event, SoundDefinition definition, String subtitle) {
        String[] splitSoundName = event.getId().getPath().split("\\.", 3);
        String subtitleKey = "subtitles.tolkienmobs." + splitSoundName[0] + "." + splitSoundName[2];
        definition.subtitle(subtitleKey);
        TolkienLangProvider.SUBTITLE_GENERATOR.put(subtitleKey, subtitle);
    }
}
