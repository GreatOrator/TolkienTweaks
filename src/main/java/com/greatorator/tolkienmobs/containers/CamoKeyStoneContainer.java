package com.greatorator.tolkienmobs.containers;

import com.greatorator.tolkienmobs.block.custom.entity.CamoKeyStoneBlockEntity;
import com.greatorator.tolkienmobs.init.TolkienContainers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CamoKeyStoneContainer extends AbstractContainerMenu {
    public final CamoKeyStoneBlockEntity tileEntity;
    private final Level level;
    public String keyCode;
    public int tickDelay;
    public int tickActive;
    public boolean keyConsume;
    public boolean rsAlways;
    public boolean rsPulse;
    public boolean rsDelay;

    public CamoKeyStoneContainer(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public CamoKeyStoneContainer(int pContainerId, Inventory inv, BlockEntity blockEntity) {
        super(TolkienContainers.KEY_STONE_CONTAINER.get(), pContainerId);
        this.level = inv.player.level();
        this.tileEntity = ((CamoKeyStoneBlockEntity) blockEntity);
        this.keyCode = tileEntity.getKeyCode();
        this.tickDelay = tileEntity.getTickDelay();
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
