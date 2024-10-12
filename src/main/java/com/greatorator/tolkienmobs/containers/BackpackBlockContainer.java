package com.greatorator.tolkienmobs.containers;

import com.greatorator.tolkienmobs.block.custom.entity.BackpackBlockEntity;
import com.greatorator.tolkienmobs.containers.handlers.BackpackItemStackHandler;
import com.greatorator.tolkienmobs.init.TolkienContainers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.Optional;

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
    public int BUCKET_SLOTS = 1;
    public static final int COLUMNS = 9;
    public static final int ROWS = 8;

    public BackpackBlockContainer(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public BackpackBlockContainer(int pContainerId, Inventory inv, BlockEntity blockEntity) {
        super(TolkienContainers.BACKPACK_CONTAINER.get(), pContainerId);
        this.tileEntity = ((BackpackBlockEntity) blockEntity);
        this.level = inv.player.level();
        this.player = inv.player;
        this.access = ContainerLevelAccess.create(this.level, this.tileEntity.getBlockPos());
        this.resultSlot = new ResultSlot(this.player, this.craftSlots, this.resultSlots, 0, 19, 151);
        this.fluidData = tileEntity.getFluidContainerData();

        addContainerSlots(this.tileEntity.itemHandler, COLUMNS, ROWS);

        addPlayerInventory(inv, 65, 146);
        addPlayerHotbar(inv, 65, 146);
        addPlayerArmorInventory(inv, 23, 197);
        addCraftingSlots();
        addFluidSlots();
        addDataSlots(fluidData);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex < ROWS * 9) {
                if (!this.moveItemStackTo(itemstack1, ROWS * 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, ROWS * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    void addContainerSlots(IItemHandler itemHandler, int cols, int rows) {
        int slot_index = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = 65 + col * 18;
                int y = 13 + row * 18;

                this.addSlot(new BackpackItemStackHandler(itemHandler, slot_index, x, y));
                slot_index++;
            }
        }
    }

    // Crafting
    private void addCraftingSlots() {
        this.addSlot(this.resultSlot);
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                this.addSlot(new Slot(this.craftSlots, j + i * 3, 2 + j * 18, 94 + i * 18));
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
        return slot.container != this.resultSlots && super.canTakeItemForPickAll(stack, slot);
    }

    private void slotChangedCraftingGrid(AbstractContainerMenu menu, Level level, Player player, CraftingContainer container, ResultContainer result) {
        if (!level.isClientSide && level.getServer() != null) {
            ServerPlayer serverPlayer = (ServerPlayer) player;
            ItemStack stack1 = ItemStack.EMPTY;
            Optional<RecipeHolder<CraftingRecipe>> optional = level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, container.asCraftInput(), level);
            if (optional.isPresent()) {
                RecipeHolder<CraftingRecipe> recipe = optional.get();
                if (result.setRecipeUsed(level, serverPlayer, recipe)) {
                    ItemStack stack2 = recipe.value().assemble(CraftingInput.of(3, 3, this.getItems()), level.registryAccess());
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

        // Fluid Tank
    public void addFluidSlots() {
        fluidHandler = tileEntity.getMachineHandler();
        addSlotRange(fluidHandler, 0, 38, 11, BUCKET_SLOTS, 18);
    }

    public int getFluidAmount() {
        return this.fluidData == null ? 0 : ((this.fluidData.get(2) << 16) | this.fluidData.get(1));
    }

    public Fluid getFluidType() {
        return this.fluidData == null ? Fluids.EMPTY : BuiltInRegistries.FLUID.byId(this.fluidData.get(0));
    }

    public FluidStack getFluidStack() {
        return new FluidStack(getFluidType(), getFluidAmount());
    }
}