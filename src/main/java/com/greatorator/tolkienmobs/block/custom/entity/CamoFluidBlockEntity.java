package com.greatorator.tolkienmobs.block.custom.entity;

import com.greatorator.tolkienmobs.containers.CamoFluidBlockContainer;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CamoFluidBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler mainInventory = new ItemStackHandler(1) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    public CamoFluidBlockEntity(BlockPos pos, BlockState blockState) {
        super(TolkienBlocks.CAMO_FLUID_BLOCK_ENTITY.get(), pos, blockState);
    }

    public void onRightClick(BlockState state, Player player, InteractionHand hand) {
    }

    public void clientTick(BlockPos pos, Level level) {
    }

    public void serverTick(BlockPos pos, Level level) {
        onNeighborChange(level);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.tolkienmobs.block_camo_fluid");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new CamoFluidBlockContainer(pContainerId, pPlayerInventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.put("inventory", mainInventory.serializeNBT(pRegistries));

        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);

        mainInventory.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    public List<BlockPos> findAirBlocks(BlockPos pos) {
        final Set<BlockPos> seen = new HashSet<>(64);
        final List<BlockPos> blockPositions = new ArrayList<>(27);
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        blockPositions.add(pos);
        for (int i = 0; i < blockPositions.size(); i++)
        {
            final BlockPos log = blockPositions.get(i);
            for (int dx = -1; dx <= 1; dx++)
            {
                for (int dy = -1; dy <= 1; dy++)
                {
                    for (int dz = -1; dz <= 1; dz++)
                    {
                        cursor.setWithOffset(log, dx, dy, dz);
                        if (!seen.contains(cursor)) {
                            final BlockPos cursorPos = cursor.immutable();
                            final BlockState cursorState = level.getBlockState(cursorPos);

                            if (cursorState.is(Blocks.AIR)) {
                                blockPositions.add(cursorPos);
                                seen.add(cursorPos);
                            } else {
                                seen.add(cursorPos);
                            }
                        }
                    }
                }
            }
        }

        Collections.reverse(blockPositions);
        return blockPositions;
    }

    public void onNeighborChange(Level level) {
        ItemStack stack = mainInventory.getStackInSlot(0);
        IFluidHandlerItem fluidHandlerItem = stack.getCapability(Capabilities.FluidHandler.ITEM);

        assert fluidHandlerItem != null;

        if (stack.isEmpty() || fluidHandlerItem.getContainer().isEmpty()) {
            return;
        }

        for (Direction facing : Direction.values()) {
//            List<BlockPos> fluidToPlace = new ArrayList<>(27);
//            fluidToPlace.add(worldPosition.relative(facing));
            BlockPos blockPos = worldPosition.relative(facing);
            Fluid fluid = fluidHandlerItem.getFluidInTank(0).getFluid();
            if (fluid == Fluids.EMPTY) {
                return;
            }
            if (level.isEmptyBlock(blockPos)) {
                level.setBlockAndUpdate(blockPos, fluid.defaultFluidState().createLegacyBlock());
            }
        }
    }

    public void markDirtyClient() {
        setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(getBlockPos());
            level.sendBlockUpdated(getBlockPos(), state, state, 3);
        }
    }
}
