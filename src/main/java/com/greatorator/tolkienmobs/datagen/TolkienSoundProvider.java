package com.greatorator.tolkienmobs.datagen;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import net.minecraft.data.PackOutput;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

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

        // Entity
            // Villager
        this.generateNewSoundWithSubtitle(TolkienSounds.VILLAGER_COIN_TRADER, "mob/villager/coin_trader", 1, "A wild Coin Trader Appears!");

            // NPC
        this.generateNewSoundWithSubtitle(TolkienSounds.IDLE_DWARF, "mob/dwarf/dwarf_say", 4, "Dwarf speaking");
        this.generateNewSoundWithSubtitle(TolkienSounds.HURT_DWARF, "mob/dwarf/dwarf_hurt", 1, "Dwarf being hurt");
        this.generateNewSoundWithSubtitle(TolkienSounds.ANGRY_DWARF, "mob/dwarf/dwarf_angry", 1, "Dwarf getting angry");
        this.generateNewSoundWithSubtitle(TolkienSounds.DEATH_DWARF, "mob/dwarf/dwarf_death", 1, "Dwarf dying");

            //Ambient
        this.generateNewSound(TolkienSounds.IDLE_RAT, "mob/entityttmrat/entityttmrat_say", 4, "Rat squeaking");
        this.generateNewSound(TolkienSounds.HURT_RAT, "mob/entityttmrat/entityttmrat_hurt", 1, "Rat hurting");
        this.generateNewSound(TolkienSounds.DEATH_RAT, "mob/entityttmrat/entityttmrat_death", 1, "Rat dying");
        this.generateNewSound(TolkienSounds.IDLE_SQUIRREL, "mob/sosquirrel/sosquirrel_say", 4, "Squirrel making noise ");
        this.generateNewSound(TolkienSounds.HURT_SQUIRREL, "mob/sosquirrel/sosquirrel_hurt", 1, "Squirrel hurting");
        this.generateNewSound(TolkienSounds.DEATH_SQUIRREL, "mob/sosquirrel/sosquirrel_death", 1, "Squirrel dying");
        this.generateNewSound(TolkienSounds.STEP_SQUIRREL, "mob/sosquirrel/sosquirrel_step", 1, "Squirrel Moving");
        this.generateNewSound(TolkienSounds.ANGRY_SQUIRREL, "mob/sosquirrel/sosquirrel_angry", 1, "Squirrel angry");
        this.generateNewSound(TolkienSounds.IDLE_FROG, "mob/toaddle/toaddle_say", 4, "Frog croaking");
        this.generateNewSound(TolkienSounds.HURT_FROG, "mob/toaddle/toaddle_hurt", 1, "Frog being hurt");
        this.generateNewSound(TolkienSounds.DEATH_FROG, "mob/toaddle/toaddle_death", 1, "Frog dying");
        this.generateNewSound(TolkienSounds.STEP_FROG, "mob/toaddle/toaddle_step", 1, "Frog moving");
        this.generateNewSound(TolkienSounds.ANGRY_FROG, "mob/toaddle/toaddle_angry", 1, "Frog angry");
        this.generateNewSound(TolkienSounds.IDLE_THRUSH, "mob/tmthrush/tmthrush_say", 4, "Thrush chirping");
        this.generateNewSound(TolkienSounds.HURT_THRUSH, "mob/tmthrush/tmthrush_hurt", 1, "Thrush Hurt");
        this.generateNewSound(TolkienSounds.DEATH_THRUSH, "mob/tmthrush/tmthrush_death", 1, "Thrush Dying");
        this.generateNewSound(TolkienSounds.FLAP_THRUSH, "mob/tmgreateagle/tmgreateagle_flapping", 1, "Bird wings flapping");
        this.generateNewSound(TolkienSounds.IDLE_CREBAIN, "mob/crebain/crebain_cry", 4, "Crebain Cawing");
        this.generateNewSound(TolkienSounds.HURT_CREBAIN, "mob/crebain/crebain_hurt", 1, "Crebain Hurt");
        this.generateNewSound(TolkienSounds.DEATH_CREBAIN, "mob/crebain/crebain_death", 1, "Crebain Dying");
        this.generateNewSound(TolkienSounds.IDLE_SWARM, "mob/midgefly_say1", 1, "Buzzing Flies");
        this.generateNewSound(TolkienSounds.IDLE_GREAT_EAGLE, "mob/tmgreateagle/tmgreateagle_flapping", 1, "Bird wings flapping");
        this.generateNewSound(TolkienSounds.HURT_GREAT_EAGLE, "mob/tmgreateagle/tmgreateagle_hurt", 1, "Crebain Hurt");
        this.generateNewSound(TolkienSounds.DEATH_GREAT_EAGLE, "mob/tmgreateagle/tmgreateagle_death", 1, "Crebain Dying");
        this.generateNewSound(TolkienSounds.ATTACK_GREAT_EAGLE, "mob/tmgreateagle/tmgreateagle_attack", 1, "Buzzing Flies");

            //Passive
        this.generateNewSound(TolkienSounds.IDLE_MUMAKIL, "mob/mumakil/mumakil_say", 4, "Mumakil Trumpets");
        this.generateNewSound(TolkienSounds.HURT_MUMAKIL, "mob/mumakil/mumakil_hurt", 1, "Mumakil has been hurt");
        this.generateNewSound(TolkienSounds.IDLE_GOAT, "mob/goat/goat_say", 4, "Bleating Battle Goat");
        this.generateNewSound(TolkienSounds.HURT_GOAT, "mob/goat/goat_hurt", 1, "Hurt Battle Goat");
        this.generateNewSound(TolkienSounds.DEATH_GOAT, "mob/goat/goat_death", 1, "Dying Battle Goat");
        this.generateNewSound(TolkienSounds.SCREAM_GOAT, "mob/goat/goat_scream", 1, "Yelling Battle Goat");
        this.generateNewSound(TolkienSounds.ANGRY_GOAT, "mob/goat/goat_angry", 1, "Angry Battle Goat");

            //Monster
        this.generateNewSound(TolkienSounds.IDLE_OATH_BREAKER, "mob/oathbreaker/oathbreaker_say", 4, "Eerie Groan");
        this.generateNewSound(TolkienSounds.IDLE_BARROW, "mob/barrowwight/barrowwight_say", 3, "Eerie Groan");
        this.generateNewSound(TolkienSounds.HURT_BARROW, "mob/barrowwight/barrowwight_hurt", 1, "Damaged Spirit");
        this.generateNewSound(TolkienSounds.IDLE_ROMIE_WALKER, "mob/romiewalker/romiewalker_idle", 2, "Eerie Groan");
        this.generateNewSound(TolkienSounds.HURT_ROMIE_WALKER, "mob/romiewalker/romiewalker_hurt", 2, "Damaged Spirit");
        this.generateNewSound(TolkienSounds.DEATH_ROMIE_WALKER, "mob/romiewalker/romiewalker_death", 2, "Damaged Spirit");
        this.generateNewSound(TolkienSounds.IDLE_ORC, "mob/orc/orc_say", 4, "Casual Orc");
        this.generateNewSound(TolkienSounds.STEP_MIMIC, "mob/mimicchest/mimic_step", 2, "A chest started moving");
        this.generateNewSound(TolkienSounds.ANGRY_MIMIC, "mob/mimicchest/mimic_angry", 1, "Chest is now Angry");
        this.generateNewSound(TolkienSounds.IDLE_GOBLIN, "mob/goblin/goblin_say", 4, "Eerie Groan");
        this.generateNewSound(TolkienSounds.HURT_GOBLIN, "mob/goblin/goblin_hurt", 1, "Eerie Groan");
        this.generateNewSound(TolkienSounds.DEATH_GOBLIN, "mob/goblin/goblin_death", 1, "Damaged Spirit");
        this.generateNewSound(TolkienSounds.ANGRY_GOBLIN, "mob/goblin/goblin_angry", 1, "Eerie Groan");
        this.generateNewSound(TolkienSounds.SHOOT_SPIDER, "mob/mirkwoodspider/shoot", 2, "Web shooting");
        this.generateNewSound(TolkienSounds.IDLE_MINOTAUR, "mob/minotaur/minotaur_say", 4, "Minotaur of the maze");
        this.generateNewSound(TolkienSounds.STEP_MINOTAUR, "mob/minotaur/minotaur_step", 1, "The Minotaur approaches");
        this.generateNewSound(TolkienSounds.HURT_MINOTAUR, "mob/minotaur/minotaur_hurt", 1, "Screams in pain");
        this.generateNewSound(TolkienSounds.DEATH_MINOTAUR, "mob/minotaur/minotaur_death", 2, "Death of the beast");
        this.generateNewSound(TolkienSounds.IDLE_TROLL, "mob/troll/troll_say", 3, "Cave Troll!");
        this.generateNewSound(TolkienSounds.STEP_TROLL, "mob/troll/troll_step", 4, "The Troll approaches");
        this.generateNewSound(TolkienSounds.HURT_TROLL, "mob/troll/troll_hurt", 1, "Screams in pain");
        this.generateNewSound(TolkienSounds.DEATH_TROLL, "mob/troll/troll_death", 1, "Death of the beast");
        this.generateNewSound(TolkienSounds.IDLE_TREEENT, "mob/treeent/treeent_say", 3, "It takes a long time to say anything in Entish");
        this.generateNewSound(TolkienSounds.STEP_TREEENT, "mob/treeent/treeent_step", 2, "Is that tree moving?");
        this.generateNewSound(TolkienSounds.HURT_TREEENT, "mob/treeent/treeent_hurt", 3, "Weapon against wood");
        this.generateNewSound(TolkienSounds.DEATH_TREEENT, "mob/treeent/treeent_death", 1, "An ent falls");
        this.generateNewSound(TolkienSounds.IDLE_WARG, "mob/warg/warg_say", 2, "Wolf-like growls");
        this.generateNewSound(TolkienSounds.HURT_WARG, "mob/warg/warg_hurt", 1, "Warg whimpers");
        this.generateNewSound(TolkienSounds.DEATH_WARG, "mob/warg/warg_death", 1, "End to the foul beast");
        this.generateNewSound(TolkienSounds.IDLE_NAZGUL, "mob/witchking/witchking_say", 4, "The Nazgul Screams");
        this.generateNewSound(TolkienSounds.STEP_NAZGUL, "mob/witchking/witchking_step", 1, "The Nazgul approaches");
        this.generateNewSound(TolkienSounds.HURT_NAZGUL, "mob/witchking/witchking_hurt", 1, "Screams in pain");
        this.generateNewSound(TolkienSounds.DEATH_NAZGUL, "mob/witchking/witchking_death", 1, "Death of Evil...or is it?");
        this.generateNewSound(TolkienSounds.IDLE_GOLLUM, "mob/gollum/gollum_say", 4, "Something lurks nearby");
        this.generateNewSound(TolkienSounds.HURT_GOLLUM, "mob/gollum/gollum_hurt", 1, "Screams in pain");
        this.generateNewSound(TolkienSounds.DEATH_GOLLUM, "mob/gollum/gollum_death", 1, "Gollum death.");

            //Boss
        this.generateNewSound(TolkienSounds.IDLE_BALROG, "mob/balrog/balrog_say", 3, "The Balrog Roars");
        this.generateNewSound(TolkienSounds.STEP_BALROG, "mob/balrog/balrog_step", 1, "Then something came into the chamber...");
        this.generateNewSound(TolkienSounds.HURT_BALROG, "mob/balrog/balrog_hurt", 1, "Screams in pain");
        this.generateNewSound(TolkienSounds.DEATH_BALROG, "mob/balrog/balrog_death", 1, "Death of the scourge of fire cloaked in shadow");
        this.generateNewSound(TolkienSounds.IDLE_WITCHKING, "mob/witchking/witchking_say", 4, "The Witch King Screams");
        this.generateNewSound(TolkienSounds.STEP_WITCHKING, "mob/witchking/witchking_step", 1, "The Witch King approaches");
        this.generateNewSound(TolkienSounds.HURT_WITCHKING, "mob/witchking/witchking_hurt", 1, "Screams in pain");
        this.generateNewSound(TolkienSounds.DEATH_WITCHKING, "mob/witchking/witchking_death", 1, "Death of Evil...or is it?");
        this.generateNewSound(TolkienSounds.ANGRY_WITCHKING, "mob/witchking/witchking_angry", 1, "You have drawn the attention of the Witch King");
        this.generateNewSound(TolkienSounds.IDLE_WATCHER, "mob/watcher/watcher_say", 4, "Beware the creatures of the deep");
        this.generateNewSound(TolkienSounds.DEATH_WATCHER, "mob/watcher/watcher_death", 1, "The end of things deeper and darker.");
        this.generateNewSound(TolkienSounds.IDLE_FELLBEAST, "mob/fellbeast/fellbeast_say", 2, "A mighty Dragon roar");
        this.generateNewSound(TolkienSounds.FLAP_FELLBEAST, "mob/fellbeast/fellbeast_flap", 1, "The beast takes flight.");

        // Projectile
        this.generateNewSound(TolkienSounds.SHOOT_BOULDER, "mob/ammo/ammo_boulder_shoot", 1, " ");
        this.generateNewSound(TolkienSounds.SHOOT_WEB, "mob/ammo/web_impact", 2, " ");
        this.generateNewSound(TolkienSounds.SHOOT_TORNADO, "mob/ammo/lift_wind", 3, " ");

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
        TolkienLangProviderUS.SUBTITLE_GENERATOR.put(subtitleKey, subtitle);
    }

    @Nonnull
    @Override
    public String getName() {
        return "Tolkienmobs - Music of Ainor";
    }
}
