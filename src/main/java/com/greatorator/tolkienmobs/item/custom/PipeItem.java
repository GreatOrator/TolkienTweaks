package com.greatorator.tolkienmobs.item.custom;

import com.greatorator.tolkienmobs.TolkienMobsConfig;
import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import com.greatorator.tolkienmobs.item.TolkienItem;
import com.greatorator.tolkienmobs.util.pipeweed.PipeSmoke;
import com.greatorator.tolkienmobs.util.pipeweed.PipeweedStrain;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

/**
 * A Shire pipe. Tapping it with pipe-weed in the pack loads the bowl: one leaf leaves the pack and
 * stays in the pipe, and the bowl is drawn in that strain's colour from then on. Holding the use
 * button draws on a loaded pipe, and letting go after a good draw spends the leaf that is in it -
 * the pipe blows that strain's shape into the air and leaves the smoker with its small comfort -
 * and the bowl is empty again. Letting go early smokes nothing and leaves the bowl loaded, which is
 * what makes a tap a loading gesture and a hold a smoke. Sneaking and using tips the bowl out and
 * puts the leaf back in the pack.
 *
 * <p>The bowl is a component rather than a reading of the pack, so what the pipe shows is what it
 * will actually burn: a pipe lying in a chest no longer takes the colour of whoever happens to look
 * at it, and a smoker who picks up a different strain still has the old one in the pipe until they
 * tip it out.
 */
public class PipeItem extends TolkienItem {
    public PipeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        PipeweedStrain bowl = bowlOf(stack);

        // A bowl is tipped out rather than silently replaced, so a smoker who has changed their mind
        // about a strain gets the leaf back instead of losing it to the next one they pick up.
        if (player.isShiftKeyDown() && bowl != null) {
            if (!level.isClientSide()) {
                clearBowl(stack);
                ItemStack leaf = new ItemStack(bowl.leafItem());
                if (!player.getInventory().add(leaf)) {
                    player.drop(leaf, false);
                }
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BUNDLE_REMOVE_ONE, SoundSource.PLAYERS, 0.7F, 0.9F);
                player.sendSystemMessage(Component.translatable(getDescriptionId() + ".tip",
                        Component.translatable(bowl.leafItem().getDescriptionId())));
            }
            return InteractionResult.SUCCESS;
        }

        if (bowl == null) {
            PipeweedStrain strain = PipeweedStrain.find(player);
            if (strain == null) {
                if (!level.isClientSide()) {
                    player.sendSystemMessage(Component.translatable(getDescriptionId() + ".empty"));
                }
                return InteractionResult.FAIL;
            }
            if (!level.isClientSide()) {
                PipeweedStrain.consume(player, strain);
                setBowl(stack, strain);
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BUNDLE_INSERT, SoundSource.PLAYERS, 0.7F, 0.8F);
                player.sendSystemMessage(Component.translatable(getDescriptionId() + ".pack",
                        Component.translatable(strain.leafItem().getDescriptionId())));
            }
        }

        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return TolkienMobsConfig.pipeDrawTicks();
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.SPYGLASS;
    }

    /**
     * A thin thread of smoke while the pipe is being drawn on, so the smoker sees it catch. It is
     * drawn in the colour of the leaf in the bowl, which is the leaf this draw will burn.
     */
    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remaining) {
        if (!level.isClientSide() || remaining % 4 != 0) {
            return;
        }
        var look = entity.getLookAngle();
        double x = entity.getX() + look.x * 0.45;
        double y = entity.getEyeY() - 0.1 + look.y * 0.45;
        double z = entity.getZ() + look.z * 0.45;
        PipeweedStrain strain = bowlOf(stack);
        if (strain == null) {
            level.addParticle(ParticleTypes.SMOKE, x, y, z, look.x * 0.01, 0.01, look.z * 0.01);
            return;
        }
        level.addParticle(PipeSmoke.smoke(strain.smoke()), x, y, z, look.x * 0.01, 0.01, look.z * 0.01);
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) {
            return false;
        }
        PipeweedStrain strain = bowlOf(stack);
        if (strain == null) {
            return false;
        }
        if (getUseDuration(stack, entity) - timeLeft < TolkienMobsConfig.pipeMinDrawTicks()) {
            return false;
        }

        if (!level.isClientSide()) {
            clearBowl(stack);
            player.addEffect(new MobEffectInstance(strain.effect(), strain.duration(), strain.amplifier()));
            if (level instanceof ServerLevel serverLevel) {
                PipeSmoke.blow(serverLevel, player, strain);
            }
            player.awardStat(Stats.ITEM_USED.get(this));
            player.getCooldowns().addCooldown(stack, TolkienMobsConfig.pipeCooldownTicks());
            player.sendSystemMessage(Component.translatable(getDescriptionId() + ".smoke",
                    Component.translatable(strain.leafItem().getDescriptionId())));
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.CANDLE_EXTINGUISH, SoundSource.PLAYERS, 0.8F, 1.1F + level.getRandom().nextFloat() * 0.2F);
        return true;
    }

    /** The leaf in the bowl, above the pipe's own lore, so a pipe says what it is holding. */
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, display, tooltipComponents, tooltipFlag);
        PipeweedStrain bowl = bowlOf(stack);
        tooltipComponents.accept(bowl == null
                ? Component.translatable(getDescriptionId() + ".bowl_empty").withStyle(ChatFormatting.DARK_GRAY)
                : Component.translatable(getDescriptionId() + ".bowl",
                        Component.translatable(bowl.leafItem().getDescriptionId())).withStyle(ChatFormatting.GRAY));
    }

    /** The strain in this pipe's bowl, or null when the bowl is empty. */
    public static PipeweedStrain bowlOf(ItemStack stack) {
        String id = stack.get(TolkienDataComponents.PIPE_BOWL);
        return id == null ? null : PipeweedStrain.byId(id);
    }

    public static void setBowl(ItemStack stack, PipeweedStrain strain) {
        stack.set(TolkienDataComponents.PIPE_BOWL, strain.id());
    }

    public static void clearBowl(ItemStack stack) {
        stack.remove(TolkienDataComponents.PIPE_BOWL);
    }
}
