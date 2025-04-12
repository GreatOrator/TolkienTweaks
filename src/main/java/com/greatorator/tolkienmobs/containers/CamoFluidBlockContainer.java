package com.greatorator.tolkienmobs.containers;

import com.greatorator.tolkienmobs.block.custom.entity.CamoFluidBlockEntity;
import com.greatorator.tolkienmobs.containers.handlers.BucketItemHandler;
import com.greatorator.tolkienmobs.init.TolkienContainers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CamoFluidBlockContainer extends TolkienContainer {
    public final CamoFluidBlockEntity tileEntity;
    private final Player player;
    private final Level level;
    private final ContainerLevelAccess access;
    public ContainerData fluidData;
    public int BUCKET_SLOTS = 1;

    public CamoFluidBlockContainer(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }
    public CamoFluidBlockContainer(int pContainerId, Inventory inv, BlockEntity blockEntity) {
        super(TolkienContainers.CAMO_FLUID_CONTAINER.get(), pContainerId);

        this.tileEntity = ((CamoFluidBlockEntity) blockEntity);
        this.level = inv.player.level();
        this.player = inv.player;
        this.access = ContainerLevelAccess.create(this.level, this.tileEntity.getBlockPos());

        addPlayerInventory(inv, 8, 20);
        addPlayerHotbar(inv, 8, 20);

        addFluidSlots();
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
        super.clicked(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public void addFluidSlots() {
        this.addSlot(new BucketItemHandler(this.tileEntity.mainInventory, 0, 80, 17));
    }
}
