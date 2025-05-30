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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CamoFluidBlockEntity extends TolkienBlockEntity implements MenuProvider {
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

    public List<BlockPos> findAirBlocks(Level world, BlockPos aPos, Direction side) {
        List<BlockPos> positions = new ArrayList<>();
        int xRange = 2;
        int yRange = 1;
        int zRange = 2;

        if (side.getAxis() != Direction.Axis.Y) {
            aPos = aPos.above();
            if (world.isEmptyBlock(aPos)) {
                positions.add(aPos.immutable());
            }
        }

        for (int xPos = aPos.getX() - xRange; xPos <= aPos.getX() + xRange; xPos++) {
            for (int yPos = aPos.getY() - yRange; yPos <= aPos.getY() + yRange; yPos++) {
                for (int zPos = aPos.getZ() - zRange; zPos <= aPos.getZ() + zRange; zPos++) {
                    if (!(aPos.getX() == xPos && aPos.getY() == yPos && aPos.getZ() == zPos)) {
                        BlockPos thePos = new BlockPos(xPos, yPos, zPos);
                        if (world.isEmptyBlock(thePos)) {
                            positions.add(thePos.immutable());
                        }
                    }
                }
            }
        }
     return positions;
    }

    public void onNeighborChange(Level level) {
        ItemStack stack = mainInventory.getStackInSlot(0);
        IFluidHandlerItem fluidHandlerItem = stack.getCapability(Capabilities.FluidHandler.ITEM);

        assert fluidHandlerItem != null;

        if (stack.isEmpty() || fluidHandlerItem.getContainer().isEmpty()) {
            return;
        }

        for (Direction facing : Direction.values()) {
            BlockPos blockPos = worldPosition.relative(facing);
            List<BlockPos> fluidToPlace = findAirBlocks(level, blockPos, facing);

            Fluid fluid = fluidHandlerItem.getFluidInTank(0).getFluid();
            if (fluid == Fluids.EMPTY) {
                return;
            }
            for (BlockPos airPos : fluidToPlace) {
                level.setBlockAndUpdate(airPos, fluid.defaultFluidState().createLegacyBlock());
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
