package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.block.custom.BackpackBlock;
import com.greatorator.tolkienmobs.block.custom.SleepingBagBlock;
import com.greatorator.tolkienmobs.block.custom.entity.BackpackBlockEntity;
import com.greatorator.tolkienmobs.containers.BackpackBlockContainer;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public record BackpackPlacementUpdateManager(
        boolean sleepingBag,
        boolean campfire
) implements CustomPacketPayload {
    public static final Type<BackpackPlacementUpdateManager> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "backpack_placement_packet"));

    @Override
    public Type<BackpackPlacementUpdateManager> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, BackpackPlacementUpdateManager> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, BackpackPlacementUpdateManager::sleepingBag,
                    ByteBufCodecs.BOOL, BackpackPlacementUpdateManager::campfire,
                    BackpackPlacementUpdateManager::new);

    public static void handle(final BackpackPlacementUpdateManager payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player sender = context.player();
            Level level = sender.level();
            AbstractContainerMenu container = sender.containerMenu;

            if (container instanceof BackpackBlockContainer backpackContainer && backpackContainer.tileEntity instanceof BackpackBlockEntity backpackBE) {
                TolkienMobsMain.LOGGER.warn("Current state: Campfire: " + payload.campfire() + ", Sleeping Bag: " + payload.sleepingBag());
                Direction direction = backpackBE.getBlockDirection();
                BlockPos pos = backpackBE.getBlockPos();
                Direction facing = level.getBlockState(pos).getValue(BackpackBlock.FACING);
                BlockPos sleepingBagPos1 = pos.relative(direction);
                BlockPos sleepingBagPos2 = sleepingBagPos1.relative(direction);
                if (!payload.sleepingBag()) {
                    backpackBE.getBackpackPlacement().sleepingBag = !backpackBE.getBackpackPlacement().campfire;
                    level.playSound(null, pos.relative(facing), SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 0.5F, 1.0F);
                    level.setBlockAndUpdate(pos.relative(facing), Blocks.AIR.defaultBlockState());

                    BlockState bagState = TolkienBlocks.SLEEPING_BAG_BLUE.get().defaultBlockState();

                    level.playSound(null, sleepingBagPos2, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.5F, 1.0F);
                    level.setBlock(sleepingBagPos1, bagState.setValue(SleepingBagBlock.FACING, direction).setValue(SleepingBagBlock.PART, BedPart.FOOT).setValue(SleepingBagBlock.CAN_DROP, false), 3);
                    level.setBlock(sleepingBagPos2, bagState.setValue(SleepingBagBlock.FACING, direction).setValue(SleepingBagBlock.PART, BedPart.HEAD).setValue(SleepingBagBlock.CAN_DROP, false), 3);

                    level.updateNeighborsAt(pos, bagState.getBlock());
                    level.updateNeighborsAt(sleepingBagPos2, bagState.getBlock());
                }else if (!payload.campfire()) {
                    backpackBE.getBackpackPlacement().campfire = !backpackBE.getBackpackPlacement().sleepingBag;
                    level.playSound(null, sleepingBagPos2, SoundEvents.WOOL_BREAK, SoundSource.BLOCKS, 0.5F, 1.0F);
                    level.setBlock(sleepingBagPos2, Blocks.AIR.defaultBlockState(), 3);
                    level.setBlock(sleepingBagPos1, Blocks.AIR.defaultBlockState(), 3);

                    level.playSound(null, pos.relative(facing), SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 0.5F, 1.0F);
                    level.setBlock(pos.relative(direction), Blocks.CAMPFIRE.defaultBlockState(), 3);
                    level.updateNeighborsAt(pos, Blocks.CAMPFIRE.defaultBlockState().getBlock());
                }else if (payload.campfire() && payload.sleepingBag()) {
                    level.playSound(null, sleepingBagPos2, SoundEvents.WOOL_BREAK, SoundSource.BLOCKS, 0.5F, 1.0F);
                    level.setBlock(sleepingBagPos2, Blocks.AIR.defaultBlockState(), 3);
                    level.setBlock(sleepingBagPos1, Blocks.AIR.defaultBlockState(), 3);
                    level.playSound(null, pos.relative(facing), SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 0.5F, 1.0F);
                    level.setBlockAndUpdate(pos.relative(facing), Blocks.AIR.defaultBlockState());
                }
            }
        });
    }
}
