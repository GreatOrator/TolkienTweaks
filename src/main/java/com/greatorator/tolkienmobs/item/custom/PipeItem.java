package com.greatorator.tolkienmobs.item.custom;

import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import com.greatorator.tolkienmobs.item.TolkienItem;
import com.greatorator.tolkienmobs.util.pipeweed.PipeSmoke;
import com.greatorator.tolkienmobs.util.pipeweed.PipeweedStrain;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

/**
 * A Shire pipe. Holding the use button draws on it, and letting go after a good draw spends one leaf
 * of whichever strain the smoker is carrying: the pipe blows that strain's shape into the air and
 * leaves them with its small comfort. Releasing too early spends nothing.
 *
 * <p>A pipe carries no leaf of its own, so its bowl is drawn in the colour of the strain the smoker
 * is carrying. That colour is wrong the moment a draw is spent, because the bowl is empty and the
 * strain is still in the pack, so the pipe keeps a spent flag: drawing on it packs the bowl again and
 * clears the flag, and a finished draw sets it, which leaves the bowl showing ash until then.
 */
public class PipeItem extends TolkienItem {
    private static final int DRAW_TICKS = 40;
    private static final int MIN_DRAW_TICKS = 25;
    private static final int COOLDOWN_TICKS = 20;

    public PipeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (PipeweedStrain.find(player) == null) {
            if (!level.isClientSide()) {
                player.sendSystemMessage(Component.translatable(getDescriptionId() + ".empty"));
            }
            return InteractionResult.FAIL;
        }
        // Packing the bowl: a pipe that was smoked out shows the leaf again from the moment it is
        // drawn on, whether or not the draw is finished.
        setSpent(player.getItemInHand(hand), false);
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return DRAW_TICKS;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.SPYGLASS;
    }

    /**
     * A thin thread of smoke while the pipe is being drawn on, so the smoker sees it catch. It is drawn
     * in the colour of the leaf being smoked, since the strain is known before the draw is released.
     */
    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remaining) {
        if (!level.isClientSide() || remaining % 6 != 0) {
            return;
        }
        var look = entity.getLookAngle();
        double x = entity.getX() + look.x * 0.45;
        double y = entity.getEyeY() - 0.1 + look.y * 0.45;
        double z = entity.getZ() + look.z * 0.45;
        PipeweedStrain strain = entity instanceof Player player ? PipeweedStrain.find(player) : null;
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
        if (getUseDuration(stack, entity) - timeLeft < MIN_DRAW_TICKS) {
            return false;
        }
        PipeweedStrain strain = PipeweedStrain.find(player);
        if (strain == null) {
            return false;
        }

        if (!level.isClientSide()) {
            PipeweedStrain.consume(player, strain);
            setSpent(stack, true);
            player.addEffect(new MobEffectInstance(strain.effect(), strain.duration(), strain.amplifier()));
            if (level instanceof ServerLevel serverLevel) {
                PipeSmoke.blow(serverLevel, player, strain);
            }
            player.awardStat(Stats.ITEM_USED.get(this));
            player.getCooldowns().addCooldown(stack, COOLDOWN_TICKS);
            player.sendSystemMessage(Component.translatable(getDescriptionId() + ".smoke",
                    Component.translatable(strain.leafItem().getDescriptionId())));
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.CANDLE_EXTINGUISH, SoundSource.PLAYERS, 0.8F, 1.1F + level.getRandom().nextFloat() * 0.2F);
        return true;
    }

    /** Whether this pipe's bowl has been smoked since it was last packed. */
    public static boolean isSpent(ItemStack stack) {
        return stack.getOrDefault(TolkienDataComponents.PIPE_SPENT, false);
    }

    /**
     * Records the bowl as smoked out, or as packed again. The flag is dropped rather than written as
     * false, so an unspent pipe carries no component at all and stacks with any other it can.
     */
    public static void setSpent(ItemStack stack, boolean spent) {
        if (spent) {
            stack.set(TolkienDataComponents.PIPE_SPENT, true);
        } else {
            stack.remove(TolkienDataComponents.PIPE_SPENT);
        }
    }
}
