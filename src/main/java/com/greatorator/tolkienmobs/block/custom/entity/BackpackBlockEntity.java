package com.greatorator.tolkienmobs.block.custom.entity;

import com.greatorator.tolkienmobs.block.custom.BackpackBlock;
import com.greatorator.tolkienmobs.block.custom.SleepingBagBlock;
import com.greatorator.tolkienmobs.containers.BackpackBlockContainer;
import com.greatorator.tolkienmobs.handler.capability.TolkienFluidTank;
import com.greatorator.tolkienmobs.handler.data.BackpackFluidData;
import com.greatorator.tolkienmobs.handler.interfaces.BackpackFluidUpgradesBlockEntity;
import com.greatorator.tolkienmobs.handler.interfaces.BackpackFluids;
import com.greatorator.tolkienmobs.handler.interfaces.BackpackSettingsBlockEntity;
import com.greatorator.tolkienmobs.handler.interfaces.BackpackUpgradesBlockEntity;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.init.TolkienTags;
import com.greatorator.tolkienmobs.item.custom.UpgradeItem;
import com.greatorator.tolkienmobs.network.BackpackSettingsUpdateManager;
import com.greatorator.tolkienmobs.util.BackpackFluidUpgrades;
import com.greatorator.tolkienmobs.util.BackpackSettings;
import com.greatorator.tolkienmobs.util.BackpackUpgrades;
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
import net.minecraft.world.level.block.Blocks;
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
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class BackpackBlockEntity extends BlockEntity implements MenuProvider, BackpackFluidUpgradesBlockEntity, BackpackUpgradesBlockEntity, BackpackSettingsBlockEntity, BackpackFluids {
    public final BackpackFluidData backpackFluidData;
    public int BUCKET_SLOTS = 1;
    public BackpackSettings backpackSettings = new BackpackSettings(true, true, true);
    public BackpackUpgrades backpackUpgrades = new BackpackUpgrades(false, false, false, false, false);
    public BackpackFluidUpgrades backpackFluidUpgrades = new BackpackFluidUpgrades(false, false, false, false, false);
    private static final Predicate<ItemStack> IS_UPGRADE_ITEM = stack -> !stack.isEmpty() && isUpgradeItem(stack);
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

    public void getUpgradeItems() {
        getUpgradeItems(this.upgradeItemHandler, IS_UPGRADE_ITEM);
    }

    private boolean getUpgradeItems(IItemHandler inv, Predicate<ItemStack> isUpgradeStack) {
        boolean hasUpgrade = false;

        this.backpackUpgrades.campfire = false;
        this.backpackUpgrades.sleepingBag = false;
        this.backpackUpgrades.crafting = false;
        this.backpackUpgrades.size_upgrade = false;
        this.backpackUpgrades.size_upgrade_2 = false;
        this.backpackFluidUpgrades.fluid_tank = false;
        this.backpackFluidUpgrades.fluid_tank_2 = false;
        this.backpackFluidUpgrades.fluid_tank_3 = false;
        this.backpackFluidUpgrades.fluid_tank_4 = false;
        this.backpackFluidUpgrades.fluid_tank_5 = false;
        this.markDirtyClient();
        this.setChanged();
        for (int i = 0; i < 9; i++) {
            inv = this.upgradeItemHandler;
            ItemStack invStack = inv.getStackInSlot(i);
            if (isUpgradeStack.test(invStack) && invStack.getItem() instanceof UpgradeItem) {
                if (invStack.getItem() == TolkienItems.ITEM_BACKPACK_UPGRADE_CAMPFIRE.get()) {
                    this.backpackUpgrades.campfire = true;
                } else if (invStack.getItem() == TolkienItems.ITEM_BACKPACK_UPGRADE_SLEEPING.get()) {
                    this.backpackUpgrades.sleepingBag = true;
                } else if (invStack.getItem() == TolkienItems.ITEM_BACKPACK_UPGRADE_CRAFTING.get()) {
                    this.backpackUpgrades.crafting = true;
                } else if (invStack.getItem() == TolkienItems.ITEM_BACKPACK_UPGRADE_SIZE.get()) {
                    this.backpackUpgrades.size_upgrade = true;
                } else if (invStack.getItem() == TolkienItems.ITEM_BACKPACK_UPGRADE_SIZE_2.get()) {
                    this.backpackUpgrades.size_upgrade_2 = true;
                } else if (invStack.getItem() == TolkienItems.ITEM_BACKPACK_UPGRADE_FLUID.get()) {
                    this.backpackFluidUpgrades.fluid_tank = true;
                } else if (invStack.getItem() == TolkienItems.ITEM_BACKPACK_UPGRADE_FLUID_2.get()) {
                    this.backpackFluidUpgrades.fluid_tank_2 = true;
                } else if (invStack.getItem() == TolkienItems.ITEM_BACKPACK_UPGRADE_FLUID_3.get()) {
                    this.backpackFluidUpgrades.fluid_tank_3 = true;
                } else if (invStack.getItem() == TolkienItems.ITEM_BACKPACK_UPGRADE_FLUID_4.get()) {
                    this.backpackFluidUpgrades.fluid_tank_4 = true;
                } else if (invStack.getItem() == TolkienItems.ITEM_BACKPACK_UPGRADE_FLUID_5.get()) {
                    this.backpackFluidUpgrades.fluid_tank_5 = true;
                }
            }
        }
        return hasUpgrade;
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

    public void tick() {
        handleItemStack();

        if (hasFluidStackInFirstSlot()) {
            transferFluidToTank();
        }

        if(hasFluidHandlerInSecondSlot()) {
            transferFluidFromTankToHandler();
        }

        pushFluidToAboveNeighbour();
    }

    public Direction getBlockDirection() {
        if(level == null || !(level.getBlockState(getBlockPos()).getBlock() instanceof BackpackBlock) || !level.getBlockState(getBlockPos()).hasProperty(BackpackBlock.FACING))
            return Direction.NORTH;
        return level.getBlockState(getBlockPos()).getValue(BackpackBlock.FACING);
    }

    public void deploySleepingbag(Level level, BlockPos pos) {
        Direction direction = this.getBlockDirection();
        BlockPos sleepingBagPos1 = pos.relative(direction);
        BlockPos sleepingBagPos2 = sleepingBagPos1.relative(direction);

        if (!level.isClientSide()) {
            getBackpackSettings().campfire = !getBackpackSettings().sleepingBag;
            PacketDistributor.sendToServer(new BackpackSettingsUpdateManager(backpackSettings.sleepingBag, backpackSettings.campfire, backpackSettings.upgrade));

            setChanged();

            removeCampfire(level, pos);
        }

        BlockState bagState = TolkienBlocks.SLEEPING_BAG_BLUE.get().defaultBlockState();
        level.setBlock(sleepingBagPos1, bagState.setValue(SleepingBagBlock.FACING, direction).setValue(SleepingBagBlock.PART, BedPart.FOOT).setValue(SleepingBagBlock.CAN_DROP, false), 3);
        level.setBlock(sleepingBagPos2, bagState.setValue(SleepingBagBlock.FACING, direction).setValue(SleepingBagBlock.PART, BedPart.HEAD).setValue(SleepingBagBlock.CAN_DROP, false), 3);

        level.updateNeighborsAt(pos, bagState.getBlock());
        level.updateNeighborsAt(sleepingBagPos2, bagState.getBlock());
    }

    public void deployCampfire(Level level, BlockPos pos) {
        Direction direction = this.getBlockDirection();

        if (!level.isClientSide()) {
            getBackpackSettings().sleepingBag = !getBackpackSettings().campfire;
            PacketDistributor.sendToServer(new BackpackSettingsUpdateManager(backpackSettings.sleepingBag, backpackSettings.campfire, backpackSettings.upgrade));

            setChanged();

            removeSleepingbag(level, direction);
        }
        level.setBlock(pos.relative(direction), Blocks.CAMPFIRE.defaultBlockState(), 3);
        level.updateNeighborsAt(pos, Blocks.CAMPFIRE.defaultBlockState().getBlock());
    }

    public void removeSleepingbag(Level level, Direction direction) {
        BlockPos sleepingBagPos1 = this.getBlockPos().relative(direction);
        BlockPos sleepingBagPos2 = sleepingBagPos1.relative(direction);

        level.playSound(null, sleepingBagPos2, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.5F, 1.0F);
        level.setBlock(sleepingBagPos2, Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(sleepingBagPos1, Blocks.AIR.defaultBlockState(), 3);
    }

    public void removeCampfire(Level level, BlockPos pos) {
        Direction facing = level.getBlockState(pos).getValue(BackpackBlock.FACING);

        level.playSound(null, pos.relative(facing), SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 0.5F, 1.0F);
        level.setBlockAndUpdate(pos.relative(facing), Blocks.AIR.defaultBlockState());
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    private void pushFluidToAboveNeighbour() {
        FluidUtil.getFluidHandler(level, worldPosition.above(), null).ifPresent(iFluidHandler -> {
            FluidUtil.tryFluidTransfer(iFluidHandler, this.FLUID_TANK, Integer.MAX_VALUE, true);
        });
    }

    private void transferFluidFromTankToHandler() {
        FluidActionResult result = FluidUtil.tryFillContainer(itemHandler.getStackInSlot(0), this.FLUID_TANK, Integer.MAX_VALUE, null, true);
        if(result.result != ItemStack.EMPTY) {
            itemHandler.setStackInSlot(0, result.result);
        }
    }

    private boolean hasFluidHandlerInSecondSlot() {
        return !itemHandler.getStackInSlot(1).isEmpty()
                && itemHandler.getStackInSlot(1).getCapability(Capabilities.FluidHandler.ITEM, null) != null
                && (itemHandler.getStackInSlot(1).getCapability(Capabilities.FluidHandler.ITEM, null).getFluidInTank(0).isEmpty() ||
                FluidUtil.tryFluidTransfer(itemHandler.getStackInSlot(1).getCapability(Capabilities.FluidHandler.ITEM, null),
                        FLUID_TANK, Integer.MAX_VALUE, false) != FluidStack.EMPTY);
    }

    private void transferFluidToTank() {
        FluidActionResult result = FluidUtil.tryEmptyContainer(itemHandler.getStackInSlot(0), this.FLUID_TANK, Integer.MAX_VALUE, null, true);
        if(result.result != ItemStack.EMPTY) {
            itemHandler.setStackInSlot(0, result.result);
        }
    }

    private boolean hasFluidStackInFirstSlot() {
        return !itemHandler.getStackInSlot(0).isEmpty()
                && itemHandler.getStackInSlot(0).getCapability(Capabilities.FluidHandler.ITEM, null) != null
                && !itemHandler.getStackInSlot(0).getCapability(Capabilities.FluidHandler.ITEM, null).getFluidInTank(0).isEmpty();
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

    public void tickServer() {
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
