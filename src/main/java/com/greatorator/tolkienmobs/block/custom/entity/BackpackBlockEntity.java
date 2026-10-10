package com.greatorator.tolkienmobs.block.custom.entity;

import com.greatorator.tolkienmobs.block.custom.BackpackBlock;
import com.greatorator.tolkienmobs.block.custom.SleepingBagBlock;
import com.greatorator.tolkienmobs.containers.BackpackBlockContainer;
import com.greatorator.tolkienmobs.handler.capability.TolkienFluidTank;
import com.greatorator.tolkienmobs.handler.data.BackpackFluidData;
import com.greatorator.tolkienmobs.handler.interfaces.block.*;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.init.TolkienTags;
import com.greatorator.tolkienmobs.item.custom.UpgradeItem;
import com.greatorator.tolkienmobs.util.block.BackpackFluidUpgrades;
import com.greatorator.tolkienmobs.util.block.BackpackPlacement;
import com.greatorator.tolkienmobs.util.block.BackpackSettings;
import com.greatorator.tolkienmobs.util.block.BackpackUpgrades;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class BackpackBlockEntity extends TolkienBlockEntity implements MenuProvider, BackpackFluidUpgradesBlockEntity, BackpackUpgradesBlockEntity, BackpackSettingsBlockEntity, BackpackPlacementBlockEntity, BackpackFluids {
    public final BackpackFluidData backpackFluidData;
    public int BUCKET_SLOTS = 2;
    public BackpackSettings backpackSettings = new BackpackSettings(true, true, true);
    public BackpackPlacement backpackPlacement = new BackpackPlacement(false, false);
    public BackpackUpgrades backpackUpgrades = new BackpackUpgrades(false, false, false, false, false);
    public BackpackFluidUpgrades backpackFluidUpgrades = new BackpackFluidUpgrades(false, false, false, false, false);
    private static final Predicate<ItemStack> IS_UPGRADE_ITEM = stack -> !stack.isEmpty() && isUpgradeItem(stack);
    public ItemStackHandler fluidHandler;
    public final ItemStackHandler itemHandler = new ItemStackHandler(72) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 64;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    public final ItemStackHandler upgradeItemHandler = new ItemStackHandler(9) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 64;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                refreshUpgrades();
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    private final FluidTank FLUID_TANK = createFluidTank();
    private FluidTank createFluidTank() {
        return new FluidTank(getMaxMB()) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                if(!level.isClientSide()) {
                    level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                }
            }

            @Override
            public boolean isFluidValid(FluidStack stack) {
                return true;
            }
        };
    }

    public BackpackBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(TolkienBlocks.BACKPACK_BLOCK_ENTITY.get(), pPos, pBlockState);
        backpackFluidData = new BackpackFluidData(this);
    }

    public FluidStack getFluid() {
        return FLUID_TANK.getFluid();
    }

    public IFluidHandler getTank(@Nullable Direction direction) {
        return FLUID_TANK;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("screen.tolkienmobs.block.tolkienmobs.backpack");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        refreshUpgrades();
        return new BackpackBlockContainer(pContainerId, pPlayerInventory, this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        this.loadAdditional(tag, lookupProvider);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, pRegistries);
        return tag;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.put("inventory", itemHandler.serializeNBT(pRegistries));
        pTag.put("upgradeInventory", upgradeItemHandler.serializeNBT(pRegistries));
        pTag = FLUID_TANK.writeToNBT(pRegistries, pTag);
        this.saveBackpackSettings(pTag);
        this.saveBackpackUpgrades(pTag);
        this.saveBackpackFluidUpgrades(pTag);

        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);

        itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        upgradeItemHandler.deserializeNBT(pRegistries, pTag.getCompound("upgradeInventory"));
        FLUID_TANK.readFromNBT(pRegistries, pTag);
        this.loadBackpackSettings(pTag);
        this.loadBackpackUpgrades(pTag);
        this.loadBackpackFluidUpgrades(pTag);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider pRegistries) {
        super.onDataPacket(net, pkt, pRegistries);
    }

    /** Upgrade Handling */
    private static boolean isUpgradeItem(ItemStack stack) {
        return stack.is(TolkienTags.Items.UPGRADES);
    }

    /** Re-reads the upgrade slots on the server and syncs the result to clients if anything changed. */
    public boolean refreshUpgrades() {
        BackpackUpgrades upgrades = new BackpackUpgrades();
        BackpackFluidUpgrades fluidUpgrades = new BackpackFluidUpgrades();

        for (int i = 0; i < this.upgradeItemHandler.getSlots(); i++) {
            ItemStack invStack = this.upgradeItemHandler.getStackInSlot(i);
            if (!IS_UPGRADE_ITEM.test(invStack) || !(invStack.getItem() instanceof UpgradeItem)) {
                continue;
            }
            if (invStack.is(TolkienItems.ITEM_BACKPACK_UPGRADE_CAMPFIRE.get())) {
                upgrades.campfire = true;
            } else if (invStack.is(TolkienItems.ITEM_BACKPACK_UPGRADE_SLEEPING.get())) {
                upgrades.sleepingBag = true;
            } else if (invStack.is(TolkienItems.ITEM_BACKPACK_UPGRADE_CRAFTING.get())) {
                upgrades.crafting = true;
            } else if (invStack.is(TolkienItems.ITEM_BACKPACK_UPGRADE_SIZE.get())) {
                upgrades.size_upgrade = true;
            } else if (invStack.is(TolkienItems.ITEM_BACKPACK_UPGRADE_SIZE_2.get())) {
                upgrades.size_upgrade_2 = true;
            } else if (invStack.is(TolkienItems.ITEM_BACKPACK_UPGRADE_FLUID.get())) {
                fluidUpgrades.fluid_tank = true;
            } else if (invStack.is(TolkienItems.ITEM_BACKPACK_UPGRADE_FLUID_2.get())) {
                fluidUpgrades.fluid_tank_2 = true;
            } else if (invStack.is(TolkienItems.ITEM_BACKPACK_UPGRADE_FLUID_3.get())) {
                fluidUpgrades.fluid_tank_3 = true;
            } else if (invStack.is(TolkienItems.ITEM_BACKPACK_UPGRADE_FLUID_4.get())) {
                fluidUpgrades.fluid_tank_4 = true;
            } else if (invStack.is(TolkienItems.ITEM_BACKPACK_UPGRADE_FLUID_5.get())) {
                fluidUpgrades.fluid_tank_5 = true;
            }
        }

        if (upgrades.equals(this.backpackUpgrades) && fluidUpgrades.equals(this.backpackFluidUpgrades)) {
            return false;
        }
        setBackpackUpgrades(upgrades);
        setBackpackFluidUpgrades(fluidUpgrades);
        return true;
    }

    public int getStorageSize() {
        if (this.backpackUpgrades.size_upgrade_2) {
            return 72;
        } else if (this.backpackUpgrades.size_upgrade) {
            return 54;
        }
        return 27;
    }

    public boolean hasFluidTank() {
        return this.backpackFluidUpgrades.fluid_tank
                || this.backpackFluidUpgrades.fluid_tank_2
                || this.backpackFluidUpgrades.fluid_tank_3
                || this.backpackFluidUpgrades.fluid_tank_4
                || this.backpackFluidUpgrades.fluid_tank_5;
    }

    public Direction getBlockDirection() {
        if(level == null || !(level.getBlockState(getBlockPos()).getBlock() instanceof BackpackBlock) || !level.getBlockState(getBlockPos()).hasProperty(BackpackBlock.FACING))
            return Direction.NORTH;
        return level.getBlockState(getBlockPos()).getValue(BackpackBlock.FACING);
    }

    /** Sleeping bag (two blocks in front) and campfire (one block in front) share space, so only one is deployed at a time. */
    public boolean isSleepingBagDeployed(Direction facing) {
        BlockState state = level.getBlockState(getBlockPos().relative(facing));
        return state.getBlock() instanceof SleepingBagBlock && !state.getValue(SleepingBagBlock.CAN_DROP);
    }

    public boolean isCampfireDeployed(Direction facing) {
        return !backpackSettings.campfire && level.getBlockState(getBlockPos().relative(facing)).getBlock() instanceof CampfireBlock;
    }

    /** Called from the screen buttons: removes whatever is deployed, then deploys the requested item unless it was the one just removed. */
    public void toggleDeployable(boolean sleepingBag, Player player) {
        if (level == null || level.isClientSide()) {
            return;
        }
        Direction facing = getBlockDirection();
        boolean bagWasDeployed = isSleepingBagDeployed(facing);
        boolean campfireWasDeployed = isCampfireDeployed(facing);

        removeDeployables(facing);

        if (sleepingBag && !bagWasDeployed && backpackUpgrades.sleepingBag) {
            placeSleepingBag(facing, player);
        } else if (!sleepingBag && !campfireWasDeployed && backpackUpgrades.campfire) {
            placeCampfire(facing, player);
        }
    }

    public void removeDeployables(Direction facing) {
        if (level == null || level.isClientSide()) {
            return;
        }
        BlockPos front = getBlockPos().relative(facing);
        if (isSleepingBagDeployed(facing)) {
            BlockPos head = front.relative(facing);
            level.playSound(null, head, SoundEvents.WOOL_BREAK, SoundSource.BLOCKS, 0.5F, 1.0F);
            if (level.getBlockState(head).getBlock() instanceof SleepingBagBlock) {
                level.setBlock(head, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
            }
            level.setBlock(front, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
        } else if (isCampfireDeployed(facing)) {
            level.playSound(null, front, SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 0.5F, 1.0F);
            level.setBlock(front, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
        }
        setDeployed(false, false);
    }

    private void placeSleepingBag(Direction facing, Player player) {
        BlockPos foot = getBlockPos().relative(facing);
        BlockPos head = foot.relative(facing);
        if (!level.getBlockState(foot).canBeReplaced() || !level.getBlockState(head).canBeReplaced()) {
            notEnoughRoom(player);
            return;
        }
        BlockState bagState = TolkienBlocks.SLEEPING_BAG_BLUE.get().defaultBlockState()
                .setValue(SleepingBagBlock.FACING, facing)
                .setValue(SleepingBagBlock.CAN_DROP, false);
        level.setBlock(foot, bagState.setValue(SleepingBagBlock.PART, BedPart.FOOT), Block.UPDATE_ALL);
        level.setBlock(head, bagState.setValue(SleepingBagBlock.PART, BedPart.HEAD), Block.UPDATE_ALL);
        level.playSound(null, head, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.5F, 1.0F);
        setDeployed(true, false);
    }

    private void placeCampfire(Direction facing, Player player) {
        BlockPos front = getBlockPos().relative(facing);
        if (!level.getBlockState(front).canBeReplaced()) {
            notEnoughRoom(player);
            return;
        }
        level.setBlock(front, Blocks.CAMPFIRE.defaultBlockState().setValue(CampfireBlock.FACING, facing), Block.UPDATE_ALL);
        level.playSound(null, front, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 0.5F, 1.0F);
        setDeployed(false, true);
    }

    private void notEnoughRoom(Player player) {
        if (player != null) {
            player.displayClientMessage(Component.translatable("screen.tolkienmobs.backpack.no_room"), true);
        }
    }

    /** Settings hold the button state (true = packed away); placement holds what is actually deployed. */
    private void setDeployed(boolean sleepingBag, boolean campfire) {
        backpackSettings.sleepingBag = !sleepingBag;
        backpackSettings.campfire = !campfire;
        backpackPlacement.sleepingBag = sleepingBag;
        backpackPlacement.campfire = campfire;
        markDirtyClient();
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    /** Fluid Handling */
    @Override
    public int getMaxMB() {
        if (this.backpackFluidUpgrades.fluid_tank_5) {
            return Integer.MAX_VALUE;
        }else if (this.backpackFluidUpgrades.fluid_tank_4) {
            return 128000;
        }else if (this.backpackFluidUpgrades.fluid_tank_3) {
            return 32000;
        }else if (this.backpackFluidUpgrades.fluid_tank_2) {
            return 8000;
        }
        return 2000;
    }

    @Override
    public ContainerData getFluidContainerData() {
        return backpackFluidData;
    }

    public void tickServer() {
        handleItemStack();

        if (hasFluidStackInFirstSlot()) {
            transferFluidToTank();
        }

        if(hasFluidHandlerInSecondSlot()) {
            transferFluidFromTankToHandler();
        }

        pushFluidToAboveNeighbour();
    }

    private void pushFluidToAboveNeighbour() {
        FluidUtil.getFluidHandler(level, worldPosition.above(), null).ifPresent(iFluidHandler -> {
            FluidUtil.tryFluidTransfer(iFluidHandler, this.FLUID_TANK, Integer.MAX_VALUE, true);
        });
    }

    private void transferFluidFromTankToHandler() {
        fluidHandler = this.getMachineHandler();
        FluidActionResult result = FluidUtil.tryFillContainer(fluidHandler.getStackInSlot(1), this.FLUID_TANK, Integer.MAX_VALUE, null, true);
        if(result.result != ItemStack.EMPTY) {
            fluidHandler.setStackInSlot(1, result.result);
            this.level.playSound(null, this.getBlockPos(), SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1F, 1.0F);
        }
    }

    private boolean hasFluidHandlerInSecondSlot() {
        fluidHandler = this.getMachineHandler();
        return !fluidHandler.getStackInSlot(1).isEmpty()
                && fluidHandler.getStackInSlot(1).getCapability(Capabilities.FluidHandler.ITEM, null) != null
                && fluidHandler.getStackInSlot(1).getCapability(Capabilities.FluidHandler.ITEM, null).getFluidInTank(0).isEmpty();
    }

    private void transferFluidToTank() {
        fluidHandler = this.getMachineHandler();
        FluidActionResult result = FluidUtil.tryEmptyContainer(fluidHandler.getStackInSlot(0), this.FLUID_TANK, Integer.MAX_VALUE, null, true);
        if(result.result != ItemStack.EMPTY) {
            fluidHandler.setStackInSlot(0, result.result);
            this.level.playSound(null, this.getBlockPos(), SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1F, 1.0F);
        }
    }

    private boolean hasFluidStackInFirstSlot() {
        fluidHandler = this.getMachineHandler();
        return !fluidHandler.getStackInSlot(0).isEmpty()
                && fluidHandler.getStackInSlot(0).getCapability(Capabilities.FluidHandler.ITEM, null) != null
                && !fluidHandler.getStackInSlot(0).getCapability(Capabilities.FluidHandler.ITEM, null).getFluidInTank(0).isEmpty();
    }

    public TolkienFluidTank getFluidTank() {
        return getData(TolkienBlocks.BACKPACK_FLUID_HANDLER);
    }

    public void handleItemStack() {
        FluidStack fluidStack = getFluidStack();
        if (fluidStack.isEmpty()) return;
        ItemStack itemStack = getItemStack();
        if (!isStackValid(itemStack, fluidStack)) return;
        IFluidHandlerItem fluidHandlerItem = itemStack.getCapability(Capabilities.FluidHandler.ITEM);
        int insertAmt = fluidHandlerItem.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE);
        if (insertAmt > 0) {
            FluidStack extractedStack = getFluidTank().drain(Math.min(insertAmt, 1000), IFluidHandler.FluidAction.EXECUTE);
            fluidHandlerItem.fill(extractedStack, IFluidHandler.FluidAction.EXECUTE);
            if (itemStack.getItem() instanceof BucketItem)
                getMachineHandler().setStackInSlot(0, fluidHandlerItem.getContainer());

        }
    }

    public ItemStack getItemStack() {
        return getMachineHandler().getStackInSlot(0);
    }

    public ItemStackHandler getMachineHandler() {
        return getData(TolkienBlocks.BACKPACK_HANDLER);
    }

    public void markDirtyClient() {
        setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(getBlockPos());
            level.sendBlockUpdated(getBlockPos(), state, state, 3);
        }
    }

    public boolean isStackValid(ItemStack itemStack, FluidStack fluidStack) {
        if (itemStack.isEmpty())
            return false;
        if (fluidStack.isEmpty())
            return false;
        IFluidHandlerItem fluidHandlerItem = itemStack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandlerItem == null)
            return false;
        int amtFilled = fluidHandlerItem.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE);
        if (amtFilled == 0)
            return false;
        return true;
    }

    @Override
    public BackpackPlacement getBackpackPlacement() {
        return backpackPlacement;
    }

    @Override
    public BlockEntity getBlockEntity() {
        return this;
    }

    @Override
    public BackpackSettings getBackpackSettings() {
        return backpackSettings;
    }

    @Override
    public BackpackUpgrades getBackpackUpgrades() {
        return backpackUpgrades;
    }

    @Override
    public BackpackFluidUpgrades getBackpackFluidUpgrades() {
        return backpackFluidUpgrades;
    }

    /** Unused Settings */
    public void drops() {
    }

    public void tick() {
    }

    public void tickClient() {
    }

//    public static BlockState getProperSleepingBag(int sleepingBagColor) {
//        return switch(sleepingBagColor) {
//            case 0 -> ModBlocks.WHITE_SLEEPING_BAG.get().defaultBlockState();
//            case 1 -> ModBlocks.ORANGE_SLEEPING_BAG.get().defaultBlockState();
//            case 2 -> ModBlocks.MAGENTA_SLEEPING_BAG.get().defaultBlockState();
//            case 3 -> ModBlocks.LIGHT_BLUE_SLEEPING_BAG.get().defaultBlockState();
//            case 4 -> ModBlocks.YELLOW_SLEEPING_BAG.get().defaultBlockState();
//            case 5 -> ModBlocks.LIME_SLEEPING_BAG.get().defaultBlockState();
//            case 6 -> ModBlocks.PINK_SLEEPING_BAG.get().defaultBlockState();
//            case 7 -> ModBlocks.GRAY_SLEEPING_BAG.get().defaultBlockState();
//            case 8 -> ModBlocks.LIGHT_GRAY_SLEEPING_BAG.get().defaultBlockState();
//            case 9 -> ModBlocks.CYAN_SLEEPING_BAG.get().defaultBlockState();
//            case 10 -> ModBlocks.PURPLE_SLEEPING_BAG.get().defaultBlockState();
//            case 11 -> ModBlocks.BLUE_SLEEPING_BAG.get().defaultBlockState();
//            case 12 -> ModBlocks.BROWN_SLEEPING_BAG.get().defaultBlockState();
//            case 13 -> ModBlocks.GREEN_SLEEPING_BAG.get().defaultBlockState();
//            case 14 -> ModBlocks.RED_SLEEPING_BAG.get().defaultBlockState();
//            case 15 -> ModBlocks.BLACK_SLEEPING_BAG.get().defaultBlockState();
//            default -> ModBlocks.RED_SLEEPING_BAG.get().defaultBlockState();
//        };
//    }
}
