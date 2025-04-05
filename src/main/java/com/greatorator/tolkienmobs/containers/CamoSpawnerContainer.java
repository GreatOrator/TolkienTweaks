package com.greatorator.tolkienmobs.containers;

import com.greatorator.tolkienmobs.block.custom.entity.CamoSpawnerBlockEntity;
import com.greatorator.tolkienmobs.init.TolkienContainers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CamoSpawnerContainer extends AbstractContainerMenu {
    public final CamoSpawnerBlockEntity tileEntity;
    private final ContainerLevelAccess access;
    private final Level level;

    public CamoSpawnerContainer(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public CamoSpawnerContainer(int pContainerId, Inventory inv, BlockEntity blockEntity) {
        super(TolkienContainers.CAMO_SPAWNER_CONTAINER.get(), pContainerId);
        this.tileEntity = ((CamoSpawnerBlockEntity) blockEntity);
        this.level = inv.player.level();
        this.access = ContainerLevelAccess.create(this.level, this.tileEntity.getBlockPos());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
        super.clicked(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
