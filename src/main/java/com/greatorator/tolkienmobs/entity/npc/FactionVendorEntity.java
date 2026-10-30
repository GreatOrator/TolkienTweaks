package com.greatorator.tolkienmobs.entity.npc;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;
import com.greatorator.tolkienmobs.faction.Alliance;
import com.greatorator.tolkienmobs.faction.FactionRank;
import com.greatorator.tolkienmobs.faction.FactionStandings;
import com.greatorator.tolkienmobs.init.TolkienFactionVendorTrades;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

/**
 * The merchant a hall keeps behind its counter.
 * <p>
 * One entity carries every alliance's envoy, the way one entity carries every alliance's guard: the
 * alliance decides which folder its liveries are read from, and which shelves it opens.
 * <p>
 * The shelves are the standing, not the trade count. A villager earns its level by being traded with;
 * an envoy will not open a friend's shelf to a stranger, so the shelf is read off the player's
 * {@link FactionRank} each time the counter is opened, and the offers are built again when it has
 * moved. That is why this overrides {@code updateTrades} rather than letting the game level the shop:
 * the state that decides the stock is on the player, not on the shopkeeper.
 */
public class FactionVendorEntity extends Villager implements GeoEntity {
    private static final EntityDataAccessor<Integer> ALLIANCE =
            SynchedEntityData.defineId(FactionVendorEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SKIN =
            SynchedEntityData.defineId(FactionVendorEntity.class, EntityDataSerializers.INT);

    /** How many liveries each side has painted, in that side's own folder. */
    public static final int SKINS = 3;

    /** Server-side only: whether anything has named this envoy's side yet. */
    private boolean allianceChosen;

    /**
     * Server-side only: the shelf the last player opened, so a player whose standing has moved gets a
     * fresh stock and a player whose has not keeps the one already standing.
     */
    private int shelf;

    public FactionVendorEntity(EntityType<? extends Villager> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    /**
     * ALLIANCE + SKIN
     */
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ALLIANCE, 0);
        builder.define(SKIN, 0);
    }

    public Alliance alliance() {
        return Alliance.values()[Mth.clamp(this.entityData.get(ALLIANCE), 0, Alliance.values().length - 1)];
    }

    public void setAlliance(Alliance alliance) {
        this.entityData.set(ALLIANCE, alliance.ordinal());
        this.allianceChosen = true;
    }

    public int skin() {
        return Math.floorMod(this.entityData.get(SKIN), SKINS);
    }

    public void setSkin(int skin) {
        this.entityData.set(SKIN, Math.floorMod(skin, SKINS));
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Alliance", this.alliance().ordinal());
        output.putInt("Skin", this.skin());
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setAlliance(Alliance.values()[Mth.clamp(input.getIntOr("Alliance", 0), 0, Alliance.values().length - 1)]);
        this.setSkin(input.getIntOr("Skin", 0));
        // The offers in the file were built for whoever last opened this counter. The next player's
        // standing decides the shelves, so they are dropped and built again on the next look.
        this.offers = null;
        this.shelf = 0;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        EntitySpawnReason spawnType, @Nullable SpawnGroupData spawnGroupData) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        // An envoy placed by a hall carries its side in its own data already. One that arrives any
        // other way - an egg, a summon - takes a side and a livery at random, so one egg shows all three.
        if (!this.allianceChosen) {
            this.setAlliance(Alliance.values()[this.random.nextInt(Alliance.values().length)]);
            this.setSkin(this.random.nextInt(SKINS));
        }
        this.setPersistenceRequired();
        return result;
    }

    /**
     * How deep the counter opens for a player: nothing until the country knows them, then a shelf per rank.
     *
     * @return the shelf to open, or 0 when the player is not known here yet
     */
    private int shelfFor(Player player) {
        return switch (FactionStandings.rank(player, this.alliance())) {
            case HOSTILE, OUTSIDER, STRANGER -> 0;
            case GUEST -> 1;
            case FRIEND -> 2;
            case KINSMAN -> 3;
        };
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide() && hand == InteractionHand.MAIN_HAND
                && this.isAlive() && !this.isBaby() && !player.isSecondaryUseActive()) {
            Alliance alliance = this.alliance();
            if (FactionStandings.isHostile(player, alliance)) {
                // A country that musters guards against you does not take your coin either.
                this.makeSound(SoundEvents.VILLAGER_NO);
                player.sendOverlayMessage(
                        Component.translatable("faction.tolkienmobs.vendor.refused", alliance.displayName()));
                return InteractionResult.SUCCESS;
            }
            int wanted = this.shelfFor(player);
            if (wanted == 0) {
                this.makeSound(SoundEvents.VILLAGER_NO);
                player.sendOverlayMessage(Component.translatable("faction.tolkienmobs.vendor.unknown",
                        alliance.displayName(), FactionRank.GUEST.threshold()));
                return InteractionResult.SUCCESS;
            }
            if (wanted != this.shelf) {
                // Dropping the offers is how the game is asked to build them again, from updateTrades.
                this.shelf = wanted;
                this.offers = null;
            }
        }
        return super.mobInteract(player, hand);
    }

    /**
     * The shelves this envoy stands behind, deepest first, for whoever the counter is open for.
     * <p>
     * The game calls this from {@code getOffers} while the offers are null, so it fills
     * {@code getOffers()} rather than replacing it, exactly as a villager's own trade table does.
     */
    @Override
    protected void updateTrades(ServerLevel level) {
        MerchantOffers offers = this.getOffers();
        offers.clear();
        for (int tier = 1; tier <= this.shelf; tier++) {
            ResourceKey<TradeSet> shelf = TolkienFactionVendorTrades.shelf(this.alliance(), tier);
            if (shelf != null) this.addOffersFromTradeSet(level, offers, shelf);
        }
    }

    /** An envoy's own side, not the trade it happens to hold: a dwarf without a job is still a dwarf. */
    @Override
    protected Component getTypeName() {
        return Component.translatable(this.getType().getDescriptionId() + "." + this.alliance().id());
    }

    /**
     * Animations: a shopkeeper stands at the counter, so one clip is all he needs.
     */
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<FactionVendorEntity>("Idle", 5, test -> test.setAndContinue(IDLE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
