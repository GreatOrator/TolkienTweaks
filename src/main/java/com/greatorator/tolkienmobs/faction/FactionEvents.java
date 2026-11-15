package com.greatorator.tolkienmobs.faction;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import com.greatorator.tolkienmobs.entity.boss.WitchKingEntity;
import com.greatorator.tolkienmobs.entity.monster.NazgulEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import com.greatorator.tolkienmobs.init.TolkienItems;

/**
 * The three ways a standing moves: killing a soldier of one alliance, handing tokens to a hall's
 * warden, and logging in to be told where you stand. It is also where the guard patrols are woken.
 */
@EventBusSubscriber(modid = MODID)
public final class FactionEvents {

    private FactionEvents() {
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            FactionStandings.sync(player);
            QuestLedger.welcome(player);
            Campaign.remindNext(player);
            QuestLedger.sync(player);
            CourageLedger.sync(player);
        }
    }

    /**
     * A soldier's death moves the two alliances that are not his own upward and his own downward, so
     * clearing a camp is a decision: the West notices an orc killed, and Mordor notices it too.
     */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        Entity victim = event.getEntity();
        QuestLedger.progressKill(player, victim);
        // A cave troll felled at depth is what clears a chamber of the stone's cave search.
        Campaign.onCaveTrollDeath(player, victim);
        // The captain of the Dourhands is stood from a page that stays in the book until it is turned in, so without a
        // mark of his fall the hunt stands him again the moment he is killed. The mark is on the player, like the
        // wraith's, so one hand's kill is not another's and no second captain is stood for the hand that ended him.
        if (victim.entityTags().contains(Campaign.DUGORIM_TAG)) {
            player.addTag(Campaign.DUGORIM_DOWN_TAG);
        }
        // Felling one of the Nine, or their master, is the lesson that the terror can be outlived.
        if (victim instanceof WitchKingEntity) {
            CourageLedger.award(player, CourageLedger.Deed.WITCHKING_FELLED);
        } else if (victim instanceof NazgulEntity) {
            CourageLedger.award(player, CourageLedger.Deed.NAZGUL_FELLED);
            // The one behind the captain: marked on the player, so the stair's hunt cannot stand him again and cannot
            // be paid before he is down.
            if (victim.entityTags().contains(Campaign.KEEP_NAZGUL_TAG)) {
                player.addTag(Campaign.NAZGUL_DOWN_TAG);
            }
        }
        for (Alliance alliance : Alliance.values()) {
            if (!victim.getType().builtInRegistryHolder().is(alliance.soldiers())) continue;
            FactionStandings.adjust(player, alliance, -FactionRank.KILL_VALUE);
            for (Alliance rival : alliance.rivals()) {
                FactionStandings.adjust(player, rival, FactionRank.KILL_VALUE);
            }
            return;
        }
    }

    /**
     * Right-clicking a hall's warden with that alliance's tokens hands over every one of them. The
     * warden is an ordinary NPC carrying the two scoreboard tags the hall's template was placed with.
     */
    @SubscribeEvent
    public static void onEnvoyInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        Entity target = event.getTarget();
        if (!(target instanceof LivingEntity living) || !living.entityTags().contains(Alliance.ENVOY_TAG)) return;

        Alliance alliance = allianceOf(living);
        if (alliance == null) return;
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
        if (player.level().isClientSide()) return;

        int taken = FactionStandings.handIn(player, alliance);
        if (taken == 0) {
            player.sendSystemMessage(Component.translatable("faction.tolkienmobs.no_tokens", alliance.displayName())
                    .withStyle(ChatFormatting.GRAY));
            return;
        }
        FactionRank rank = FactionStandings.rank(player, alliance);
        player.sendSystemMessage(Component.translatable("faction.tolkienmobs.hand_in",
                        taken, alliance.displayName(), FactionStandings.standing(player, alliance), rankName(rank))
                .withStyle(ChatFormatting.WHITE));
        player.level().playSound(null, player.blockPosition(), SoundEvents.VILLAGER_YES, SoundSource.PLAYERS, 0.6F, 1.0F);
    }

    /**
     * The ashen scroll read at the goblin cave's mouth. The cave is cut behind a wall of the hill's own stone
     * until the scroll is read within reach of the door: reading it is what takes the wall down, and the scroll
     * goes to dust for it.
     */
    @SubscribeEvent
    public static void onScrollAtCaveMouth(PlayerInteractEvent.RightClickItem event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!(player.level() instanceof ServerLevel level)) return;
        ItemStack stack = event.getItemStack();
        if (!stack.is(TolkienItems.ITEM_SCROLL.get())) return;
        BlockPos pin = Pins.at(level, GoblinCave.PIN);
        if (pin == null) return;
        if (pin.distToCenterSqr(player.position()) > (double) GoblinCave.SCROLL_REACH * GoblinCave.SCROLL_REACH) return;
        if (!GoblinCave.open(level)) return;
        stack.shrink(1);
        player.sendSystemMessage(Component.translatable(GoblinCave.OPEN_LINE).withStyle(ChatFormatting.GOLD));
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            FactionGuards.tick(player);
            QuestGivers.tick(player);
            Campaign.tick(player);
            // The bodies breathe and turn, and the walk out is stepped, on every tick: the campaign's own
            // tick is once a second, which is too coarse for either.
            Departure.poseTick(player);
            Departure.walkTick(player);
            Places.tick(player);
        }
    }

    /** The alliance whose tag this warden carries, or null if it carries none of them. */
    public static Alliance allianceOf(Entity warden) {
        for (Alliance alliance : Alliance.values()) {
            if (warden.entityTags().contains(alliance.envoyTag())) return alliance;
        }
        return null;
    }

    public static Component rankName(FactionRank rank) {
        return Component.translatable("faction.tolkienmobs.rank." + rank.id()).withStyle(rank.chat());
    }

}
