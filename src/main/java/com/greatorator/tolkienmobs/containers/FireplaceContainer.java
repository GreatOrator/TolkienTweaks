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
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class FireplaceContainer extends TolkienContainer implements RecipeInput {
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
    public static final int INGREDIENT_1_SLOT_ID = 0;
    public static final int INGREDIENT_2_SLOT_ID = 1;
    public static final int FUEL_SLOT_ID = 2;
    public static final int RESULT_SLOT_ID = 4;

    public FireplaceContainer(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    public FireplaceContainer(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(TolkienContainers.FIREPLACE_CONTAINER.get(), pContainerId);
        tileEntity = ((FireplaceBlockEntity) entity);
        this.level = inv.player.level();
        this.containerData = data;

        addPlayerInventory(inv, -15, 56);
        addPlayerHotbar(inv, -15, 56);

        this.addSlot(new FireplaceSlot(this.tileEntity.itemHandler, 0, 23, 15, TEXTURE_LOC_SLOT_INGREDIENT_1));
        this.addSlot(new FireplaceSlot(this.tileEntity.itemHandler, 1, 45, 15, TEXTURE_LOC_SLOT_INGREDIENT_2));
        this.addSlot(new FuelSlot(this.tileEntity.itemHandler, 2, 34, 55, TEXTURE_LOC_SLOT_FUEL));
        this.addSlot(new OutputSlot(this.tileEntity.itemHandler, 3, 91, 35));

        addDataSlots(data);
    }

    public boolean isCrafting() {
        return containerData.get(0) > 0;
    }

    public int getScaledArrowProgress() {
        int progress = this.containerData.get(0);
        int maxProgress = this.containerData.get(1);
        int arrowPixelSize = 24;

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

    @Override
    public @NotNull ItemStack getItem(int index) {
        return this.tileEntity.itemHandler.getStackInSlot(index);
    }

    @Override
    public int size() {
        return 4;
    }
}