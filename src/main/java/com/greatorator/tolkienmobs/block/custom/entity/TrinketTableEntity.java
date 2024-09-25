package com.greatorator.tolkienmobs.block.custom.entity;

import com.greatorator.tolkienmobs.containers.TrinketTableContainer;
import com.greatorator.tolkienmobs.containers.handlers.TrinketTableItemStackHandler;
import com.greatorator.tolkienmobs.handler.interfaces.ITicker;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.recipe.TrinketRecipe;
import com.greatorator.tolkienmobs.recipe.input.TripleRecipeInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TrinketTableEntity extends BlockEntity implements ITicker, MenuProvider {
    private final TrinketTableItemStackHandler inventory;
    private ItemStack curr;
    private TrinketRecipe recipe;
    public SimpleContainer simpleContainer = new SimpleContainer(4);
    private TrinketRecipe cachedTask = null;
    private int work;
    private int maxWork = 100;
    private int processingTime = 0;
    private boolean craft;
    private final ContainerData containerData = new ContainerData() {
            @Override
        public int get(int pIndex) {
            return switch (pIndex) {
                case 0 -> work;
                case 1 -> maxWork;
                default -> throw new IllegalArgumentException("Invalid index: " + pIndex);
            };
        }

        @Override
        public void set(int pIndex, int pValue) {
            throw new IllegalStateException("Cannot set values through IIntArray");
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public TrinketTableEntity(BlockPos pPos, BlockState pBlockState) {
        super(TolkienBlocks.TRINKETTABLE.get(), pPos, pBlockState);
        TrinketTableItemStackHandler inventory1;
        inventory1 = new TrinketTableItemStackHandler(4).setInputFilter((stack, slot) -> slot == 0);
        inventory1 = new TrinketTableItemStackHandler(4).setInputFilter((stack, slot) -> slot == 1);
        inventory1 = new TrinketTableItemStackHandler(4).setInputFilter((stack, slot) -> slot == 2);
        inventory1 = new TrinketTableItemStackHandler(4).setOutputFilter((stack, slot) -> slot == 3);
        this.inventory = inventory1;
    }

    public void drops(Level pLevel) {
        this.simpleContainer.setItem(0, this.inventory.getStackInSlot(0));
        this.simpleContainer.setItem(1, this.inventory.getStackInSlot(1));
        this.simpleContainer.setItem(2, this.inventory.getStackInSlot(2));
        this.simpleContainer.setItem(3, this.inventory.getStackInSlot(3));
        Containers.dropContents(pLevel, this.worldPosition, this.simpleContainer);
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
//        if (getLevel() == null) {return;}
//        ItemStack stack = inventory.getStackInSlot(0);
//        if ((stack.isEmpty() || this.curr != stack) && (recipe != null || work != -1)) {
//            this.recipe = null;
//            this.work = -1;
//            this.maxWork = 0;
//            this.curr = stack;
//            return;
//        }
//        if (!stack.isEmpty() && this.recipe == null && this.curr != stack) {
//            Registry<TrinketRecipe> recipes = getLevel().registryAccess().registry(TolkienRegistry.TRINKET_RECIPES).orElseThrow();
//            this.recipe = recipes.entrySet().stream().filter(entry -> entry.getValue().matches(, level)).map(Map.Entry::getValue).findFirst().orElse(null);
//            if (this.recipe == null) {return;}
//            this.work = this.maxWork = recipe.duration();
//            this.curr = stack;
//            return;
//        }
//        if (!stack.isEmpty() && this.recipe != null && this.work == -1)
//        {
//            this.work = this.maxWork = recipe.duration();
//        }
//        if (this.work > 0)
//        {
//            this.work--;
//            if (this.work == 0)
//            {
//                ItemStack comb = this.inventory.getStackInSlot(0);
//                comb.shrink(1);
//                this.inventory.setStackInSlot(0, comb);
//                List<ItemStack> outputs = this.recipe.assemble(this);
//                for (ItemStack output : outputs)
//                {
//                    ItemStack out = output;
//                    for (int i = 1; i < 10; i++)
//                    {
//                        if (this.inventory.insertItem(i, out.copy(), true) != out)
//                        {
//                            out = this.inventory.insertItem(i, out.copy(), false);
//                            if (out.isEmpty())
//                            {
//                                break;
//                            }
//                        }
//                    }
//                }
//                this.work = -1;
//                this.maxWork = 0;
//            }
//        }
    }

    private boolean hasCraftWork() {
        var task = this.getTask();
        if (task != null) {
            // Only process if the result would fit.
//            return sideItemHandler.insertItem(1, task.getResultItem().copy(), true).isEmpty();
        }

        this.setProcessingTime(0);
        return this.isCraft();
    }

    public boolean isCraft() {
        return this.craft;
    }

    @Nullable
    public TrinketRecipe getTask() {
        if (this.cachedTask == null && level != null) {
//            ItemStack input = this.sideItemHandler.getStackInSlot(0);
//            ItemStack plateA = this.topItemHandler.getStackInSlot(0);
//            ItemStack plateB = this.bottomItemHandler.getStackInSlot(0);
//            if (input.isEmpty()) {
//                return null; // No input to handle
//            }

//            this.cachedTask = TrinketRecipeHelper.findRecipe(level, input, plateA, plateB, true);
        }
        return this.cachedTask;
    }

    public int getMaxProcessingTime() {
        return this.maxWork;
    }

    public int getProcessingTime() {
        return this.processingTime;
    }

    private void setProcessingTime(int processingTime) {
        this.processingTime = processingTime;
    }

    @Override
    public void clientTick(Level level, BlockPos pos, BlockState state) {}

    @Override
    public void setRemoved() {
        super.setRemoved();
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("screen.tolkienmobs.block.tolkienmobs.trinket_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
        return new TrinketTableContainer(i, playerInventory, this, ContainerLevelAccess.create(getLevel(), getBlockPos()));
    }

    public TrinketTableItemStackHandler getInventory()
    {
        return inventory;
    }

    public ContainerData getContainerData()
    {
        return containerData;
    }

    public RecipeInput getRecipeInput() {
        return new TripleRecipeInput(this.inventory.getStackInSlot(0), this.inventory.getStackInSlot(1), this.inventory.getStackInSlot(2));
    }

    // World Save / Read Methods
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.putInt("work", work);
        tag.putInt("maxWork", maxWork);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        this.inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        this.work = tag.getInt("work");
        this.maxWork = tag.getInt("maxWork");
    }

    // Data Syncing Methods
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries)
    {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.putInt("work", work);
        tag.putInt("maxWork", maxWork);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.handleUpdateTag(tag, registries);
        this.inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        this.work = tag.getInt("work");
        this.maxWork = tag.getInt("maxWork");
    }

    // Data Update Methods
    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this, (be, reg) ->
        {
            CompoundTag tag = new CompoundTag();
            tag.put("inventory", inventory.serializeNBT(reg));
            tag.putInt("work", work);
            tag.putInt("maxWork", maxWork);
            return tag;
        });
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries)
    {
        CompoundTag tag = pkt.getTag();
        this.inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        this.work = tag.getInt("work");
        this.maxWork = tag.getInt("maxWork");
    }
}