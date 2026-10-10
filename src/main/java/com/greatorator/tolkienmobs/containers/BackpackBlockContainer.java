package com.greatorator.tolkienmobs.containers;

import com.greatorator.tolkienmobs.block.custom.entity.BackpackBlockEntity;
import com.greatorator.tolkienmobs.containers.handlers.BackpackItemStackHandler;
import com.greatorator.tolkienmobs.containers.handlers.BucketItemHandler;
import com.greatorator.tolkienmobs.containers.handlers.UpgradeItemHandler;
import com.greatorator.tolkienmobs.init.TolkienContainers;
import com.greatorator.tolkienmobs.init.TolkienTags;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.Optional;

/**
 * Every slot the backpack can ever show is added up front, in a fixed order, and each slot's
 * {@link Slot#isActive()} follows the block entity's current upgrades. This lets the open screen
 * grow/shrink as upgrades are added or removed without reopening the menu.
 */
public class BackpackBlockContainer extends TolkienContainer{
    public final BackpackBlockEntity tileEntity;
    private final Level level;
    private final ContainerLevelAccess access;
    private final CraftingContainer craftSlots = new TransientCraftingContainer(this, 3, 3);
    private final ResultContainer resultSlots = new ResultContainer();
    private final ResultSlot resultSlot;
    private final Player player;
    public ItemStackHandler fluidHandler;
    public ContainerData fluidData;
    public static final int COLUMNS = 9;
    public static final int MAX_ROWS = 8;
    public static final int UPGRADE_COLUMNS = 3;
    public static final int UPGRADE_ROWS = 3;

    public static final int STORAGE_START = 0;
    public static final int STORAGE_SLOTS = COLUMNS * MAX_ROWS;
    public static final int UPGRADE_START = STORAGE_START + STORAGE_SLOTS;
    public static final int UPGRADE_SLOTS = UPGRADE_COLUMNS * UPGRADE_ROWS;
    public static final int PLAYER_INV_START = UPGRADE_START + UPGRADE_SLOTS;
    public static final int PLAYER_INV_END = PLAYER_INV_START + 36;
    public static final int ARMOR_START = PLAYER_INV_END;
    public static final int ARMOR_SLOTS = 5;
    public static final int CRAFT_RESULT_SLOT = ARMOR_START + ARMOR_SLOTS;
    public static final int CRAFT_GRID_START = CRAFT_RESULT_SLOT + 1;
    public static final int FLUID_START = CRAFT_GRID_START + 9;
    public static final int FLUID_SLOTS = 2;

    public BackpackBlockContainer(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public BackpackBlockContainer(int pContainerId, Inventory inv, BlockEntity blockEntity) {
        super(TolkienContainers.BACKPACK_CONTAINER.get(), pContainerId);
        this.tileEntity = ((BackpackBlockEntity) blockEntity);
        this.level = inv.player.level();
        this.player = inv.player;
        this.access = ContainerLevelAccess.create(this.level, this.tileEntity.getBlockPos());
        this.resultSlot = new ResultSlot(this.player, this.craftSlots, this.resultSlots, 0, 19, 151) {
            @Override
            public boolean isActive() {
                return hasCrafting();
            }
        };
        this.fluidData = tileEntity.getFluidContainerData();

        addContainerSlots(this.tileEntity.itemHandler, COLUMNS, MAX_ROWS);
        addUpgradeSlots(this.tileEntity.upgradeItemHandler, UPGRADE_COLUMNS, UPGRADE_ROWS);

        addPlayerInventory(inv, 65, 146);
        addPlayerHotbar(inv, 65, 146);
        addPlayerArmorInventory(inv);

        addCraftingSlots();

        addFluidSlots();
        addDataSlots(fluidData);
    }

    private boolean hasCrafting() {
        return this.tileEntity.getBackpackUpgrades().crafting;
    }

    private boolean isUpgradePanelOpen() {
        return this.tileEntity.getBackpackSettings().upgrade;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot slot = this.slots.get(pIndex);
        if (slot == null || !slot.hasItem() || !slot.isActive()) {
            return ItemStack.EMPTY;
        }

        ItemStack stackInSlot = slot.getItem();
        ItemStack original = stackInSlot.copy();
        int storageEnd = STORAGE_START + getStorageSize();

        if (pIndex == CRAFT_RESULT_SLOT) {
            this.access.execute((lvl, pos) -> stackInSlot.getItem().onCraftedBy(stackInSlot, lvl, playerIn));
            if (!moveItemStackTo(stackInSlot, PLAYER_INV_START, PLAYER_INV_END, true)
                    && !moveItemStackTo(stackInSlot, STORAGE_START, storageEnd, false)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(stackInSlot, original);
        } else if (pIndex >= PLAYER_INV_START && pIndex < PLAYER_INV_END) { // Player inventory -> backpack
            boolean moved = false;
            if (isUpgradePanelOpen() && stackInSlot.is(TolkienTags.Items.UPGRADES)) {
                moved = moveItemStackTo(stackInSlot, UPGRADE_START, UPGRADE_START + UPGRADE_SLOTS, false);
            }
            if (!moved && this.tileEntity.hasFluidTank() && stackInSlot.getItem() instanceof BucketItem) {
                moved = moveItemStackTo(stackInSlot, FLUID_START, FLUID_START + FLUID_SLOTS, false);
            }
            if (!moved && !moveItemStackTo(stackInSlot, STORAGE_START, storageEnd, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stackInSlot, PLAYER_INV_START, PLAYER_INV_END, true)) { // Backpack -> player inventory
            return ItemStack.EMPTY;
        }

        if (stackInSlot.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (stackInSlot.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(playerIn, stackInSlot);
        if (pIndex == CRAFT_RESULT_SLOT) {
            playerIn.drop(stackInSlot, false);
        }

        return original;
    }

    public int getStorageSize() {
        return this.tileEntity.getStorageSize();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void broadcastChanges() {
        // Hand back anything left in the crafting grid if the crafting upgrade was removed
        if (!hasCrafting() && !this.craftSlots.isEmpty()) {
            this.access.execute((lvl, pos) -> this.clearContainer(this.player, this.craftSlots));
        }
        super.broadcastChanges();
    }

    @Override
    void addContainerSlots(IItemHandler itemHandler, int cols, int rows) {
        int slot_index = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = 65 + col * 18;
                int y = 13 + row * 18;

                this.addSlot(new BackpackItemStackHandler(itemHandler, slot_index, x, y) {
                    @Override
                    public boolean isActive() {
                        return getContainerSlot() < getStorageSize();
                    }

                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return isActive() && super.mayPlace(stack);
                    }
                });
                slot_index++;
            }
        }
    }

    /** Upgrade Function */
    void addUpgradeSlots(IItemHandler itemHandler, int cols, int rows) {
        int slot_index = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = 237 + col * 18;
                int y = 53 + row * 18;

                this.addSlot(new UpgradeItemHandler(itemHandler, slot_index, x, y) {
                    @Override
                    public boolean isActive() {
                        return isUpgradePanelOpen();
                    }

                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return isActive() && super.mayPlace(stack);
                    }
                });
                slot_index++;
            }
        }
    }

    /** Crafting */
    private void addCraftingSlots() {
        this.addSlot(this.resultSlot);
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                this.addSlot(new Slot(this.craftSlots, j + i * 3, 2 + j * 18, 94 + i * 18) {
                    @Override
                    public boolean isActive() {
                        return hasCrafting();
                    }

                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return isActive() && super.mayPlace(stack);
                    }
                });
            }
        }
    }
    @Override
    public void slotsChanged(Container container) {
        this.access.execute((level, blockPos) -> slotChangedCraftingGrid(this, level, this.player, this.craftSlots, this.resultSlots));
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, blockPos) -> this.clearContainer(player, this.craftSlots));
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.isActive() && slot.container != this.resultSlots && super.canTakeItemForPickAll(stack, slot);
    }

    private void slotChangedCraftingGrid(AbstractContainerMenu menu, Level level, Player player, CraftingContainer container, ResultContainer result) {
        if (!level.isClientSide && level.getServer() != null) {
            ServerPlayer serverPlayer = (ServerPlayer) player;
            ItemStack stack1 = ItemStack.EMPTY;
            Optional<RecipeHolder<CraftingRecipe>> optional = level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, container.asCraftInput(), level);
            if (optional.isPresent()) {
                RecipeHolder<CraftingRecipe> recipe = optional.get();
                if (result.setRecipeUsed(level, serverPlayer, recipe)) {
                    ItemStack stack2 = recipe.value().assemble(container.asCraftInput(), level.registryAccess());
                    if (stack2.isItemEnabled(level.enabledFeatures())) {
                        stack1 = stack2;
                    }
                }
            }
            result.setItem(0, stack1);
            menu.setRemoteSlot(0, stack1);
            serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.incrementStateId(), resultSlot.index, stack1));
        }
    }

    /** Fluid Tank */
    public void addFluidSlots() {
        fluidHandler = tileEntity.getMachineHandler();
        for (int i = 0; i < FLUID_SLOTS; i++) {
            this.addSlot(new BucketItemHandler(fluidHandler, i, 38, i == 0 ? 11 : 58) {
                @Override
                public boolean isActive() {
                    return tileEntity.hasFluidTank();
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return isActive() && super.mayPlace(stack);
                }
            });
        }
    }
}
