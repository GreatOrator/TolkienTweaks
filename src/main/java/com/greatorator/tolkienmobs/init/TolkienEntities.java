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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MODID);
    public static final DeferredRegister.Items SPAWN_EGGS = DeferredRegister.createItems(MODID);

        /** Entities */
        // Ambient
    public static final Supplier<EntityType<GeckoEntity>> ENTITY_TTM_GECKO =
            ENTITY_TYPES.register("entityttmgecko", () -> EntityType.Builder.of(GeckoEntity::new, MobCategory.AMBIENT)
                    .sized(0.75f, 0.35f).build("entityttmgecko"));
    public static final Supplier<EntityType<RatEntity>> ENTITY_TTM_RAT =
            ENTITY_TYPES.register("entityttmrat", () -> EntityType.Builder.of(RatEntity::new, MobCategory.AMBIENT)
                    .sized(0.75f, 0.35f).build("entityttmrat"));
    public static final Supplier<EntityType<SquirrelEntity>> ENTITY_TTM_SQUIRREL =
            ENTITY_TYPES.register("entityttmsquirrel", () -> EntityType.Builder.of(SquirrelEntity::new, MobCategory.AMBIENT)
                    .sized(0.75f, 0.35f).build("entityttmsquirrel"));
    public static final Supplier<EntityType<FrogEntity>> ENTITY_TTM_FROG =
            ENTITY_TYPES.register("entityttmfrog", () -> EntityType.Builder.of(FrogEntity::new, MobCategory.AMBIENT)
                    .sized(0.75f, 0.35f).build("entityttmfrog"));
    public static final Supplier<EntityType<ThrushEntity>> ENTITY_TTM_THRUSH =
            ENTITY_TYPES.register("entityttmthrush", () -> EntityType.Builder.of(ThrushEntity::new, MobCategory.AMBIENT)
                    .sized(0.75f, 0.35f).build("entityttmthrush"));
    public static final Supplier<EntityType<CrebainEntity>> ENTITY_TTM_CREBAIN =
            ENTITY_TYPES.register("entityttmcrebain", () -> EntityType.Builder.of(CrebainEntity::new, MobCategory.AMBIENT)
                    .sized(0.75f, 0.35f).build("entityttmcrebain"));
    public static final Supplier<EntityType<SwarmEntity>> ENTITY_TTM_SWARM =
            ENTITY_TYPES.register("entityttmswarm", () -> EntityType.Builder.of(SwarmEntity::new, MobCategory.MONSTER)
                    .sized(0.75f, 0.35f).build("entityttmswarm"));
    public static final Supplier<EntityType<GreatEagleEntity>> ENTITY_TTM_GREAT_EAGLE =
            ENTITY_TYPES.register("entityttmgreateagle", () -> EntityType.Builder.of(GreatEagleEntity::new, MobCategory.CREATURE)
                    .sized(1.225F, 1.6625F).build("entityttmgreateagle"));

        // Passive
    public static final Supplier<EntityType<AurochEntity>> ENTITY_TTM_AUROCH =
            ENTITY_TYPES.register("entityttmauroch", () -> EntityType.Builder.of(AurochEntity::new, MobCategory.CREATURE)
                    .sized(1.125F, 1.3125F).build("entityttmauroch"));
    public static final Supplier<EntityType<MumakilEntity>> ENTITY_TTM_MUMAKIL =
            ENTITY_TYPES.register("entityttmmumakil", () -> EntityType.Builder.of(MumakilEntity::new, MobCategory.CREATURE)
                    .sized(2.5F, 3.3125F).build("entityttmmumakil"));
    public static final Supplier<EntityType<GoatEntity>> ENTITY_TTM_GOAT =
            ENTITY_TYPES.register("entityttmgoat", () -> EntityType.Builder.of(GoatEntity::new, MobCategory.CREATURE)
                    .sized(0.875F, 1.875F).build("entityttmgoat"));
    public static final Supplier<EntityType<NazgulSteedEntity>> ENTITY_TTM_NAZGULSTEED =
            ENTITY_TYPES.register("entityttmnazgulsteed", () -> EntityType.Builder.of(NazgulSteedEntity::new, MobCategory.CREATURE)
                    .sized(1.0F, 1.5F).build("entityttmnazgulsteed"));
    public static final Supplier<EntityType<ShadowfaxEntity>> ENTITY_TTM_SHADOWFAX =
            ENTITY_TYPES.register("entityttmshadowfax", () -> EntityType.Builder.of(ShadowfaxEntity::new, MobCategory.CREATURE)
                    .sized(1.0F, 1.5F).build("entityttmshadowfax"));
    public static final Supplier<EntityType<GollumEntity>> ENTITY_TTM_GOLLUM =
            ENTITY_TYPES.register("entityttmgollum", () -> EntityType.Builder.of(GollumEntity::new, MobCategory.CREATURE)
                    .sized(0.625F, 1.5625F).build("entityttmgollum"));
    public static final Supplier<EntityType<IstariEntity>> ENTITY_TTM_ISTARI =
            ENTITY_TYPES.register("entityttmistari", () -> EntityType.Builder.of(IstariEntity::new, MobCategory.CREATURE)
                    .sized(0.75F, 2.0F).build("entityttmistari"));

        // NPC
    public static final Supplier<EntityType<HumanEntity>> ENTITY_TTM_HUMAN =
            ENTITY_TYPES.register("entityttmhuman", () -> EntityType.Builder.of(HumanEntity::new, MobCategory.MISC)
                    .sized(0.5F, 2.0F).build("entityttmhuman"));
    public static final Supplier<EntityType<DwarfEntity>> ENTITY_TTM_DWARF =
            ENTITY_TYPES.register("entityttmdwarf", () -> EntityType.Builder.of(DwarfEntity::new, MobCategory.MISC)
                    .sized(0.50625F, 1.51875F).build("entityttmdwarf"));
    public static final Supplier<EntityType<ElvesEntity>> ENTITY_TTM_ELVES =
            ENTITY_TYPES.register("entityttmelves", () -> EntityType.Builder.of(ElvesEntity::new, MobCategory.MISC)
                    .sized(0.55F, 2.2F).build("entityttmelves"));
    public static final Supplier<EntityType<HobbitEntity>> ENTITY_TTM_HOBBIT =
            ENTITY_TYPES.register("entityttmhobbit", () -> EntityType.Builder.of(HobbitEntity::new, MobCategory.MISC)
                    .sized(0.35F, 1.4F).build("entityttmhobbit"));
    public static final Supplier<EntityType<SouthronEntity>> ENTITY_TTM_SOUTHRON =
            ENTITY_TYPES.register("entityttmsouthron", () -> EntityType.Builder.of(SouthronEntity::new, MobCategory.MISC)
                    .sized(0.5F, 2.0F).build("entityttmsouthron"));
    public static final Supplier<EntityType<OrcTraderEntity>> ENTITY_TTM_ORC_TRADER =
            ENTITY_TYPES.register("entityttmorc_trader", () -> EntityType.Builder.of(OrcTraderEntity::new, MobCategory.MISC)
                    .sized(0.5F, 2.0F).build("entityttmorc_trader"));

        // Hostile
    public static final Supplier<EntityType<BarrowWightEntity>> ENTITY_TTM_BARROW =
            ENTITY_TYPES.register("entityttmbarrow", () -> EntityType.Builder.of(BarrowWightEntity::new, MobCategory.MONSTER)
                    .sized(0.5f, 2.0f).build("entityttmbarrow"));
    public static final Supplier<EntityType<OathBreakerEntity>> ENTITY_TTM_OATHBREAKER =
            ENTITY_TYPES.register("entityttmoathbreaker", () -> EntityType.Builder.of(OathBreakerEntity::new, MobCategory.MONSTER)
                    .sized(0.5f, 2.0f).build("entityttmoathbreaker"));
    public static final Supplier<EntityType<FellSpiritEntity>> ENTITY_TTM_FELLSPIRIT =
            ENTITY_TYPES.register("entityttmfellspirit", () -> EntityType.Builder.of(FellSpiritEntity::new, MobCategory.MONSTER)
                    .sized(0.5f, 2.0f).build("entityttmfellspirit"));
    public static final Supplier<EntityType<EnragedFellSpiritEntity>> ENTITY_TTM_ENRAGED_FELLSPIRIT =
            ENTITY_TYPES.register("entityttmenragedfellspirit", () -> EntityType.Builder.of(EnragedFellSpiritEntity::new, MobCategory.MONSTER)
                    .sized(0.5f, 2.0f).build("entityttmfellspirit"));
    public static final Supplier<EntityType<BrigandEntity>> ENTITY_TTM_BRIGAND =
            ENTITY_TYPES.register("entityttmbrigand", () -> EntityType.Builder.of(BrigandEntity::new, MobCategory.MONSTER)
                    .sized(0.5f, 2.0f).build("entityttmbrigand"));
    public static final Supplier<EntityType<HaradrimEntity>> ENTITY_TTM_HARADRIM =
            ENTITY_TYPES.register("entityttmharadrim", () -> EntityType.Builder.of(HaradrimEntity::new, MobCategory.MONSTER)
                    .sized(0.5f, 2.0f).build("entityttmharadrim"));
    public static final Supplier<EntityType<RomieWalkerEntity>> ENTITY_TTM_ROMIEWALKER =
            ENTITY_TYPES.register("entityttmromiewalker", () -> EntityType.Builder.of(RomieWalkerEntity::new, MobCategory.MONSTER)
                    .sized(0.5f, 2.0f).build("entityttmromiewalker"));
    public static final Supplier<EntityType<MimicChestEntity>> ENTITY_TTM_MIMICCHEST =
            ENTITY_TYPES.register("entityttmmimicchest", () -> EntityType.Builder.of(MimicChestEntity::new, MobCategory.MONSTER)
                    .sized(1.0F, 1.3125F).build("entityttmmimicchest"));
    public static final Supplier<EntityType<MordorOrcEntity>> ENTITY_TTM_MORDORORC =
            ENTITY_TYPES.register("entityttmmordororc", () -> EntityType.Builder.of(MordorOrcEntity::new, MobCategory.MONSTER)
                    .sized(0.5F, 2.0625F).build("entityttmmordororc"));
    public static final Supplier<EntityType<UrukHaiEntity>> ENTITY_TTM_URUKHAI =
            ENTITY_TYPES.register("entityttmurukhai", () -> EntityType.Builder.of(UrukHaiEntity::new, MobCategory.MONSTER)
                    .sized(0.5F, 2.0625F).build("entityttmurukhai"));
    public static final Supplier<EntityType<DuergarEntity>> ENTITY_TTM_DUERGAR =
            ENTITY_TYPES.register("entityttmduergar", () -> EntityType.Builder.of(DuergarEntity::new, MobCategory.MONSTER)
                    .sized(0.50625F, 1.51875F).build("entityttmduergar"));
    public static final Supplier<EntityType<GoblinEntity>> ENTITY_TTM_GOBLIN =
            ENTITY_TYPES.register("entityttmgoblin", () -> EntityType.Builder.<GoblinEntity>of(GoblinEntity::new, MobCategory.MONSTER)
                    .sized(0.375F, 0.9375F).build("entityttmgoblin"));
    public static final Supplier<EntityType<MirkwoodSpiderEntity>> ENTITY_TTM_MIRKWOODSPIDER =
            ENTITY_TYPES.register("entityttmmirkwoodspider", () -> EntityType.Builder.<MirkwoodSpiderEntity>of(MirkwoodSpiderEntity::new, MobCategory.MONSTER)
                    .sized(1.25F, 0.8125F).build("entityttmmirkwoodspider"));
    public static final Supplier<EntityType<RockGolemEntity>> ENTITY_ROCKGOLEM =
            ENTITY_TYPES.register("entityttmrockgolem", () -> EntityType.Builder.of(RockGolemEntity::new, MobCategory.MONSTER)
                    .sized(2.5F, 3.875F).build("entityttmrockgolem"));
    public static final Supplier<EntityType<HuronEntity>> ENTITY_TTM_HURON =
            ENTITY_TYPES.register("entityttmhuron", () -> EntityType.Builder.of(HuronEntity::new, MobCategory.MONSTER)
                    .sized(0.75F, 2.25F).build("entityttmhuron"));
    public static final Supplier<EntityType<MinotaurEntity>> ENTITY_TTM_MINOTAUR =
            ENTITY_TYPES.register("entityttmminotaur", () -> EntityType.Builder.of(MinotaurEntity::new, MobCategory.MONSTER)
                    .sized(1.0F, 3.4375F).build("entityttmminotaur"));
    public static final Supplier<EntityType<DeepClawEntity>> ENTITY_TTM_DEEPCLAW =
            ENTITY_TYPES.register("entityttmdeepclaw", () -> EntityType.Builder.of(DeepClawEntity::new, MobCategory.MONSTER)
                    .sized(0.625F, 0.5625F).build("entityttmdeepclaw"));
    public static final Supplier<EntityType<TrollEntity>> ENTITY_TTM_TROLL =
            ENTITY_TYPES.register("entityttmtroll", () -> EntityType.Builder.of(TrollEntity::new, MobCategory.MONSTER)
                    .sized(0.75F, 2.625F).build("entityttmtroll"));
    public static final Supplier<EntityType<TreeEntEntity>> ENTITY_TTM_TREEENT =
            ENTITY_TYPES.register("entityttmtreeent", () -> EntityType.Builder.of(TreeEntEntity::new, MobCategory.MONSTER)
                    .sized(1.125F, 5.5F).build("entityttmtreeent"));
    public static final Supplier<EntityType<SwampHagEntity>> ENTITY_TTM_SWAMPHAG =
            ENTITY_TYPES.register("entityttmswamphag", () -> EntityType.Builder.of(SwampHagEntity::new, MobCategory.MONSTER)
                    .sized(1.0625F, 2.125F).build("entityttmswamphag"));
    public static final Supplier<EntityType<WargEntity>> ENTITY_TTM_WARG =
            ENTITY_TYPES.register("entityttmwarg", () -> EntityType.Builder.of(WargEntity::new, MobCategory.MONSTER)
                    .sized(1.875F, 2.0625F).build("entityttmwarg"));
    public static final Supplier<EntityType<ElementalGolemEntity>> ENTITY_TTM_ELEMENTALGOLEM =
            ENTITY_TYPES.register("entityttmelementalgolem", () -> EntityType.Builder.of(ElementalGolemEntity::new, MobCategory.MONSTER)
                    .sized(1.0F, 3.125F).build("entityttmelementalgolem"));
    public static final Supplier<EntityType<NazgulEntity>> ENTITY_TTM_NAZGUL =
            ENTITY_TYPES.register("entityttmnazgul", () -> EntityType.Builder.of(NazgulEntity::new, MobCategory.MONSTER)
                    .sized(0.55F, 2.2F).build("entityttmnazgul"));

        // Boss
    public static final Supplier<EntityType<GoblinKingEntity>> ENTITY_TTM_GOBLINKING =
            ENTITY_TYPES.register("entityttmgoblinking", () -> EntityType.Builder.of(GoblinKingEntity::new, MobCategory.MONSTER)
                    .sized(0.5F, 1.25F).build("entityttmgoblinking"));
    public static final Supplier<EntityType<BalrogEntity>> ENTITY_TTM_BALROG =
            ENTITY_TYPES.register("entityttmbalrog", () -> EntityType.Builder.of(BalrogEntity::new, MobCategory.MONSTER)
                    .sized(1.0625F, 3.5F).build("entityttmbalrog"));
    public static final Supplier<EntityType<ShelobEntity>> ENTITY_TTM_SHELOB =
            ENTITY_TYPES.register("entityttmshelob", () -> EntityType.Builder.of(ShelobEntity::new, MobCategory.MONSTER)
                    .sized(1.6875F, 1.78125F).build("entityttmshelob"));
    public static final Supplier<EntityType<WitchKingEntity>> ENTITY_TTM_WITCHKING =
            ENTITY_TYPES.register("entityttmwitchking", () -> EntityType.Builder.of(WitchKingEntity::new, MobCategory.MONSTER)
                    .sized(0.65F, 2.6F).build("entityttmwitchking"));
    public static final Supplier<EntityType<WatcherEntity>> ENTITY_TTM_WATCHER =
            ENTITY_TYPES.register("entityttmwatcher", () -> EntityType.Builder.of(WatcherEntity::new, MobCategory.MONSTER)
                    .sized(7.5F, 7.5F).build("entityttmwatcher"));
    public static final Supplier<EntityType<MithrilGolemEntity>> ENTITY_TTM_MITHRILGOLEM =
            ENTITY_TYPES.register("entityttmmithrilgolem", () -> EntityType.Builder.of(MithrilGolemEntity::new, MobCategory.MONSTER)
                    .sized(1.0F, 3.125F).build("entityttmmithrilgolem"));
    public static final Supplier<EntityType<MorgulironGolemEntity>> ENTITY_TTM_MORGULIRONGOLEM =
            ENTITY_TYPES.register("entityttmmorgulirongolem", () -> EntityType.Builder.of(MorgulironGolemEntity::new, MobCategory.MONSTER)
                    .sized(1.0F, 3.125F).build("entityttmmorgulirongolem"));
    public static final Supplier<EntityType<GwahirEntity>> ENTITY_TTM_GWAHIR =
            ENTITY_TYPES.register("entityttmgwahir", () -> EntityType.Builder.of(GwahirEntity::new, MobCategory.MONSTER)
                    .sized(1.4F, 1.9F).build("entityttmgwahir"));
    public static final Supplier<EntityType<FellBeastEntity>> ENTITY_FELL_BEAST =
            ENTITY_TYPES.register("entityttmfellbeast", () -> EntityType.Builder.of(FellBeastEntity::new, MobCategory.MONSTER)
                    .sized(2.625F, 4.25F).build("entityttmfellbeast"));

        // Projectiles
    public static final Supplier<EntityType<BoulderEntity>> AMMO_BOULDER =
            ENTITY_TYPES.register("ammo_boulder", () -> EntityType.Builder.<BoulderEntity>of(BoulderEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).build("ammo_boulder"));
    public static final Supplier<EntityType<FellBeastFireballEntity>> AMMO_FELLBEAST_FIREBALL =
            ENTITY_TYPES.register("ammo_fellbeast_fireball", () -> EntityType.Builder.<FellBeastFireballEntity>of(FellBeastFireballEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).build("ammo_fellbeast_fireball"));
    public static final Supplier<EntityType<CobwebProjectileEntity>> AMMO_COBWEB =
            ENTITY_TYPES.register("ammo_cobweb", () -> EntityType.Builder.<CobwebProjectileEntity>of(CobwebProjectileEntity::new, MobCategory.MISC)
                    .sized(0.75F, 0.75F).build("ammo_cobweb"));
    public static final Supplier<EntityType<TornadoEntity>> AMMO_TORNADO =
            ENTITY_TYPES.register("ammo_tornado", () -> EntityType.Builder.<TornadoEntity>of(TornadoEntity::new, MobCategory.MISC)
                    .sized(0.75F, 0.75F).build("ammo_tornado"));
    public static final Supplier<EntityType<GaladhrimArrowEntity>> AMMO_ARROW_GALADHRIM =
            ENTITY_TYPES.register("ammo_galadhrim_arrow", () -> EntityType.Builder.<GaladhrimArrowEntity>of(GaladhrimArrowEntity::new, MobCategory.MISC)
                    .sized(0.75F, 0.75F).build("ammo_galadhrim_arrow"));
    public static final Supplier<EntityType<UtumnoArrowEntity>> AMMO_ARROW_UTUMNO =
            ENTITY_TYPES.register("ammo_utumno_arrow", () -> EntityType.Builder.<UtumnoArrowEntity>of(UtumnoArrowEntity::new, MobCategory.MISC)
                    .sized(0.75F, 0.75F).build("ammo_utumno_arrow"));
    public static final Supplier<EntityType<SimpleTrapEntity>> TRAP_SIMPLE =
            ENTITY_TYPES.register("trap_simple", () -> EntityType.Builder.of(SimpleTrapEntity::new, MobCategory.MISC)
                    .sized(0.75F, 0.75F).build("trap_simple"));

        // Boats
    public static final DeferredHolder<EntityType<?>, EntityType<MallornBoatEntity>> MALLORN_BOAT =
            ENTITY_TYPES.register("boat_mallorn", () -> EntityType.Builder.<MallornBoatEntity>of(MallornBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10).build("boat_mallorn"));
    public static final DeferredHolder<EntityType<?>, EntityType<MallornChestBoatEntity>> MALLORN_CHEST_BOAT =
            ENTITY_TYPES.register("chest_boat_mallorn", () -> EntityType.Builder.<MallornChestBoatEntity>of(MallornChestBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10).build("boat_mallorn"));
    public static final DeferredHolder<EntityType<?>, EntityType<MirkwoodBoatEntity>> MIRKWOOD_BOAT =
            ENTITY_TYPES.register("boat_mirkwood", () -> EntityType.Builder.<MirkwoodBoatEntity>of(MirkwoodBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10).build("boat_mirkwood"));
    public static final DeferredHolder<EntityType<?>, EntityType<MirkwoodChestBoatEntity>> MIRKWOOD_CHEST_BOAT =
            ENTITY_TYPES.register("chest_boat_mirkwood", () -> EntityType.Builder.<MirkwoodChestBoatEntity>of(MirkwoodChestBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10).build("boat_mirkwood"));
    public static final DeferredHolder<EntityType<?>, EntityType<CulumaldaBoatEntity>> CULUMALDA_BOAT =
            ENTITY_TYPES.register("boat_culumalda", () -> EntityType.Builder.<CulumaldaBoatEntity>of(CulumaldaBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10).build("boat_culumalda"));
    public static final DeferredHolder<EntityType<?>, EntityType<CulumaldaChestBoatEntity>> CULUMALDA_CHEST_BOAT =
            ENTITY_TYPES.register("chest_boat_culumalda", () -> EntityType.Builder.<CulumaldaChestBoatEntity>of(CulumaldaChestBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10).build("boat_culumalda"));
    public static final DeferredHolder<EntityType<?>, EntityType<LebethronBoatEntity>> LEBETHRON_BOAT =
            ENTITY_TYPES.register("boat_lebethron", () -> EntityType.Builder.<LebethronBoatEntity>of(LebethronBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10).build("boat_lebethron"));
    public static final DeferredHolder<EntityType<?>, EntityType<LebethronChestBoatEntity>> LEBETHRON_CHEST_BOAT =
            ENTITY_TYPES.register("chest_boat_lebethron", () -> EntityType.Builder.<LebethronChestBoatEntity>of(LebethronChestBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10).build("boat_lebethron"));
    public static final DeferredHolder<EntityType<?>, EntityType<FangornoakBoatEntity>> FANGORNOAK_BOAT =
            ENTITY_TYPES.register("boat_fangornoak", () -> EntityType.Builder.<FangornoakBoatEntity>of(FangornoakBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10).build("boat_fangornoak"));
    public static final DeferredHolder<EntityType<?>, EntityType<FangornoakChestBoatEntity>> FANGORNOAK_CHEST_BOAT =
            ENTITY_TYPES.register("chest_boat_fangornoak", () -> EntityType.Builder.<FangornoakChestBoatEntity>of(FangornoakChestBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10).build("boat_fangornoak"));
    public static final DeferredHolder<EntityType<?>, EntityType<DeadwoodBoatEntity>> DEADWOOD_BOAT =
            ENTITY_TYPES.register("boat_deadwood", () -> EntityType.Builder.<DeadwoodBoatEntity>of(DeadwoodBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10).build("boat_deadwood"));
    public static final DeferredHolder<EntityType<?>, EntityType<DeadwoodChestBoatEntity>> DEADWOOD_CHEST_BOAT =
            ENTITY_TYPES.register("chest_boat_deadwood", () -> EntityType.Builder.<DeadwoodChestBoatEntity>of(DeadwoodChestBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10).build("boat_deadwood"));
    public static final DeferredHolder<EntityType<?>, EntityType<DwarvenMapleBoatEntity>> DWARVEN_MAPLE_BOAT =
            ENTITY_TYPES.register("boat_dwarven_maple", () -> EntityType.Builder.<DwarvenMapleBoatEntity>of(DwarvenMapleBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10).build("boat_dwarven_maple"));
    public static final DeferredHolder<EntityType<?>, EntityType<DwarvenMapleChestBoatEntity>> DWARVEN_MAPLE_CHEST_BOAT =
            ENTITY_TYPES.register("chest_boat_dwarven_maple", () -> EntityType.Builder.<DwarvenMapleChestBoatEntity>of(DwarvenMapleChestBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).clientTrackingRange(10).build("boat_dwarven_maple"));

    /** Items */
    public static final DeferredHolder<EntityType<?>, EntityType<MorgulCrystalEntity>> MORGUL_CRYSTAL =
            ENTITY_TYPES.register("morgul_crystal", () -> EntityType.Builder.<MorgulCrystalEntity>of(MorgulCrystalEntity::new, MobCategory.MISC)
                    .sized(2.0F, 2.0F).clientTrackingRange(16).updateInterval(Integer.MAX_VALUE).build(MODID + ":morgul_crystal"));

    /** Spawn Eggs */
        // Ambient
    public static final DeferredItem<Item> EGG_TTMGECKO = SPAWN_EGGS.register("entityttmgecko_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_GECKO, 9482106, 16121867,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMRAT = SPAWN_EGGS.register("entityttmrat_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_RAT, 9482106, 5600397,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMSQUIRREL = SPAWN_EGGS.register("entityttmsquirrel_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_SQUIRREL, 9482106, 7405383,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMFROG = SPAWN_EGGS.register("entityttmfrog_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_FROG, 9482106, 14289362,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMTHRUSH = SPAWN_EGGS.register("entityttmthrush_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_THRUSH, 9482106, 12198412,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMCREBAIN = SPAWN_EGGS.register("entityttmcrebain_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_CREBAIN, 9482106, 16739362,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMSWARM = SPAWN_EGGS.register("entityttmswarm_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_SWARM, 9482106, 14088652,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMGREATEAGLE = SPAWN_EGGS.register("entityttmgreateagle_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_GREAT_EAGLE, 2576351, 16121867,
                    new Item.Properties()));

        // Passive
    public static final DeferredItem<Item> EGG_TTMAUROCH = SPAWN_EGGS.register("entityttmauroch_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_AUROCH, 7668978, 16121867,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMMUMAKIL = SPAWN_EGGS.register("entityttmmumakil_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_MUMAKIL, 7668978, 5600397,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMGOAT = SPAWN_EGGS.register("entityttmgoat_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_GOAT, 7668978, 7405383,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMSHADOWFAX = SPAWN_EGGS.register("entityttmshadowfax_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_SHADOWFAX, 2576351, 5600397,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMNAZGULSTEED = SPAWN_EGGS.register("entityttmnazgulsteed_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_NAZGULSTEED, 2576351, 7405383,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMGOLLUM = SPAWN_EGGS.register("entityttmgollum_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_GOLLUM, 2576351, 12198412,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMISTARI = SPAWN_EGGS.register("entityttmistari_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_ISTARI, 2576351, 16739362,
                    new Item.Properties()));

        // NPC
    public static final DeferredItem<Item> EGG_TTMHUMAN = SPAWN_EGGS.register("entityttmhuman_spawn_egg",
                () -> new DeferredSpawnEggItem(ENTITY_TTM_HUMAN, 15186506, 16121867,
                        new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMDWARF = SPAWN_EGGS.register("entityttmdwarf_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_DWARF, 15186506, 5600397,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMELVES = SPAWN_EGGS.register("entityttmelves_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_ELVES, 15186506, 7405383,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMHOBBIT = SPAWN_EGGS.register("entityttmhobbit_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_HOBBIT, 15186506, 14289362,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMSOUTHRON = SPAWN_EGGS.register("entityttmsouthron_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_SOUTHRON, 15186506, 12198412,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMORC_TRADER = SPAWN_EGGS.register("entityttmorc_trader_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_ORC_TRADER, 15186506, 16739362,
                    new Item.Properties()));

        // Hostile
    public static final DeferredItem<Item> EGG_TTMBARROW = SPAWN_EGGS.register("entityttmbarrow_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_BARROW, 585619, 16121867,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMOATHBREAKER = SPAWN_EGGS.register("entityttmoathbreaker_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_OATHBREAKER, 585619, 5600397,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMFELLSPIRIT = SPAWN_EGGS.register("entityttmfellspirit_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_FELLSPIRIT, 585619, 7405383,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMENRAGEDFELLSPIRIT = SPAWN_EGGS.register("entityttmenragedfellspirit_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_ENRAGED_FELLSPIRIT, 585619, 9649602,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMBRIGAND = SPAWN_EGGS.register("entityttmbrigand_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_BRIGAND, 585619, 14289362,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMHARADRIM = SPAWN_EGGS.register("entityttmharadrim_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_HARADRIM, 585619, 12198412,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMROMIEWALKER = SPAWN_EGGS.register("entityttmromiewalker_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_ROMIEWALKER, 585619, 16739362,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMMIMICCHEST = SPAWN_EGGS.register("entityttmmimicchest_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_MIMICCHEST, 585619, 14088652,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMMORDORORC = SPAWN_EGGS.register("entityttmmordororc_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_MORDORORC, 585619, 9467561,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMURUKHAI = SPAWN_EGGS.register("entityttmurukhai_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_URUKHAI, 585619, 8807990,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMDUERGAR = SPAWN_EGGS.register("entityttmduergar_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_DUERGAR, 585619, 9226665,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMGOBLIN = SPAWN_EGGS.register("entityttmgoblin_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_GOBLIN, 585619, 1380646,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMMIRKWOODSPIDER = SPAWN_EGGS.register("entityttmmirkwoodspider_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_MIRKWOODSPIDER, 585619, 2301661,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMROCKGOLEM = SPAWN_EGGS.register("entityttmrockgolem_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_ROCKGOLEM, 585619, 10600204,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMHURON = SPAWN_EGGS.register("entityttmhuron_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_HURON, 585619, 2973229,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMMINOTAUR = SPAWN_EGGS.register("entityttmminotaur_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_MINOTAUR, 585619, 8755748,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMDEEPCLAW = SPAWN_EGGS.register("entityttmdeepclaw_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_DEEPCLAW, 585619, 13146148,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMTROLL = SPAWN_EGGS.register("entityttmtroll_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_TROLL, 585619, 12659887,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMTREEENT = SPAWN_EGGS.register("entityttmtreeent_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_TREEENT, 585619, 2703752,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMSWAMPHAG = SPAWN_EGGS.register("entityttmswamphag_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_SWAMPHAG, 585619, 698898,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMWARG = SPAWN_EGGS.register("entityttmwarg_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_WARG, 585619, 16211457,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMELEMENTALGOLEM = SPAWN_EGGS.register("entityttmelementalgolem_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_ELEMENTALGOLEM, 585619, 7893889,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMNAZGUL = SPAWN_EGGS.register("entityttmnazgul_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_NAZGUL, 2576351, 14289362,
                    new Item.Properties()));

        // Boss
    public static final DeferredItem<Item> EGG_TTMGOBLINKING = SPAWN_EGGS.register("entityttmgoblinking_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_GOBLINKING, 15673963, 16121867,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMBALROG = SPAWN_EGGS.register("entityttmbalrog_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_BALROG, 15673963, 5600397,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMSHELOB = SPAWN_EGGS.register("entityttmshelob_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_SHELOB, 15673963, 7405383,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMWITCHKING = SPAWN_EGGS.register("entityttmwitchking_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_WITCHKING, 15673963, 14289362,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMWATCHER = SPAWN_EGGS.register("entityttmwatcher_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_WATCHER, 15673963, 12198412,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMMITHRILGOLEM = SPAWN_EGGS.register("entityttmmithrilgolem_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_MITHRILGOLEM, 15673963, 16739362,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMMORGULIRONGOLEM = SPAWN_EGGS.register("entityttmmorgulirongolem_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_MORGULIRONGOLEM, 15673963, 14088652,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMGWAHIR = SPAWN_EGGS.register("entityttmgwahir_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_TTM_GWAHIR, 15673963, 9467561,
                    new Item.Properties()));
    public static final DeferredItem<Item> EGG_TTMFELLBEAST = SPAWN_EGGS.register("entityttmfellbeast_spawn_egg",
            () -> new DeferredSpawnEggItem(ENTITY_FELL_BEAST, 15673963, 8807990,
                    new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
        SPAWN_EGGS.register(eventBus);
    }
}