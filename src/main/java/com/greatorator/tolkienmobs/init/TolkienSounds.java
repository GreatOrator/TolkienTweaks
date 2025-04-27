package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

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
        // Villager
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGER_COIN_TRADER = createEvent("mob.tolkienmobs.coin_trader");

        // NPC
    public static final DeferredHolder<SoundEvent, SoundEvent> IDLE_DWARF = createEvent("mob.dwarf.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> ANGRY_DWARF = createEvent("mob.dwarf.angry");
    public static final DeferredHolder<SoundEvent, SoundEvent> HURT_DWARF = createEvent("mob.dwarf.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEATH_DWARF = createEvent("mob.dwarf.death");

    // Ambient
            // Rat
    public static final DeferredHolder<SoundEvent, SoundEvent> IDLE_RAT = createEvent("mob.entityttmrat.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> HURT_RAT = createEvent("mob.entityttmrat.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEATH_RAT = createEvent("mob.entityttmrat.death");

            // Squirrel
    public static final DeferredHolder<SoundEvent, SoundEvent> IDLE_SQUIRREL = createEvent("mob.entityttmsquirrel.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> HURT_SQUIRREL = createEvent("mob.entityttmsquirrel.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEATH_SQUIRREL = createEvent("mob.entityttmsquirrel.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> STEP_SQUIRREL = createEvent("mob.entityttmsquirrel.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> ANGRY_SQUIRREL = createEvent("mob.entityttmsquirrel.angry");

            // Frog
    public static final DeferredHolder<SoundEvent, SoundEvent> IDLE_FROG = createEvent("mob.entityttmfrog.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> HURT_FROG = createEvent("mob.entityttmfrog.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEATH_FROG = createEvent("mob.entityttmfrog.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> STEP_FROG = createEvent("mob.entityttmfrog.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> ANGRY_FROG = createEvent("mob.entityttmfrog.angry");

            // Thrush
    public static final DeferredHolder<SoundEvent, SoundEvent> IDLE_THRUSH = createEvent("mob.entityttmthrush.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> HURT_THRUSH = createEvent("mob.entityttmthrush.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEATH_THRUSH = createEvent("mob.entityttmthrush.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> FLAP_THRUSH = createEvent("mob.tmgreateagle.flapping");

            // Great Eagle
    public static final DeferredHolder<SoundEvent, SoundEvent> IDLE_GREAT_EAGLE = createEvent("mob.tmgreateagle.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> HURT_GREAT_EAGLE = createEvent("mob.tmgreateagle.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEATH_GREAT_EAGLE = createEvent("mob.tmgreateagle.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> ATTACK_GREAT_EAGLE = createEvent("mob.tmgreateagle.attack");

            // Thrush
    public static final DeferredHolder<SoundEvent, SoundEvent> IDLE_CREBAIN = createEvent("mob.entityttmcrebain.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> HURT_CREBAIN = createEvent("mob.entityttmcrebain.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEATH_CREBAIN = createEvent("mob.entityttmcrebain.death");

            // Midgefly
    public static final DeferredHolder<SoundEvent, SoundEvent> IDLE_SWARM = createEvent("mob.midgefly.idle");

            // Mumakil
    public static final DeferredHolder<SoundEvent, SoundEvent> IDLE_MUMAKIL = createEvent("mob.entityttmmumakil.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> HURT_MUMAKIL = createEvent("mob.entityttmmumakil.hurt");

            // Goat
    public static final DeferredHolder<SoundEvent, SoundEvent> IDLE_GOAT = createEvent("mob.entityttmgoat.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> HURT_GOAT = createEvent("mob.entityttmgoat.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEATH_GOAT = createEvent("mob.entityttmgoat.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> SCREAM_GOAT = createEvent("mob.entityttmgoat.scream");
    public static final DeferredHolder<SoundEvent, SoundEvent> ANGRY_GOAT = createEvent("mob.entityttmgoat.angry");


    // Monster
            // Barrow Wight
    public static final DeferredHolder<SoundEvent, SoundEvent> IDLE_BARROW = createEvent("mob.barrow.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> HURT_BARROW = createEvent("mob.barrow.hurt");

            // Oath Breaker
    public static final DeferredHolder<SoundEvent, SoundEvent> IDLE_OATH_BREAKER = createEvent("mob.oathbreaker.idle");

            // Mirkwood Spider
    public static final DeferredHolder<SoundEvent, SoundEvent> SHOOT_SPIDER = createEvent("mob.mirkwoodspider.shoot");

            // Romie Walker
    public static final DeferredHolder<SoundEvent, SoundEvent> IDLE_ROMIE_WALKER = createEvent("mob.romiewalker.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> HURT_ROMIE_WALKER = createEvent("mob.romiewalker.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEATH_ROMIE_WALKER = createEvent("mob.romiewalker.death");

            // Goblin
    public static final DeferredHolder<SoundEvent, SoundEvent> IDLE_GOBLIN = createEvent("mob.goblin.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> HURT_GOBLIN = createEvent("mob.goblin.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEATH_GOBLIN = createEvent("mob.goblin.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> ANGRY_GOBLIN = createEvent("mob.goblin.angry");

            // Barrow Wight
    public static final DeferredHolder<SoundEvent, SoundEvent> STEP_MIMIC = createEvent("mob.mimic.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> ANGRY_MIMIC = createEvent("mob.mimic.angry");

            // Orc
    public static final DeferredHolder<SoundEvent, SoundEvent> IDLE_ORC = createEvent("mob.orc.idle");

            // Minotaur
    public static final DeferredHolder<SoundEvent, SoundEvent> IDLE_MINOTAUR = createEvent("mob.minotaur.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> STEP_MINOTAUR = createEvent("mob.minotaur.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> HURT_MINOTAUR = createEvent("mob.minotaur.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEATH_MINOTAUR = createEvent("mob.minotaur.death");

            // Nazgul
    public static final DeferredHolder<SoundEvent, SoundEvent> IDLE_NAZGUL = createEvent("mob.nazgul.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> STEP_NAZGUL = createEvent("mob.nazgul.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> HURT_NAZGUL = createEvent("mob.nazgul.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEATH_NAZGUL = createEvent("mob.nazgul.death");

            // Nazgul
    public static final DeferredHolder<SoundEvent, SoundEvent> IDLE_GOLLUM = createEvent("mob.gollum.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> HURT_GOLLUM = createEvent("mob.gollum.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEATH_GOLLUM = createEvent("mob.gollum.death");

            // Tree Ent
    public static final DeferredHolder<SoundEvent, SoundEvent> IDLE_TREEENT = createEvent("mob.treeent.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> STEP_TREEENT = createEvent("mob.treeent.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> HURT_TREEENT = createEvent("mob.treeent.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEATH_TREEENT = createEvent("mob.treeent.death");

            // Warg
    public static final DeferredHolder<SoundEvent, SoundEvent> IDLE_WARG = createEvent("mob.warg.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> HURT_WARG = createEvent("mob.warg.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEATH_WARG = createEvent("mob.warg.death");

            // Troll
    public static final DeferredHolder<SoundEvent, SoundEvent> IDLE_TROLL = createEvent("mob.troll.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> STEP_TROLL = createEvent("mob.troll.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> HURT_TROLL = createEvent("mob.troll.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEATH_TROLL = createEvent("mob.troll.death");

            // Projectile
    public static final DeferredHolder<SoundEvent, SoundEvent> SHOOT_BOULDER = createEvent("entity.boulder.shoot");
    public static final DeferredHolder<SoundEvent, SoundEvent> SHOOT_WEB = createEvent("entity.web.shoot");
    public static final DeferredHolder<SoundEvent, SoundEvent> SHOOT_TORNADO = createEvent("entity.tornado.shoot");

    // Boss
        // Balrog
    public static final DeferredHolder<SoundEvent, SoundEvent> IDLE_BALROG = createEvent("mob.balrog.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> STEP_BALROG = createEvent("mob.balrog.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> HURT_BALROG = createEvent("mob.balrog.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEATH_BALROG = createEvent("mob.balrog.death");

        // Witch King
    public static final DeferredHolder<SoundEvent, SoundEvent> IDLE_WITCHKING = createEvent("mob.witchking.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> STEP_WITCHKING = createEvent("mob.witchking.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> HURT_WITCHKING = createEvent("mob.witchking.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEATH_WITCHKING = createEvent("mob.witchking.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> ANGRY_WITCHKING = createEvent("mob.witchking.angry");

        // Watcher
    public static final DeferredHolder<SoundEvent, SoundEvent> IDLE_WATCHER = createEvent("mob.watcher.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEATH_WATCHER = createEvent("mob.watcher.death");

        // Fell Beast
    public static final DeferredHolder<SoundEvent, SoundEvent> IDLE_FELLBEAST = createEvent("mob.fellbeast.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> FLAP_FELLBEAST = createEvent("mob.fellbeast.flap");



    private static DeferredHolder<SoundEvent, SoundEvent> createEvent(String sound) {
        return SOUND_EVENTS.register(sound, () -> SoundEvent.createVariableRangeEvent(TolkienMobsMain.prefix(sound)));
    }
}