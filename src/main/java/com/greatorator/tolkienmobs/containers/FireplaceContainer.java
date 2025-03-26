package com.greatorator.tolkienmobs.containers;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.block.custom.entity.FireplaceBlockEntity;
import com.greatorator.tolkienmobs.containers.slots.FireplaceSlot;
import com.greatorator.tolkienmobs.containers.slots.FuelSlot;
import com.greatorator.tolkienmobs.containers.slots.OutputSlot;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienContainers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;

public class FireplaceContainer extends TolkienContainer {
    private final FireplaceBlockEntity tileEntity;
    private final Level level;
    private final ContainerData containerData;
    public static final ResourceLocation TEXTURE_LOC_SLOT_INGREDIENT_1 = TolkienMobsMain.resLoc("item/food");
    public static final ResourceLocation TEXTURE_LOC_SLOT_INGREDIENT_2 = TolkienMobsMain.resLoc("item/food");
    public static final ResourceLocation TEXTURE_LOC_SLOT_FUEL = TolkienMobsMain.resLoc("item/fuel");
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    private static final int TE_INVENTORY_SLOT_COUNT = 4;  // must be the number of slots you have!

    public FireplaceContainer(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(6));
    }

    public FireplaceContainer(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(TolkienContainers.FIREPLACE_CONTAINER.get(), pContainerId);
        checkContainerSize(inv, 6);
        tileEntity = (FireplaceBlockEntity) entity;
        this.level = inv.player.level();
        this.containerData = data;

        addPlayerInventory(inv, -15, 56);
        addPlayerHotbar(inv, -15, 56);

        IItemHandler fuelHandler = tileEntity.getFuelHandler();
        this.addSlot(new FuelSlot(fuelHandler, 0, 34, 55, TEXTURE_LOC_SLOT_FUEL, this));

        IItemHandler itemHandler = tileEntity.getInputHandler();
        this.addSlot(new FireplaceSlot(itemHandler, 0, 23, 15, TEXTURE_LOC_SLOT_INGREDIENT_1, this));
        this.addSlot(new FireplaceSlot(itemHandler, 1, 45, 15, TEXTURE_LOC_SLOT_INGREDIENT_2, this));

        IItemHandler outputHandler = tileEntity.getOutputHandler();
        this.addSlot(new OutputSlot(outputHandler, 0, 91, 35));

        addDataSlots(data);
    }

    public boolean isCrafting() {
        return containerData.get(0) > 0;
    }

    public int getScaledArrowProgress() {
        int progress = this.containerData.get(0);
        int maxProgress = this.containerData.get(1);
        int arrowPixelSize = 18;

        return maxProgress != 0 && progress != 0 ? progress * arrowPixelSize / maxProgress : 0;
    }

    public int getScaledFireplaceProgress() {
        int progress = this.containerData.get(0);
        int maxProgress = this.containerData.get(1);
        int crystalPixelSize = 16;

        return maxProgress != 0 && progress != 0 ? progress * crystalPixelSize / maxProgress : 0;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, tileEntity.getBlockPos()),
                pPlayer, TolkienBlocks.FIREPLACE.get());
    }
}