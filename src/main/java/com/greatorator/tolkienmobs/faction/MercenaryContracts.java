package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import com.greatorator.tolkienmobs.init.TolkienItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The mercenary work a player carries, and the two moments it moves: a soldier of an alliance dying, and
 * a contract being taken to a board.
 * <p>
 * Progress lives on the contract item rather than in world data, so the paper and the work are one thing.
 * A contract thrown away is work abandoned, which is the point of a contract being an item at all.
 */
@EventBusSubscriber(modid = MODID)
public final class MercenaryContracts {

    private MercenaryContracts() {
    }

    /**
     * A kill moves every contract the killer carries against the dead soldier's alliance. A contract that
     * is already paid for is left alone: it is the board's business now, not the fighting's.
     */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        Alliance dead = soldierOf(event.getEntity());
        if (dead == null) return;

        boolean moved = false;
        var inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            Contract contract = stack.get(TolkienDataComponents.CONTRACT.get());
            if (contract == null || contract.complete() || contract.target() != dead) continue;

            Contract after = contract.advanced();
            stack.set(TolkienDataComponents.CONTRACT.get(), after);
            moved = true;
            player.sendOverlayMessage(after.complete()
                    ? Component.translatable("contract.tolkienmobs.done").withStyle(ChatFormatting.GREEN)
                    : Component.translatable("contract.tolkienmobs.progress",
                            Component.literal(String.valueOf(after.progress())),
                            Component.literal(String.valueOf(after.goal())))
                    .withStyle(ChatFormatting.GRAY));
        }

        if (moved) {
            player.level().playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP,
                    SoundSource.PLAYERS, 0.5F, 1.4F);
        }
    }

    /**
     * What right-clicking a board does: pay out a finished contract, or post a fresh one when the player
     * carries none.
     * <p>
     * A player already carrying work is told so instead of being handed a second contract, because an
     * inventory is where a contract lives and one at a time is what makes it a decision.
     * <p>
     * The board pays any finished contract, whichever alliance posted it. That is the placeholder's
     * simplification: a notice board of the West should only pay its own work, and a real one will.
     */
    public static void useBoard(ServerPlayer player) {
        if (pay(player)) return;

        if (carried(player) != null) {
            player.sendSystemMessage(Component.translatable("contract.tolkienmobs.carried")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }

        Contract contract = Contract.offer(player);
        ItemStack stack = new ItemStack(TolkienItems.MERCENARY_CONTRACT.get());
        stack.set(TolkienDataComponents.CONTRACT.get(), contract);
        // The title of the paper is the work itself, so a pack of contracts can be read without a tooltip.
        stack.set(DataComponents.CUSTOM_NAME, Component.translatable("item.tolkienmobs.mercenary_contract.named",
                        Component.literal(String.valueOf(contract.goal())),
                        contract.target().displayName().copy().withStyle(contract.target().chat()))
                .withStyle(style -> style.withItalic(false)));
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }

        player.sendSystemMessage(Component.translatable("contract.tolkienmobs.taken",
                        contract.employer().displayName(), contract.target().displayName(),
                        Component.literal(String.valueOf(contract.goal())))
                .withStyle(ChatFormatting.WHITE));
        player.level().playSound(null, player.blockPosition(), SoundEvents.VILLAGER_YES,
                SoundSource.PLAYERS, 0.6F, 1.0F);
    }

    /**
     * Pays the first finished contract in the pack: the employer gains its reward and each of its rivals
     * loses half of one, so serving one side is felt by the other two.
     *
     * @return whether a contract was paid, which is what decides if the board posts new work
     */
    private static boolean pay(ServerPlayer player) {
        var inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            Contract contract = stack.get(TolkienDataComponents.CONTRACT.get());
            if (contract == null || !contract.complete()) continue;

            FactionStandings.adjust(player, contract.employer(), contract.reward());
            for (Alliance rival : contract.employer().rivals()) {
                FactionStandings.adjust(player, rival, contract.betrayal());
            }
            stack.shrink(1);

            player.sendSystemMessage(Component.translatable("contract.tolkienmobs.paid",
                            Component.literal(String.valueOf(contract.reward())),
                            contract.employer().displayName())
                    .withStyle(ChatFormatting.GREEN));
            player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP,
                    SoundSource.PLAYERS, 0.7F, 1.2F);
            return true;
        }
        return false;
    }

    /** The first stack in the pack that carries a contract, or null when the player carries none. */
    private static ItemStack carried(ServerPlayer player) {
        var inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.get(TolkienDataComponents.CONTRACT.get()) != null) return stack;
        }
        return null;
    }

    /** The alliance whose soldiers tag names this entity, or null when no alliance claims it. */
    private static Alliance soldierOf(Entity entity) {
        for (Alliance alliance : Alliance.values()) {
            if (entity.getType().builtInRegistryHolder().is(alliance.soldiers())) return alliance;
        }
        return null;
    }
}
