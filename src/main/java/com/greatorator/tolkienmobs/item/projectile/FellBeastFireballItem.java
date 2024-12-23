package com.greatorator.tolkienmobs.item.projectile;

import com.greatorator.tolkienmobs.entity.projectiles.FellBeastFireballEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.Random;

public class FellBeastFireballItem extends Item {
   protected final Random random = new Random();

   public FellBeastFireballItem(Properties properties) {
      super(properties);
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level worldIn, Player player, @Nonnull InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      Vec3 pos = player.getLookAngle();
      worldIn.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIREWORK_ROCKET_SHOOT, SoundSource.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
      if (!worldIn.isClientSide) {
         FellBeastFireballEntity fellBeastFireballEntity = new FellBeastFireballEntity(player, worldIn);
         fellBeastFireballEntity.shootFromRotation(player, player.xRotO, player.yRotO, 0.0F, 1.5F, 1.0F);
         worldIn.addFreshEntity(fellBeastFireballEntity);
      }

      player.awardStat(Stats.ITEM_USED.get(this));
      if (!player.getAbilities().instabuild) {
         itemstack.shrink(1);
      }

      return InteractionResultHolder.sidedSuccess(itemstack, worldIn.isClientSide());
   }
}