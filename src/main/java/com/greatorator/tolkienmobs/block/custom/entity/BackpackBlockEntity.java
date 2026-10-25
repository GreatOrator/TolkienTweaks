package com.greatorator.tolkienmobs.block.custom.entity;

import com.greatorator.tolkienmobs.block.custom.BackpackBlock;
import com.greatorator.tolkienmobs.block.custom.SleepingBagBlock;
import com.greatorator.tolkienmobs.containers.BackpackBlockContainer;
import com.greatorator.tolkienmobs.handler.capability.TolkienFluidTank;
import com.greatorator.tolkienmobs.handler.capability.TolkienItemStackHandler;
import com.greatorator.tolkienmobs.handler.data.BackpackFluidData;
import com.greatorator.tolkienmobs.handler.interfaces.block.*;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienDataComponents;
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
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class BackpackBlockEntity extends TolkienBlockEntity implements MenuProvider, BackpackFluidUpgradesBlockEntity, BackpackUpgradesBlockEntity, BackpackSettingsBlockEntity, BackpackPlacementBlockEntity, BackpackFluids {
    public final BackpackFluidData backpackFluidData;
    public int BUCKET_SLOTS = 2;
    public BackpackSettings backpackSettings = new BackpackSettings(true, true, true, true);
    public BackpackPlacement backpackPlacement = new BackpackPlacement(false, false);
    public BackpackUpgrades backpackUpgrades = new BackpackUpgrades(false, false, false, false, false, false, false, false);
    public BackpackFluidUpgrades backpackFluidUpgrades = new BackpackFluidUpgrades(false, false, false, false, false);
    private static final Predicate<ItemStack> IS_UPGRADE_ITEM = stack -> !stack.isEmpty() && isUpgradeItem(stack);
    public TolkienItemStackHandler fluidHandler;
    public final TolkienItemStackHandler itemHandler = new TolkienItemStackHandler(72) {
        @Override
        protected void onContentsChanged(int slot, ItemStack stack) {
            contentsChanged();
        }
    };

    public final TolkienItemStackHandler upgradeItemHandler = new TolkienItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot, ItemStack stack) {
            if(level != null && !level.isClientSide()) {
                refreshUpgrades();
            }
            contentsChanged();
        }
    };
    private final TolkienFluidTank FLUID_TANK = createFluidTank();
    private TolkienFluidTank createFluidTank() {
        return new TolkienFluidTank(getMaxMB()) {
            @Override
            protected void onContentsChanged(int slot, FluidStack stack) {
                contentsChanged();
            }
        };
    }

    /** Set when this backpack is opened from a player's hand instead of being placed; changes are saved onto this stack. */
    @Nullable
    private ItemStack heldStack;
    @Nullable
    private InteractionHand heldHand;
    private final ItemStack[] lastHeldBuckets = {ItemStack.EMPTY, ItemStack.EMPTY};
    @Nullable
    private CompoundTag pendingContents;
    private int syncedFluidId;
    private int syncedFluidAmount;
    public static final int SYNCED_STATE_COUNT = 5;

    public BackpackBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(TolkienBlocks.BACKPACK_BLOCK_ENTITY.get(), pPos, pBlockState);
        backpackFluidData = new BackpackFluidData(this);
    }

    /** A backpack that is not in the world, backed by the backpack item in the player's hand. */
    public static BackpackBlockEntity createHeld(Player player, ItemStack stack, @Nullable InteractionHand hand) {
        BackpackBlockEntity backpack = new BackpackBlockEntity(player.blockPosition(), TolkienBlocks.BACKPACK.get().defaultBlockState());
        backpack.setLevel(player.level());
        backpack.heldStack = stack;
        backpack.heldHand = hand;
        CustomData contents = backpack.heldStack.get(TolkienDataComponents.BACKPACK_CONTENTS.get());
        if (contents != null) {
            backpack.loadContents(contents.copyTag(), player.level().registryAccess());
        }
        if (!player.level().isClientSide()) {
            backpack.refreshUpgrades();
        }
        return backpack;
    }

    public boolean isHeld() {
        return heldStack != null;
    }

    @Nullable
    public ItemStack getHeldStack() {
        return heldStack;
    }

    /** The hand holding this backpack, or null when it is worn in the Curios back slot. */
    @Nullable
    public InteractionHand getHeldHand() {
        return heldHand;
    }

    private void contentsChanged() {
        if (level == null || level.isClientSide()) {
            return;
        }
        if (isHeld()) {
            saveToStack();
            return;
        }
        setChanged();
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }

    public void saveToStack() {
        if (heldStack != null && level != null && !level.isClientSide()) {
            heldStack.set(TolkienDataComponents.BACKPACK_CONTENTS.get(), CustomData.of(saveContents(level.registryAccess())));
        }
    }

    /** Called every server tick while a held backpack is open: runs the bucket slots and saves if they changed. */
    public void tickHeld() {
        tickServer();
        TolkienItemStackHandler buckets = getMachineHandler();
        boolean changed = false;
        for (int i = 0; i < lastHeldBuckets.length && i < buckets.size(); i++) {
            ItemStack stack = ItemUtil.getStack(buckets, i);
            if (!ItemStack.matches(stack, lastHeldBuckets[i])) {
                lastHeldBuckets[i] = stack.copy();
                changed = true;
            }
        }
        if (changed) {
            saveToStack();
        }
    }

    /** Everything a backpack carries when it is picked up as an item. */
    public CompoundTag saveContents(HolderLookup.Provider registries) {
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registries);
        itemHandler.serialize(output.child("inventory"));
        upgradeItemHandler.serialize(output.child("upgradeInventory"));
        getMachineHandler().serialize(output.child("bucketInventory"));
        FLUID_TANK.serialize(output);
        this.saveBackpackSettings(output);
        this.saveBackpackUpgrades(output);
        this.saveBackpackFluidUpgrades(output);
        return output.buildResult();
    }

    /** The same contents read back out of the stack's component, which is still a plain tag. */
    private void loadContents(CompoundTag contents, HolderLookup.Provider registries) {
        this.loadContents(TagValueInput.create(ProblemReporter.DISCARDING, registries, contents));
    }

    public void loadContents(ValueInput input) {
        itemHandler.deserialize(input.childOrEmpty("inventory"));
        upgradeItemHandler.deserialize(input.childOrEmpty("upgradeInventory"));
        if (input.child("bucketInventory").isPresent()) {
            getMachineHandler().deserialize(input.childOrEmpty("bucketInventory"));
        }
        FLUID_TANK.deserialize(input);
        this.loadBackpackSettings(input);
        this.loadBackpackUpgrades(input);
        this.loadBackpackFluidUpgrades(input);
        FLUID_TANK.setCapacity(getMaxMB());
    }

    public boolean hasContents() {
        return !FLUID_TANK.isEmpty() || hasItems(itemHandler) || hasItems(upgradeItemHandler) || hasItems(getMachineHandler());
    }

    private static boolean hasItems(ResourceHandler<ItemResource> handler) {
        for (int i = 0; i < handler.size(); i++) {
            if (!handler.getResource(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter input) {
        super.applyImplicitComponents(input);
        CustomData contents = input.get(TolkienDataComponents.BACKPACK_CONTENTS.get());
        if (contents != null) {
            if (level != null) {
                loadContents(contents.copyTag(), level.registryAccess());
            } else {
                pendingContents = contents.copyTag();
            }
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (pendingContents != null && level != null) {
            loadContents(pendingContents, level.registryAccess());
            pendingContents = null;
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        if (level != null && hasContents()) {
            components.set(TolkienDataComponents.BACKPACK_CONTENTS.get(), CustomData.of(saveContents(level.registryAccess())));
        }
    }

    /** Upgrades, settings and the tank, packed into menu data slots (16 bits each) for held backpacks, which get no block entity updates. */
    public int getSyncedState(int index) {
        return switch (index) {
            case 0 -> upgradeBits();
            case 1 -> (backpackSettings.sleepingBag ? 1 : 0) | (backpackSettings.campfire ? 2 : 0) | (backpackSettings.upgrade ? 4 : 0)
                    | (backpackSettings.magnet ? 8 : 0);
            case 2 -> BuiltInRegistries.FLUID.getId(FLUID_TANK.getFluid().getFluid());
            case 3 -> FLUID_TANK.getFluidAmount() & 0xFFFF;
            case 4 -> FLUID_TANK.getFluidAmount() >>> 16;
            default -> 0;
        };
    }

    public void applySyncedState(int index, int value) {
        value &= 0xFFFF;
        switch (index) {
            case 0 -> {
                backpackUpgrades.crafting = (value & 1) != 0;
                backpackUpgrades.sleepingBag = (value & 2) != 0;
                backpackUpgrades.campfire = (value & 4) != 0;
                backpackUpgrades.size_upgrade = (value & 8) != 0;
                backpackUpgrades.size_upgrade_2 = (value & 16) != 0;
                backpackFluidUpgrades.fluid_tank = (value & 32) != 0;
                backpackFluidUpgrades.fluid_tank_2 = (value & 64) != 0;
                backpackFluidUpgrades.fluid_tank_3 = (value & 128) != 0;
                backpackFluidUpgrades.fluid_tank_4 = (value & 256) != 0;
                backpackFluidUpgrades.fluid_tank_5 = (value & 512) != 0;
                backpackUpgrades.magnet_1 = (value & 1024) != 0;
                backpackUpgrades.magnet_2 = (value & 2048) != 0;
                backpackUpgrades.magnet_3 = (value & 4096) != 0;
                FLUID_TANK.setCapacity(getMaxMB());
            }
            case 1 -> {
                backpackSettings.sleepingBag = (value & 1) != 0;
                backpackSettings.campfire = (value & 2) != 0;
                backpackSettings.upgrade = (value & 4) != 0;
                backpackSettings.magnet = (value & 8) != 0;
            }
            case 2 -> syncedFluidId = value;
            case 3 -> syncedFluidAmount = (syncedFluidAmount & 0xFFFF0000) | value;
            case 4 -> syncedFluidAmount = (syncedFluidAmount & 0xFFFF) | (value << 16);
            default -> {
            }
        }
        if (index >= 2) {
            Fluid fluid = BuiltInRegistries.FLUID.byId(syncedFluidId);
            FLUID_TANK.setFluid(fluid == Fluids.EMPTY || syncedFluidAmount <= 0 ? FluidStack.EMPTY : new FluidStack(fluid, syncedFluidAmount));
        }
    }

    private int upgradeBits() {
        return (backpackUpgrades.crafting ? 1 : 0)
                | (backpackUpgrades.sleepingBag ? 2 : 0)
                | (backpackUpgrades.campfire ? 4 : 0)
                | (backpackUpgrades.size_upgrade ? 8 : 0)
                | (backpackUpgrades.size_upgrade_2 ? 16 : 0)
                | (backpackFluidUpgrades.fluid_tank ? 32 : 0)
                | (backpackFluidUpgrades.fluid_tank_2 ? 64 : 0)
                | (backpackFluidUpgrades.fluid_tank_3 ? 128 : 0)
                | (backpackFluidUpgrades.fluid_tank_4 ? 256 : 0)
                | (backpackFluidUpgrades.fluid_tank_5 ? 512 : 0)
                | (backpackUpgrades.magnet_1 ? 1024 : 0)
                | (backpackUpgrades.magnet_2 ? 2048 : 0)
                | (backpackUpgrades.magnet_3 ? 4096 : 0);
    }

    public FluidStack getFluid() {
        return FLUID_TANK.getFluid();
    }

    public TolkienFluidTank getTank(@Nullable Direction direction) {
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
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, pRegistries);
        saveAdditional(output);
        return output.buildResult();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        itemHandler.serialize(output.child("inventory"));
        upgradeItemHandler.serialize(output.child("upgradeInventory"));
        FLUID_TANK.serialize(output);
        this.saveBackpackSettings(output);
        this.saveBackpackUpgrades(output);
        this.saveBackpackFluidUpgrades(output);

        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        itemHandler.deserialize(input.childOrEmpty("inventory"));
        upgradeItemHandler.deserialize(input.childOrEmpty("upgradeInventory"));
        FLUID_TANK.deserialize(input);
        this.loadBackpackSettings(input);
        this.loadBackpackUpgrades(input);
        this.loadBackpackFluidUpgrades(input);
        FLUID_TANK.setCapacity(getMaxMB());
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    /**
     * 26.1 calls this before the block entity goes, which is where the sleeping bag and campfire the
     * pack put in front of itself are taken back out. The old {@code Block#onRemove} did the same
     * thing; the hook it used no longer exists.
     */
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        this.removeDeployables(state.getValue(BackpackBlock.FACING));
        super.preRemoveSideEffects(pos, state);
    }

    /** Upgrade Handling */
    private static boolean isUpgradeItem(ItemStack stack) {
        return stack.is(TolkienTags.Items.UPGRADES);
    }

    /** Re-reads the upgrade slots on the server and syncs the result to clients if anything changed. */
    public boolean refreshUpgrades() {
        BackpackUpgrades upgrades = new BackpackUpgrades();
        BackpackFluidUpgrades fluidUpgrades = new BackpackFluidUpgrades();

        for (int i = 0; i < this.upgradeItemHandler.size(); i++) {
            ItemStack invStack = ItemUtil.getStack(this.upgradeItemHandler, i);
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
            } else if (invStack.is(TolkienItems.ITEM_BACKPACK_UPGRADE_MAGNET_1.get())) {
                upgrades.magnet_1 = true;
            } else if (invStack.is(TolkienItems.ITEM_BACKPACK_UPGRADE_MAGNET_2.get())) {
                upgrades.magnet_2 = true;
            } else if (invStack.is(TolkienItems.ITEM_BACKPACK_UPGRADE_MAGNET_3.get())) {
                upgrades.magnet_3 = true;
            }
        }

        if (upgrades.equals(this.backpackUpgrades) && fluidUpgrades.equals(this.backpackFluidUpgrades)) {
            return false;
        }
        setBackpackUpgrades(upgrades);
        setBackpackFluidUpgrades(fluidUpgrades);
        FLUID_TANK.setCapacity(getMaxMB());
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
        if (level == null || level.isClientSide() || isHeld()) {
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
        if (level == null || level.isClientSide() || isHeld()) {
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
            player.sendOverlayMessage(Component.translatable("screen.tolkienmobs.backpack.no_room"));
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

        if (!isHeld()) {
            pushFluidToAboveNeighbour();
        }
    }

    private void pushFluidToAboveNeighbour() {
        ResourceHandler<FluidResource> above = level.getCapability(Capabilities.Fluid.BLOCK, worldPosition.above(), Direction.DOWN);
        if (above != null) {
            moveFluid(FLUID_TANK, above, Integer.MAX_VALUE);
        }
    }

    private void transferFluidFromTankToHandler() {
        ResourceHandler<FluidResource> itemTank = slotFluidHandler(1);
        if (itemTank != null && moveFluid(FLUID_TANK, itemTank, Integer.MAX_VALUE) > 0) {
            this.level.playSound(null, this.getBlockPos(), SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1F, 1.0F);
        }
    }

    private boolean hasFluidHandlerInSecondSlot() {
        ResourceHandler<FluidResource> itemTank = slotFluidHandler(1);
        return itemTank != null && ResourceHandlerUtil.isEmpty(itemTank);
    }

    private void transferFluidToTank() {
        ResourceHandler<FluidResource> itemTank = slotFluidHandler(0);
        if (itemTank != null && moveFluid(itemTank, FLUID_TANK, Integer.MAX_VALUE) > 0) {
            this.level.playSound(null, this.getBlockPos(), SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1F, 1.0F);
        }
    }

    private boolean hasFluidStackInFirstSlot() {
        ResourceHandler<FluidResource> itemTank = slotFluidHandler(0);
        return itemTank != null && !ResourceHandlerUtil.isEmpty(itemTank);
    }

    /** The fluid handler of the container item in a machine slot, bound to that slot so it writes back. */
    @Nullable
    private ResourceHandler<FluidResource> slotFluidHandler(int slot) {
        return ItemAccess.forHandlerIndex(getMachineHandler(), slot).getCapability(Capabilities.Fluid.ITEM);
    }

    /** Moves fluid between two handlers in one transaction, committing only if something moved. */
    private static int moveFluid(ResourceHandler<FluidResource> source, ResourceHandler<FluidResource> sink, int amount) {
        try (Transaction transaction = Transaction.openRoot()) {
            int moved = ResourceHandlerUtil.move(source, sink, resource -> true, amount, transaction);
            if (moved > 0) {
                transaction.commit();
            }
            return moved;
        }
    }

    public TolkienFluidTank getFluidTank() {
        return getData(TolkienBlocks.BACKPACK_FLUID_HANDLER);
    }

    public void handleItemStack() {
        ResourceHandler<FluidResource> itemTank = slotFluidHandler(0);
        if (itemTank == null) return;
        moveFluid(getFluidTank(), itemTank, 1000);
    }

    public ItemStack getItemStack() {
        return ItemUtil.getStack(getMachineHandler(), 0);
    }

    public TolkienItemStackHandler getMachineHandler() {
        return getData(TolkienBlocks.BACKPACK_HANDLER);
    }

    public void markDirtyClient() {
        if (isHeld()) {
            saveToStack();
            return;
        }
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
        ResourceHandler<FluidResource> itemTank = ItemAccess.forStack(itemStack).getCapability(Capabilities.Fluid.ITEM);
        if (itemTank == null)
            return false;
        try (Transaction transaction = Transaction.openRoot()) {
            int amtFilled = itemTank.insert(FluidResource.of(fluidStack), fluidStack.getAmount(), transaction);
            if (amtFilled == 0)
                return false;
        }
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
