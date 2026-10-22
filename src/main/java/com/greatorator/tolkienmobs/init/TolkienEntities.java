package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.entity.ambient.*;
import com.greatorator.tolkienmobs.entity.boss.*;
import com.greatorator.tolkienmobs.entity.item.*;
import com.greatorator.tolkienmobs.entity.monster.*;
import com.greatorator.tolkienmobs.entity.npc.*;
import com.greatorator.tolkienmobs.entity.passive.*;
import com.greatorator.tolkienmobs.entity.projectiles.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MODID);
    public static final DeferredRegister.Items SPAWN_EGGS = DeferredRegister.createItems(MODID);

    /** Colours of each spawn egg; the 1.21.4 item model definition carries them as tints. */
    public static final Map<DeferredItem<Item>, SpawnEggColors> SPAWN_EGG_COLORS = new LinkedHashMap<>();

        /** Entities */
        // Ambient
    public static final Supplier<EntityType<GeckoEntity>> ENTITY_TTM_GECKO =
            registerEntity("entityttmgecko", () -> EntityType.Builder.of(GeckoEntity::new, MobCategory.AMBIENT)
                    .sized(0.75f, 0.35f));
    public static final Supplier<EntityType<RatEntity>> ENTITY_TTM_RAT =
            registerEntity("entityttmrat", () -> EntityType.Builder.of(RatEntity::new, MobCategory.AMBIENT)
                    .sized(0.75f, 0.35f));
    public static final Supplier<EntityType<SquirrelEntity>> ENTITY_TTM_SQUIRREL =
            registerEntity("entityttmsquirrel", () -> EntityType.Builder.of(SquirrelEntity::new, MobCategory.AMBIENT)
                    .sized(0.75f, 0.35f));
    public static final Supplier<EntityType<FrogEntity>> ENTITY_TTM_FROG =
            registerEntity("entityttmfrog", () -> EntityType.Builder.of(FrogEntity::new, MobCategory.AMBIENT)
                    .sized(0.75f, 0.35f));
    public static final Supplier<EntityType<ThrushEntity>> ENTITY_TTM_THRUSH =
            registerEntity("entityttmthrush", () -> EntityType.Builder.of(ThrushEntity::new, MobCategory.AMBIENT)
                    .sized(0.75f, 0.35f));
    public static final Supplier<EntityType<CrebainEntity>> ENTITY_TTM_CREBAIN =
            registerEntity("entityttmcrebain", () -> EntityType.Builder.of(CrebainEntity::new, MobCategory.AMBIENT)
                    .sized(0.75f, 0.35f));
    public static final Supplier<EntityType<SwarmEntity>> ENTITY_TTM_SWARM =
            registerEntity("entityttmswarm", () -> EntityType.Builder.of(SwarmEntity::new, MobCategory.MONSTER)
                    .sized(0.75f, 0.35f));
    public static final Supplier<EntityType<GreatEagleEntity>> ENTITY_TTM_GREAT_EAGLE =
            registerEntity("entityttmgreateagle", () -> EntityType.Builder.of(GreatEagleEntity::new, MobCategory.CREATURE)
                    .sized(1.225F, 1.6625F));

        // Passive
    public static final Supplier<EntityType<AurochEntity>> ENTITY_TTM_AUROCH =
            registerEntity("entityttmauroch", () -> EntityType.Builder.of(AurochEntity::new, MobCategory.CREATURE)
                    .sized(1.125F, 1.3125F));
    public static final Supplier<EntityType<MumakilEntity>> ENTITY_TTM_MUMAKIL =
            registerEntity("entityttmmumakil", () -> EntityType.Builder.of(MumakilEntity::new, MobCategory.CREATURE)
                    .sized(2.5F, 3.3125F));
    public static final Supplier<EntityType<GoatEntity>> ENTITY_TTM_GOAT =
            registerEntity("entityttmgoat", () -> EntityType.Builder.of(GoatEntity::new, MobCategory.CREATURE)
                    .sized(0.875F, 1.875F));
    public static final Supplier<EntityType<NazgulSteedEntity>> ENTITY_TTM_NAZGULSTEED =
            registerEntity("entityttmnazgulsteed", () -> EntityType.Builder.of(NazgulSteedEntity::new, MobCategory.CREATURE)
                    .sized(1.0F, 1.5F));
    public static final Supplier<EntityType<ShadowfaxEntity>> ENTITY_TTM_SHADOWFAX =
            registerEntity("entityttmshadowfax", () -> EntityType.Builder.of(ShadowfaxEntity::new, MobCategory.CREATURE)
                    .sized(1.0F, 1.5F));
    public static final Supplier<EntityType<GollumEntity>> ENTITY_TTM_GOLLUM =
            registerEntity("entityttmgollum", () -> EntityType.Builder.of(GollumEntity::new, MobCategory.CREATURE)
                    .sized(0.625F, 1.5625F));
    public static final Supplier<EntityType<IstariEntity>> ENTITY_TTM_ISTARI =
            registerEntity("entityttmistari", () -> EntityType.Builder.of(IstariEntity::new, MobCategory.CREATURE)
                    .sized(0.75F, 2.0F));

        // NPC
    public static final Supplier<EntityType<HumanEntity>> ENTITY_TTM_HUMAN =
            registerEntity("entityttmhuman", () -> EntityType.Builder.of(HumanEntity::new, MobCategory.MISC)
                    .sized(0.5F, 2.0F));
    public static final Supplier<EntityType<DwarfEntity>> ENTITY_TTM_DWARF =
            registerEntity("entityttmdwarf", () -> EntityType.Builder.of(DwarfEntity::new, MobCategory.MISC)
                    .sized(0.50625F, 1.51875F));
    public static final Supplier<EntityType<ElvesEntity>> ENTITY_TTM_ELVES =
            registerEntity("entityttmelves", () -> EntityType.Builder.of(ElvesEntity::new, MobCategory.MISC)
                    .sized(0.55F, 2.2F));
    public static final Supplier<EntityType<HobbitEntity>> ENTITY_TTM_HOBBIT =
            registerEntity("entityttmhobbit", () -> EntityType.Builder.of(HobbitEntity::new, MobCategory.MISC)
                    .sized(0.35F, 1.4F));
    public static final Supplier<EntityType<SouthronTraderEntity>> ENTITY_TTM_SOUTHRON =
            registerEntity("entityttmsouthron", () -> EntityType.Builder.of(SouthronTraderEntity::new, MobCategory.MISC)
                    .sized(0.5F, 2.0F));
    public static final Supplier<EntityType<OrcTraderEntity>> ENTITY_TTM_ORC_TRADER =
            registerEntity("entityttmorc_trader", () -> EntityType.Builder.of(OrcTraderEntity::new, MobCategory.MISC)
                    .sized(0.5F, 2.0F));
    public static final Supplier<EntityType<OrcEntity>> ENTITY_TTM_ORC =
            registerEntity("entityttmorc", () -> EntityType.Builder.of(OrcEntity::new, MobCategory.MISC)
                    .sized(0.5F, 2.0F));
    public static final Supplier<EntityType<SouthronEntity>> ENTITY_TTM_SOUTHRON_NPC =
            registerEntity("entityttmsouthron_npc", () -> EntityType.Builder.of(SouthronEntity::new, MobCategory.MISC)
                    .sized(0.5F, 2.0F));

        // Hostile
    public static final Supplier<EntityType<BarrowWightEntity>> ENTITY_TTM_BARROW =
            registerEntity("entityttmbarrow", () -> EntityType.Builder.of(BarrowWightEntity::new, MobCategory.MONSTER)
                    .sized(0.5f, 2.0f));
    public static final Supplier<EntityType<OathBreakerEntity>> ENTITY_TTM_OATHBREAKER =
            registerEntity("entityttmoathbreaker", () -> EntityType.Builder.of(OathBreakerEntity::new, MobCategory.MONSTER)
                    .sized(0.5f, 2.0f));
    public static final Supplier<EntityType<FellSpiritEntity>> ENTITY_TTM_FELLSPIRIT =
            registerEntity("entityttmfellspirit", () -> EntityType.Builder.of(FellSpiritEntity::new, MobCategory.MONSTER)
                    .sized(0.5f, 2.0f));
    public static final Supplier<EntityType<EnragedFellSpiritEntity>> ENTITY_TTM_ENRAGED_FELLSPIRIT =
            registerEntity("entityttmenragedfellspirit", () -> EntityType.Builder.of(EnragedFellSpiritEntity::new, MobCategory.MONSTER)
                    .sized(0.5f, 2.0f));
    public static final Supplier<EntityType<BrigandEntity>> ENTITY_TTM_BRIGAND =
            registerEntity("entityttmbrigand", () -> EntityType.Builder.of(BrigandEntity::new, MobCategory.MONSTER)
                    .sized(0.5f, 2.0f));
    public static final Supplier<EntityType<HaradrimEntity>> ENTITY_TTM_HARADRIM =
            registerEntity("entityttmharadrim", () -> EntityType.Builder.of(HaradrimEntity::new, MobCategory.MONSTER)
                    .sized(0.5f, 2.0f));
    public static final Supplier<EntityType<RomieWalkerEntity>> ENTITY_TTM_ROMIEWALKER =
            registerEntity("entityttmromiewalker", () -> EntityType.Builder.of(RomieWalkerEntity::new, MobCategory.MONSTER)
                    .sized(0.5f, 2.0f));
    public static final Supplier<EntityType<MimicChestEntity>> ENTITY_TTM_MIMICCHEST =
            registerEntity("entityttmmimicchest", () -> EntityType.Builder.of(MimicChestEntity::new, MobCategory.MONSTER)
                    .sized(1.0F, 1.3125F));
    public static final Supplier<EntityType<MordorOrcEntity>> ENTITY_TTM_MORDORORC =
            registerEntity("entityttmmordororc", () -> EntityType.Builder.of(MordorOrcEntity::new, MobCategory.MONSTER)
                    .sized(0.5F, 2.0625F));
    public static final Supplier<EntityType<UrukHaiEntity>> ENTITY_TTM_URUKHAI =
            registerEntity("entityttmurukhai", () -> EntityType.Builder.of(UrukHaiEntity::new, MobCategory.MONSTER)
                    .sized(0.5F, 2.0625F));
    public static final Supplier<EntityType<DuergarEntity>> ENTITY_TTM_DUERGAR =
            registerEntity("entityttmduergar", () -> EntityType.Builder.of(DuergarEntity::new, MobCategory.MONSTER)
                    .sized(0.50625F, 1.51875F));
    public static final Supplier<EntityType<GoblinEntity>> ENTITY_TTM_GOBLIN =
            registerEntity("entityttmgoblin", () -> EntityType.Builder.<GoblinEntity>of(GoblinEntity::new, MobCategory.MONSTER)
                    .sized(0.375F, 0.9375F));
    public static final Supplier<EntityType<MirkwoodSpiderEntity>> ENTITY_TTM_MIRKWOODSPIDER =
            registerEntity("entityttmmirkwoodspider", () -> EntityType.Builder.<MirkwoodSpiderEntity>of(MirkwoodSpiderEntity::new, MobCategory.MONSTER)
                    .sized(1.25F, 0.8125F));
    public static final Supplier<EntityType<RockGolemEntity>> ENTITY_ROCKGOLEM =
            registerEntity("entityttmrockgolem", () -> EntityType.Builder.of(RockGolemEntity::new, MobCategory.MONSTER)
                    .sized(2.5F, 3.875F));
    public static final Supplier<EntityType<HuronEntity>> ENTITY_TTM_HURON =
            registerEntity("entityttmhuron", () -> EntityType.Builder.of(HuronEntity::new, MobCategory.MONSTER)
                    .sized(0.75F, 2.25F));
    public static final Supplier<EntityType<MinotaurEntity>> ENTITY_TTM_MINOTAUR =
            registerEntity("entityttmminotaur", () -> EntityType.Builder.of(MinotaurEntity::new, MobCategory.MONSTER)
                    .sized(1.0F, 3.4375F));
    public static final Supplier<EntityType<DeepClawEntity>> ENTITY_TTM_DEEPCLAW =
            registerEntity("entityttmdeepclaw", () -> EntityType.Builder.of(DeepClawEntity::new, MobCategory.MONSTER)
                    .sized(0.625F, 0.5625F));
    public static final Supplier<EntityType<TrollEntity>> ENTITY_TTM_TROLL =
            registerEntity("entityttmtroll", () -> EntityType.Builder.of(TrollEntity::new, MobCategory.MONSTER)
                    .sized(0.75F, 2.625F));
    public static final Supplier<EntityType<TreeEntEntity>> ENTITY_TTM_TREEENT =
            registerEntity("entityttmtreeent", () -> EntityType.Builder.of(TreeEntEntity::new, MobCategory.MONSTER)
                    .sized(1.125F, 5.5F));
    public static final Supplier<EntityType<SwampHagEntity>> ENTITY_TTM_SWAMPHAG =
            registerEntity("entityttmswamphag", () -> EntityType.Builder.of(SwampHagEntity::new, MobCategory.MONSTER)
                    .sized(1.0625F, 2.125F));
    public static final Supplier<EntityType<WargEntity>> ENTITY_TTM_WARG =
            registerEntity("entityttmwarg", () -> EntityType.Builder.of(WargEntity::new, MobCategory.MONSTER)
                    .sized(1.875F, 2.0625F));
    public static final Supplier<EntityType<ElementalGolemEntity>> ENTITY_TTM_ELEMENTALGOLEM =
            registerEntity("entityttmelementalgolem", () -> EntityType.Builder.of(ElementalGolemEntity::new, MobCategory.MONSTER)
                    .sized(1.0F, 3.125F));
    public static final Supplier<EntityType<NazgulEntity>> ENTITY_TTM_NAZGUL =
            registerEntity("entityttmnazgul", () -> EntityType.Builder.of(NazgulEntity::new, MobCategory.MONSTER)
                    .sized(0.55F, 2.2F));

        // Boss
    public static final Supplier<EntityType<GoblinKingEntity>> ENTITY_TTM_GOBLINKING =
            registerEntity("entityttmgoblinking", () -> EntityType.Builder.of(GoblinKingEntity::new, MobCategory.MONSTER)
                    .sized(0.5F, 1.25F));
    public static final Supplier<EntityType<BalrogEntity>> ENTITY_TTM_BALROG =
            registerEntity("entityttmbalrog", () -> EntityType.Builder.of(BalrogEntity::new, MobCategory.MONSTER)
                    .sized(1.0625F, 3.5F));
    public static final Supplier<EntityType<ShelobEntity>> ENTITY_TTM_SHELOB =
            registerEntity("entityttmshelob", () -> EntityType.Builder.of(ShelobEntity::new, MobCategory.MONSTER)
                    .sized(1.6875F, 1.78125F));
    public static final Supplier<EntityType<WitchKingEntity>> ENTITY_TTM_WITCHKING =
            registerEntity("entityttmwitchking", () -> EntityType.Builder.of(WitchKingEntity::new, MobCategory.MONSTER)
                    .sized(0.65F, 2.6F));
    public static final Supplier<EntityType<WatcherEntity>> ENTITY_TTM_WATCHER =
            registerEntity("entityttmwatcher", () -> EntityType.Builder.of(WatcherEntity::new, MobCategory.MONSTER)
                    .sized(7.5F, 7.5F));
    public static final Supplier<EntityType<MithrilGolemEntity>> ENTITY_TTM_MITHRILGOLEM =
            registerEntity("entityttmmithrilgolem", () -> EntityType.Builder.of(MithrilGolemEntity::new, MobCategory.MONSTER)
                    .sized(1.0F, 3.125F));
    public static final Supplier<EntityType<MorgulironGolemEntity>> ENTITY_TTM_MORGULIRONGOLEM =
            registerEntity("entityttmmorgulirongolem", () -> EntityType.Builder.of(MorgulironGolemEntity::new, MobCategory.MONSTER)
                    .sized(1.0F, 3.125F));
    public static final Supplier<EntityType<GwahirEntity>> ENTITY_TTM_GWAHIR =
            registerEntity("entityttmgwahir", () -> EntityType.Builder.of(GwahirEntity::new, MobCategory.MONSTER)
                    .sized(1.4F, 1.9F));
    public static final Supplier<EntityType<FellBeastEntity>> ENTITY_FELL_BEAST =
            registerEntity("entityttmfellbeast", () -> EntityType.Builder.of(FellBeastEntity::new, MobCategory.MONSTER)
                    .sized(2.625F, 4.25F));

        // Projectiles
    public static final Supplier<EntityType<BoulderEntity>> AMMO_BOULDER =
            registerEntity("ammo_boulder", () -> EntityType.Builder.<BoulderEntity>of(BoulderEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f));
    public static final Supplier<EntityType<FellBeastFireballEntity>> AMMO_FELLBEAST_FIREBALL =
            registerEntity("ammo_fellbeast_fireball", () -> EntityType.Builder.<FellBeastFireballEntity>of(FellBeastFireballEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f));
    public static final Supplier<EntityType<CobwebProjectileEntity>> AMMO_COBWEB =
            registerEntity("ammo_cobweb", () -> EntityType.Builder.<CobwebProjectileEntity>of(CobwebProjectileEntity::new, MobCategory.MISC)
                    .sized(0.75F, 0.75F));
    public static final Supplier<EntityType<TornadoEntity>> AMMO_TORNADO =
            registerEntity("ammo_tornado", () -> EntityType.Builder.<TornadoEntity>of(TornadoEntity::new, MobCategory.MISC)
                    .sized(0.75F, 0.75F));
    public static final Supplier<EntityType<GaladhrimArrowEntity>> AMMO_ARROW_GALADHRIM =
            registerEntity("ammo_galadhrim_arrow", () -> EntityType.Builder.<GaladhrimArrowEntity>of(GaladhrimArrowEntity::new, MobCategory.MISC)
                    .sized(0.75F, 0.75F));
    public static final Supplier<EntityType<UtumnoArrowEntity>> AMMO_ARROW_UTUMNO =
            registerEntity("ammo_utumno_arrow", () -> EntityType.Builder.<UtumnoArrowEntity>of(UtumnoArrowEntity::new, MobCategory.MISC)
                    .sized(0.75F, 0.75F));
    public static final Supplier<EntityType<SimpleTrapEntity>> TRAP_SIMPLE =
            registerEntity("trap_simple", () -> EntityType.Builder.of(SimpleTrapEntity::new, MobCategory.MISC)
                    .sized(0.75F, 0.75F));

        // Boats
    public static final DeferredHolder<EntityType<?>, EntityType<MallornBoatEntity>> MALLORN_BOAT =
            registerEntity("boat_mallorn", () -> EntityType.Builder.<MallornBoatEntity>of(MallornBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<MallornChestBoatEntity>> MALLORN_CHEST_BOAT =
            registerEntity("chest_boat_mallorn", () -> EntityType.Builder.<MallornChestBoatEntity>of(MallornChestBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<MirkwoodBoatEntity>> MIRKWOOD_BOAT =
            registerEntity("boat_mirkwood", () -> EntityType.Builder.<MirkwoodBoatEntity>of(MirkwoodBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<MirkwoodChestBoatEntity>> MIRKWOOD_CHEST_BOAT =
            registerEntity("chest_boat_mirkwood", () -> EntityType.Builder.<MirkwoodChestBoatEntity>of(MirkwoodChestBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<CulumaldaBoatEntity>> CULUMALDA_BOAT =
            registerEntity("boat_culumalda", () -> EntityType.Builder.<CulumaldaBoatEntity>of(CulumaldaBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<CulumaldaChestBoatEntity>> CULUMALDA_CHEST_BOAT =
            registerEntity("chest_boat_culumalda", () -> EntityType.Builder.<CulumaldaChestBoatEntity>of(CulumaldaChestBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<LebethronBoatEntity>> LEBETHRON_BOAT =
            registerEntity("boat_lebethron", () -> EntityType.Builder.<LebethronBoatEntity>of(LebethronBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<LebethronChestBoatEntity>> LEBETHRON_CHEST_BOAT =
            registerEntity("chest_boat_lebethron", () -> EntityType.Builder.<LebethronChestBoatEntity>of(LebethronChestBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<FangornoakBoatEntity>> FANGORNOAK_BOAT =
            registerEntity("boat_fangornoak", () -> EntityType.Builder.<FangornoakBoatEntity>of(FangornoakBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<FangornoakChestBoatEntity>> FANGORNOAK_CHEST_BOAT =
            registerEntity("chest_boat_fangornoak", () -> EntityType.Builder.<FangornoakChestBoatEntity>of(FangornoakChestBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<DeadwoodBoatEntity>> DEADWOOD_BOAT =
            registerEntity("boat_deadwood", () -> EntityType.Builder.<DeadwoodBoatEntity>of(DeadwoodBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<DeadwoodChestBoatEntity>> DEADWOOD_CHEST_BOAT =
            registerEntity("chest_boat_deadwood", () -> EntityType.Builder.<DeadwoodChestBoatEntity>of(DeadwoodChestBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<DwarvenMapleBoatEntity>> DWARVEN_MAPLE_BOAT =
            registerEntity("boat_dwarven_maple", () -> EntityType.Builder.<DwarvenMapleBoatEntity>of(DwarvenMapleBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<DwarvenMapleChestBoatEntity>> DWARVEN_MAPLE_CHEST_BOAT =
            registerEntity("chest_boat_dwarven_maple", () -> EntityType.Builder.<DwarvenMapleChestBoatEntity>of(DwarvenMapleChestBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10));

    /** Items */
    public static final DeferredHolder<EntityType<?>, EntityType<MorgulCrystalEntity>> MORGUL_CRYSTAL =
            registerEntity("morgul_crystal", () -> EntityType.Builder.<MorgulCrystalEntity>of(MorgulCrystalEntity::new, MobCategory.MISC)
                    .sized(2.0F, 2.0F).clientTrackingRange(16).updateInterval(Integer.MAX_VALUE));

    /** Spawn Eggs */
        // Ambient
    public static final DeferredItem<Item> EGG_TTMGECKO = spawnEgg("entityttmgecko_spawn_egg", ENTITY_TTM_GECKO, 9482106, 16121867);
    public static final DeferredItem<Item> EGG_TTMRAT = spawnEgg("entityttmrat_spawn_egg", ENTITY_TTM_RAT, 9482106, 5600397);
    public static final DeferredItem<Item> EGG_TTMSQUIRREL = spawnEgg("entityttmsquirrel_spawn_egg", ENTITY_TTM_SQUIRREL, 9482106, 7405383);
    public static final DeferredItem<Item> EGG_TTMFROG = spawnEgg("entityttmfrog_spawn_egg", ENTITY_TTM_FROG, 9482106, 14289362);
    public static final DeferredItem<Item> EGG_TTMTHRUSH = spawnEgg("entityttmthrush_spawn_egg", ENTITY_TTM_THRUSH, 9482106, 12198412);
    public static final DeferredItem<Item> EGG_TTMCREBAIN = spawnEgg("entityttmcrebain_spawn_egg", ENTITY_TTM_CREBAIN, 9482106, 16739362);
    public static final DeferredItem<Item> EGG_TTMSWARM = spawnEgg("entityttmswarm_spawn_egg", ENTITY_TTM_SWARM, 9482106, 14088652);
    public static final DeferredItem<Item> EGG_TTMGREATEAGLE = spawnEgg("entityttmgreateagle_spawn_egg", ENTITY_TTM_GREAT_EAGLE, 2576351, 16121867);

        // Passive
    public static final DeferredItem<Item> EGG_TTMAUROCH = spawnEgg("entityttmauroch_spawn_egg", ENTITY_TTM_AUROCH, 7668978, 16121867);
    public static final DeferredItem<Item> EGG_TTMMUMAKIL = spawnEgg("entityttmmumakil_spawn_egg", ENTITY_TTM_MUMAKIL, 7668978, 5600397);
    public static final DeferredItem<Item> EGG_TTMGOAT = spawnEgg("entityttmgoat_spawn_egg", ENTITY_TTM_GOAT, 7668978, 7405383);
    public static final DeferredItem<Item> EGG_TTMSHADOWFAX = spawnEgg("entityttmshadowfax_spawn_egg", ENTITY_TTM_SHADOWFAX, 2576351, 5600397);
    public static final DeferredItem<Item> EGG_TTMNAZGULSTEED = spawnEgg("entityttmnazgulsteed_spawn_egg", ENTITY_TTM_NAZGULSTEED, 2576351, 7405383);
    public static final DeferredItem<Item> EGG_TTMGOLLUM = spawnEgg("entityttmgollum_spawn_egg", ENTITY_TTM_GOLLUM, 2576351, 12198412);
    public static final DeferredItem<Item> EGG_TTMISTARI = spawnEgg("entityttmistari_spawn_egg", ENTITY_TTM_ISTARI, 2576351, 16739362);

        // NPC
    public static final DeferredItem<Item> EGG_TTMHUMAN = spawnEgg("entityttmhuman_spawn_egg", ENTITY_TTM_HUMAN, 15186506, 16121867);
    public static final DeferredItem<Item> EGG_TTMDWARF = spawnEgg("entityttmdwarf_spawn_egg", ENTITY_TTM_DWARF, 15186506, 5600397);
    public static final DeferredItem<Item> EGG_TTMELVES = spawnEgg("entityttmelves_spawn_egg", ENTITY_TTM_ELVES, 15186506, 7405383);
    public static final DeferredItem<Item> EGG_TTMHOBBIT = spawnEgg("entityttmhobbit_spawn_egg", ENTITY_TTM_HOBBIT, 15186506, 14289362);
    public static final DeferredItem<Item> EGG_TTMSOUTHRON = spawnEgg("entityttmsouthron_spawn_egg", ENTITY_TTM_SOUTHRON, 15186506, 12198412);
    public static final DeferredItem<Item> EGG_TTMORC_TRADER = spawnEgg("entityttmorc_trader_spawn_egg", ENTITY_TTM_ORC_TRADER, 15186506, 16739362);
    public static final DeferredItem<Item> EGG_TTMORC = spawnEgg("entityttmorc_spawn_egg", ENTITY_TTM_ORC, 15186506, 9467561);
    public static final DeferredItem<Item> EGG_TTMSOUTHRON_NPC = spawnEgg("entityttmsouthron_npc_spawn_egg", ENTITY_TTM_SOUTHRON_NPC, 15186506, 12198412);

        // Hostile
    public static final DeferredItem<Item> EGG_TTMBARROW = spawnEgg("entityttmbarrow_spawn_egg", ENTITY_TTM_BARROW, 585619, 16121867);
    public static final DeferredItem<Item> EGG_TTMOATHBREAKER = spawnEgg("entityttmoathbreaker_spawn_egg", ENTITY_TTM_OATHBREAKER, 585619, 5600397);
    public static final DeferredItem<Item> EGG_TTMFELLSPIRIT = spawnEgg("entityttmfellspirit_spawn_egg", ENTITY_TTM_FELLSPIRIT, 585619, 7405383);
    public static final DeferredItem<Item> EGG_TTMENRAGEDFELLSPIRIT = spawnEgg("entityttmenragedfellspirit_spawn_egg", ENTITY_TTM_ENRAGED_FELLSPIRIT, 585619, 9649602);
    public static final DeferredItem<Item> EGG_TTMBRIGAND = spawnEgg("entityttmbrigand_spawn_egg", ENTITY_TTM_BRIGAND, 585619, 14289362);
    public static final DeferredItem<Item> EGG_TTMHARADRIM = spawnEgg("entityttmharadrim_spawn_egg", ENTITY_TTM_HARADRIM, 585619, 12198412);
    public static final DeferredItem<Item> EGG_TTMROMIEWALKER = spawnEgg("entityttmromiewalker_spawn_egg", ENTITY_TTM_ROMIEWALKER, 585619, 16739362);
    public static final DeferredItem<Item> EGG_TTMMIMICCHEST = spawnEgg("entityttmmimicchest_spawn_egg", ENTITY_TTM_MIMICCHEST, 585619, 14088652);
    public static final DeferredItem<Item> EGG_TTMMORDORORC = spawnEgg("entityttmmordororc_spawn_egg", ENTITY_TTM_MORDORORC, 585619, 9467561);
    public static final DeferredItem<Item> EGG_TTMURUKHAI = spawnEgg("entityttmurukhai_spawn_egg", ENTITY_TTM_URUKHAI, 585619, 8807990);
    public static final DeferredItem<Item> EGG_TTMDUERGAR = spawnEgg("entityttmduergar_spawn_egg", ENTITY_TTM_DUERGAR, 585619, 9226665);
    public static final DeferredItem<Item> EGG_TTMGOBLIN = spawnEgg("entityttmgoblin_spawn_egg", ENTITY_TTM_GOBLIN, 585619, 1380646);
    public static final DeferredItem<Item> EGG_TTMMIRKWOODSPIDER = spawnEgg("entityttmmirkwoodspider_spawn_egg", ENTITY_TTM_MIRKWOODSPIDER, 585619, 2301661);
    public static final DeferredItem<Item> EGG_TTMROCKGOLEM = spawnEgg("entityttmrockgolem_spawn_egg", ENTITY_ROCKGOLEM, 585619, 10600204);
    public static final DeferredItem<Item> EGG_TTMHURON = spawnEgg("entityttmhuron_spawn_egg", ENTITY_TTM_HURON, 585619, 2973229);
    public static final DeferredItem<Item> EGG_TTMMINOTAUR = spawnEgg("entityttmminotaur_spawn_egg", ENTITY_TTM_MINOTAUR, 585619, 8755748);
    public static final DeferredItem<Item> EGG_TTMDEEPCLAW = spawnEgg("entityttmdeepclaw_spawn_egg", ENTITY_TTM_DEEPCLAW, 585619, 13146148);
    public static final DeferredItem<Item> EGG_TTMTROLL = spawnEgg("entityttmtroll_spawn_egg", ENTITY_TTM_TROLL, 585619, 12659887);
    public static final DeferredItem<Item> EGG_TTMTREEENT = spawnEgg("entityttmtreeent_spawn_egg", ENTITY_TTM_TREEENT, 585619, 2703752);
    public static final DeferredItem<Item> EGG_TTMSWAMPHAG = spawnEgg("entityttmswamphag_spawn_egg", ENTITY_TTM_SWAMPHAG, 585619, 698898);
    public static final DeferredItem<Item> EGG_TTMWARG = spawnEgg("entityttmwarg_spawn_egg", ENTITY_TTM_WARG, 585619, 16211457);
    public static final DeferredItem<Item> EGG_TTMELEMENTALGOLEM = spawnEgg("entityttmelementalgolem_spawn_egg", ENTITY_TTM_ELEMENTALGOLEM, 585619, 7893889);
    public static final DeferredItem<Item> EGG_TTMNAZGUL = spawnEgg("entityttmnazgul_spawn_egg", ENTITY_TTM_NAZGUL, 2576351, 14289362);

        // Boss
    public static final DeferredItem<Item> EGG_TTMGOBLINKING = spawnEgg("entityttmgoblinking_spawn_egg", ENTITY_TTM_GOBLINKING, 15673963, 16121867);
    public static final DeferredItem<Item> EGG_TTMBALROG = spawnEgg("entityttmbalrog_spawn_egg", ENTITY_TTM_BALROG, 15673963, 5600397);
    public static final DeferredItem<Item> EGG_TTMSHELOB = spawnEgg("entityttmshelob_spawn_egg", ENTITY_TTM_SHELOB, 15673963, 7405383);
    public static final DeferredItem<Item> EGG_TTMWITCHKING = spawnEgg("entityttmwitchking_spawn_egg", ENTITY_TTM_WITCHKING, 15673963, 14289362);
    public static final DeferredItem<Item> EGG_TTMWATCHER = spawnEgg("entityttmwatcher_spawn_egg", ENTITY_TTM_WATCHER, 15673963, 12198412);
    public static final DeferredItem<Item> EGG_TTMMITHRILGOLEM = spawnEgg("entityttmmithrilgolem_spawn_egg", ENTITY_TTM_MITHRILGOLEM, 15673963, 16739362);
    public static final DeferredItem<Item> EGG_TTMMORGULIRONGOLEM = spawnEgg("entityttmmorgulirongolem_spawn_egg", ENTITY_TTM_MORGULIRONGOLEM, 15673963, 14088652);
    public static final DeferredItem<Item> EGG_TTMGWAHIR = spawnEgg("entityttmgwahir_spawn_egg", ENTITY_TTM_GWAHIR, 15673963, 9467561);
    public static final DeferredItem<Item> EGG_TTMFELLBEAST = spawnEgg("entityttmfellbeast_spawn_egg", ENTITY_FELL_BEAST, 15673963, 8807990);

    /** Builds an entity type against the key it is registered under, which 1.21.4 requires. */
    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> registerEntity(String name, Supplier<EntityType.Builder<T>> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(MODID, name));
        return ENTITY_TYPES.register(name, () -> builder.get().build(key));
    }

    /** A spawn egg whose entity type and tint colours are resolved lazily, as DeferredSpawnEggItem did. */
    private static DeferredItem<Item> spawnEgg(String name, Supplier<? extends EntityType<? extends Mob>> type, int background, int highlight) {
        DeferredItem<Item> egg = SPAWN_EGGS.register(name, () -> new SpawnEggItem(type.get(), new Item.Properties()));
        SPAWN_EGG_COLORS.put(egg, new SpawnEggColors(background, highlight));

        return egg;
    }

    public record SpawnEggColors(int background, int highlight) {
    }

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
        SPAWN_EGGS.register(eventBus);
    }
}