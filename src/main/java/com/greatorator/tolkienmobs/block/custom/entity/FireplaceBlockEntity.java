package com.greatorator.tolkienmobs.block.custom.entity;

import com.greatorator.tolkienmobs.block.custom.FireplaceBlock;
import com.greatorator.tolkienmobs.containers.FireplaceContainer;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class FireplaceBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    private static final int INGREDIENT_1_SLOT = 0;
    private static final int INGREDIENT_2_SLOT = 1;
    private static final int OUTPUT_SLOT = 2;
    private static final int FUEL_SLOT = 3;
    private final ContainerData data;
    private int progress = 0;
    private int maxProgress = 72;
    private final int DEFAULT_MAX_PROGRESS = 100;
    private float rotation;

    public FireplaceBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(TolkienBlocks.FIREPLACE_BLOCK_ENTITY.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> FireplaceBlockEntity.this.progress;
                    case 1 -> FireplaceBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0: FireplaceBlockEntity.this.progress = pValue;
                    case 1: FireplaceBlockEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("screen.tolkienmobs.block.tolkienmobs.fireplace");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new FireplaceContainer(pContainerId, pPlayerInventory, this, this.data);
    }

    public static void particleTick(Level level, BlockPos pos, BlockState state, FireplaceBlockEntity blockEntity) {
        RandomSource randomsource = level.random;
        int l;
        if (randomsource.nextFloat() < 0.11F) {
            for(l = 0; l < randomsource.nextInt(2) + 2; ++l) {
                FireplaceBlock.makeParticles(level, pos, (Boolean)state.getValue(FireplaceBlock.LIT), false);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.put("inventory", itemHandler.serializeNBT(pRegistries));
        pTag.putInt("progress", progress);
        pTag.putInt("max_progress", maxProgress);

        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        progress = pTag.getInt("progress");
        maxProgress = pTag.getInt("max_progress");
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++) {
            inv.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    public void tick(Level level, BlockPos pPos, BlockState pState) {
//        if(hasRecipe() && isOutputSlotEmptyOrReceivable()) {
//            increaseCraftingProgress();
//            level.setBlockAndUpdate(pPos, pState.setValue(FireplaceBlock.LIT, true));
//            setChanged(level, pPos, pState);
//
//            if (hasCraftingFinished()) {
//                craftItem();
//                resetProgress();
//            }
//
//        } else {
//            resetProgress();
//            level.setBlockAndUpdate(pPos, pState.setValue(FireplaceBlock.LIT, false));
//        }
    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = DEFAULT_MAX_PROGRESS;
    }

//    private void craftItem() {
//        Optional<RecipeHolder<FireplaceRecipe>> recipe = getCurrentRecipe();
//        ItemStack output = recipe.get().value().output();
//
//        itemHandler.extractItem(TRINKET_SLOT, 1, false);
//        itemHandler.extractItem(POTION_SLOT, 1, false);
//        itemHandler.extractItem(GEM_SLOT, 1, false);
//        itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(output.getItem(),
//                itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + output.getCount()));
//    }

    private boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    private boolean isOutputSlotEmptyOrReceivable() {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ||
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() < this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }

//    private boolean hasRecipe() {
//        Optional<RecipeHolder<FireplaceRecipe>> recipe = getCurrentRecipe();
//        if(recipe.isEmpty()) {
//            return false;
//        }
//
//        ItemStack output = recipe.get().value().getResultItem(null);
//        return canInsertAmountIntoOutputSlot(output.getCount()) && canInsertItemIntoOutputSlot(output);
//    }

//    private Optional<RecipeHolder<FireplaceRecipe>> getCurrentRecipe() {
//        return this.level.getRecipeManager()
//                .getRecipeFor(TolkienRecipesTypes.FIREPLACE, new TrinketTableRecipeInput(itemHandler.getStackInSlot(TRINKET_SLOT)), level);
//    }

    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        return itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ||
                itemHandler.getStackInSlot(OUTPUT_SLOT).getItem() == output.getItem();
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        int maxCount = itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ? 64 : itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
        int currentCount = itemHandler.getStackInSlot(OUTPUT_SLOT).getCount();

        return maxCount >= currentCount + count;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    public float getRenderingRotation() {
        rotation += 0.5f;
        if(rotation >= 360) {
            rotation = 0;
        }
        return rotation;
    }
}