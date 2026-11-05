package com.greatorator.tolkienmobs.event;

import com.greatorator.tolkienmobs.block.custom.SleepingBagBlock;
import com.greatorator.tolkienmobs.init.TolkienBiomes;
import com.greatorator.tolkienmobs.init.TolkienMobEffects;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.init.TolkienPotions;
import com.greatorator.tolkienmobs.init.TolkienRecipesTypes;
import com.greatorator.tolkienmobs.item.custom.DwarvenHammerItem;
import com.greatorator.tolkienmobs.util.block.BackpackMagnet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.player.PlayerSetSpawnEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashSet;
import java.util.Set;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

@EventBusSubscriber(modid = MODID)
public class TolkienServerEvents {
    private static final Set<BlockPos> HARVESTED_BLOCKS = new HashSet<>();

    /**
     * The backpack's magnet gathers drops around a player several times a second. It runs on the
     * server only: the drop is taken out of the world there, and the backpack it fills is the stack
     * the player is carrying.
     */
    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            BackpackMagnet.tick(player);
            marshTick(player);
        }
    }

    /**
     * The Dead Marshes are not quiet. Three things happen to whoever walks them, on three different clocks:
     * a dread of the place that takes the speed from the legs and the strength from the arm, the lights the
     * dead are said to burn over their pools, and every so often a cry from somewhere out in the water.
     * <p>
     * All three are driven from the player's own tick because none of them is a property of a block: the
     * dread lasts only while you stand in it, the lights sit on water the player happens to be near, and the
     * cry comes from a point picked out in the dark. Creative and spectator players are left out of it - a
     * builder flying over the marsh is not being haunted.
     */
    private static void marshTick(ServerPlayer player) {
        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        if (!player.level().getBiome(player.blockPosition()).is(TolkienBiomes.DEAD_MARSHES)) {
            return;
        }

        if (player.tickCount % 20 == 0) {
            player.addEffect(new MobEffectInstance(TolkienMobEffects.MARSH_DREAD, 60, 0, true, false, true));
        }

        if (player.tickCount % 10 == 0) {
            corpseLights(player);
        }

        if (player.tickCount % 300 == 0 && player.getRandom().nextInt(3) == 0) {
            faintCry(player);
        }
    }

    /**
     * A light over a pool: the marsh's own flame, standing on the water a little way from the player rather
     * than on him. Six water surfaces are tried and the first one found takes the light, so a player deep in
     * a dry stretch of the marsh sees nothing - the lights belong to the pools.
     */
    private static void corpseLights(ServerPlayer player) {
        ServerLevel level = (ServerLevel) player.level();
        RandomSource random = player.getRandom();
        BlockPos origin = player.blockPosition();

        for (int attempt = 0; attempt < 6; attempt++) {
            BlockPos pos = origin.offset(random.nextInt(41) - 20, random.nextInt(17) - 8, random.nextInt(41) - 20);

            if (level.getFluidState(pos).is(Fluids.WATER) && level.getBlockState(pos.above()).isAir()) {
                double x = pos.getX() + 0.5D;
                double y = pos.getY() + 1.05D;
                double z = pos.getZ() + 0.5D;

                level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 1, 0.05D, 0.02D, 0.05D, 0.0D);
                level.sendParticles(ParticleTypes.SOUL, x, y + 0.1D, z, 2, 0.12D, 0.08D, 0.12D, 0.005D);
                return;
            }
        }
    }

    /**
     * A cry out in the marsh, from a point far enough away that it is a sound and not a warning: the volume
     * is what carries it (a sound is heard out to about sixteen times its volume), so it is played loud and
     * low rather than close and loud. The cry can be thirty blocks out, so the volume has to reach past
     * that or the furthest calls would be sent to nobody.
     */
    private static void faintCry(ServerPlayer player) {
        ServerLevel level = (ServerLevel) player.level();
        RandomSource random = player.getRandom();
        double angle = random.nextDouble() * Math.PI * 2.0D;
        double distance = 14.0D + random.nextDouble() * 16.0D;
        double x = player.getX() + Math.cos(angle) * distance;
        double z = player.getZ() + Math.sin(angle) * distance;

        level.playSound(null, x, player.getY() + 1.0D, z, TolkienSounds.IDLE_BARROW.get(),
                SoundSource.AMBIENT, 2.4F, 0.5F + random.nextFloat() * 0.2F);
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        event.sendRecipes(TolkienRecipesTypes.FIREPLACE_TYPE.get());
    }

    @SubscribeEvent
    public static void onBrewingRecipeRegister(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        builder.addMix(Potions.AWKWARD, TolkienItems.PIPEWEED_ITEM.get(), TolkienPotions.ENT_DRAUGHT);
        builder.addMix(Potions.AWKWARD, TolkienItems.INGOT_MITHRIL.get(), TolkienPotions.PORTABLE_REPAIR);
        builder.addMix(Potions.AWKWARD, TolkienItems.GEM_AMMOLITE.get(), TolkienPotions.ERU_BLESSING);
        builder.addMix(Potions.AWKWARD, TolkienItems.GOLDEN_TREE_ACORN.get(), TolkienPotions.ELF_NIMBLENESS);
        builder.addMix(Potions.AWKWARD, TolkienItems.LEMBAS.get(), TolkienPotions.ELF_VITALITY);
    }

    @SubscribeEvent
    public static void playerSetSpawn(PlayerSetSpawnEvent event)
    {
        Level level = event.getEntity().level();

        if(event.getNewSpawn() != null)
        {
            Block block = level.getBlockState(event.getNewSpawn()).getBlock();

            if(!level.isClientSide() && block instanceof SleepingBagBlock && !event.isForced())
            {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onHammerUsage(BreakBlockEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHandItem = player.getMainHandItem();

        if(mainHandItem.getItem() instanceof DwarvenHammerItem hammer && player instanceof ServerPlayer serverPlayer) {
            BlockPos initialBlockPos = event.getPos();
            if(HARVESTED_BLOCKS.contains(initialBlockPos)) {
                return;
            }

            for(BlockPos pos : DwarvenHammerItem.getBlocksToBeDestroyed(1, initialBlockPos, serverPlayer)) {
                if(pos == initialBlockPos || !hammer.isCorrectToolForDrops(mainHandItem, event.getLevel().getBlockState(pos))) {
                    continue;
                }

                HARVESTED_BLOCKS.add(pos);
                serverPlayer.gameMode.destroyBlock(pos);
                HARVESTED_BLOCKS.remove(pos);
            }
        }
    }
}
