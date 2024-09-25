package com.greatorator.tolkienmobs.containers;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.block.custom.entity.TrinketTableEntity;
import com.greatorator.tolkienmobs.containers.slots.GemSlot;
import com.greatorator.tolkienmobs.containers.slots.PotionSlot;
import com.greatorator.tolkienmobs.containers.slots.TrinketSlot;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienContainers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TrinketTableContainer extends TolkienContainer {
    private TrinketTableEntity tileEntity;
    private final ItemStackHandler trinketHandler;
    private final ContainerData containerData;
    private final ContainerLevelAccess containerAccess;
    public static final ResourceLocation TEXTURE_LOC_SLOT_TRINKET = TolkienMobsMain.resLoc("textures/gui/trinkettable/ring.png");
    public static final ResourceLocation TEXTURE_LOC_SLOT_POTION = TolkienMobsMain.resLoc("textures/gui/trinkettable/potion.png");
    public static final ResourceLocation TEXTURE_LOC_SLOT_GEM = TolkienMobsMain.resLoc("textures/gui/trinkettable/gem.png");
    public static final ResourceLocation TEXTURE_LOC_SLOT_OUTPUT = TolkienMobsMain.resLoc("textures/gui/slot_base.png");
    public static final int SLOTS = 4;
    private final NonNullList<ItemStack> contents = NonNullList.withSize(4, ItemStack.EMPTY);

    public TrinketTableContainer(int windowId, Inventory playerInventory, FriendlyByteBuf extraData) {
        super(TolkienContainers.TRINKET_TABLE_CONTAINER.get(), windowId);
        BlockPos pos = extraData.readBlockPos();
        trinketHandler = new ItemStackHandler(4);
        this.tileEntity = (TrinketTableEntity) playerInventory.player.level().getBlockEntity(pos);
        this.containerData = new SimpleContainerData(4);
        this.containerAccess = ContainerLevelAccess.NULL;

        addTrinketSlots();
        addPlayerSlots(playerInventory, -15, 56);
        addDataSlots(containerData);
    }

    public TrinketTableContainer(int windowId, Inventory playerInventory, TrinketTableEntity tileEntity, ContainerLevelAccess access) {
        super(TolkienContainers.TRINKET_TABLE_CONTAINER.get(), windowId);
        trinketHandler = new ItemStackHandler(4);
        this.tileEntity = tileEntity;
        this.containerData = tileEntity.getContainerData();
        this.containerAccess = access;

        addTrinketSlots();
        addPlayerSlots(playerInventory, -15, 56);
        addDataSlots(containerData);
    }
        @Override
    public boolean stillValid(Player playerIn) {
            return AbstractContainerMenu.stillValid(containerAccess, playerIn, TolkienBlocks.TRINKET_TABLE.get());
    }

    public ItemStack getItem(int index) {
        return contents.get(index);
    }

    private void addTrinketSlots() {
            addSlot(new TrinketSlot(trinketHandler, 0, 23, 15)).setBackground(InventoryMenu.BLOCK_ATLAS, TEXTURE_LOC_SLOT_TRINKET);
            addSlot(new PotionSlot(trinketHandler, 1, 45, 15)).setBackground(InventoryMenu.BLOCK_ATLAS, TEXTURE_LOC_SLOT_POTION);
            addSlot(new GemSlot(trinketHandler, 2, 34, 55)).setBackground(InventoryMenu.BLOCK_ATLAS, TEXTURE_LOC_SLOT_GEM);
            addSlot(new TrinketSlot(trinketHandler, 3, 91, 35)).setBackground(InventoryMenu.BLOCK_ATLAS, TEXTURE_LOC_SLOT_OUTPUT);
    }

    @Override
    @Nonnull
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack currentStack = slot.getItem();
            itemstack = currentStack.copy();

            if (index < SLOTS) {
                if (!this.moveItemStackTo(currentStack, SLOTS, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(currentStack, 0, SLOTS, false)) {
                return ItemStack.EMPTY;
            }

            if (currentStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    public TrinketTableEntity getTe() {
        return tileEntity;
    }

    public int getContainerSize() {
        return 4;
    }

    public int getWork()
    {
        return this.containerData.get(0);
    }

    public int getMaxWork()
    {
        return this.containerData.get(1);
    }
}
