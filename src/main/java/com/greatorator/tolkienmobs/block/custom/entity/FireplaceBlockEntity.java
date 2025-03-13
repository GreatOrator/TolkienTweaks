package com.greatorator.tolkienmobs.block.custom.entity;

import com.greatorator.tolkienmobs.block.custom.FireplaceBlock;
import com.greatorator.tolkienmobs.containers.FireplaceContainer;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienRecipesTypes;
import com.greatorator.tolkienmobs.recipe.FireplaceRecipe;
import com.greatorator.tolkienmobs.recipe.FireplaceRecipeInput;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class FireplaceBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler fuelHandler = new ItemStackHandler(1){
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.getBurnTime(RecipeType.SMELTING) > 0;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    public final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.is(Tags.Items.FOODS);
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    public final ItemStackHandler outputHandler = new ItemStackHandler(1){
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    public final Object2IntOpenHashMap<ResourceLocation> recipes = new Object2IntOpenHashMap<>();
    public RecipeType<? extends AbstractCookingRecipe> recipeType;
    private final RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> recipeCheckSmelting;
    private final RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> recipeCheckSmoking;
    private final RecipeManager.CachedCheck<FireplaceRecipeInput, ? extends FireplaceRecipe> recipeCheckGenerator;

    private static final int OUTPUT_SLOT = 0;
    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;
    private int burnProgress = 0;
    private int maxBurnProgress = 0;
    private int fuelTime = 0;
    private int maxFuelTime = 0;
    private boolean isBurning = false;

    public FireplaceBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(TolkienBlocks.FIREPLACE_BLOCK_ENTITY.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> FireplaceBlockEntity.this.progress;
                    case 1 -> FireplaceBlockEntity.this.maxProgress;
                    case 2 -> FireplaceBlockEntity.this.fuelTime;
                    case 3 -> FireplaceBlockEntity.this.maxFuelTime;
                    case 4 -> FireplaceBlockEntity.this.burnProgress;
                    case 5 -> FireplaceBlockEntity.this.maxBurnProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> FireplaceBlockEntity.this.progress = pValue;
                    case 1 -> FireplaceBlockEntity.this.maxProgress = pValue;
                    case 2 -> FireplaceBlockEntity.this.fuelTime = pValue;
                    case 3 -> FireplaceBlockEntity.this.maxFuelTime = pValue;
                    case 5 -> FireplaceBlockEntity.this.maxBurnProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 6;
            }
        };
        recipeCheckSmelting = RecipeManager.createCheck(RecipeType.SMELTING);
        recipeCheckSmoking = RecipeManager.createCheck(RecipeType.SMOKING);
        recipeCheckGenerator = RecipeManager.createCheck(TolkienRecipesTypes.FIREPLACE_TYPE.get());
        recipeType = RecipeType.SMELTING;
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
        pTag.put("fuel", fuelHandler.serializeNBT(pRegistries));
        pTag.put("input", itemHandler.serializeNBT(pRegistries));
        pTag.put("output", outputHandler.serializeNBT(pRegistries));

        pTag.putInt("progress", progress);
        pTag.putInt("burnProgress", burnProgress);
        pTag.putInt("fuelTime", fuelTime);
        pTag.putInt("maxFuelTime", maxFuelTime);
        pTag.putInt("maxBurnProgress", maxBurnProgress);
        pTag.putBoolean("isBurning", isBurning);

        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        fuelHandler.deserializeNBT(pRegistries, pTag.getCompound("fuel"));
        itemHandler.deserializeNBT(pRegistries, pTag.getCompound("input"));
        outputHandler.deserializeNBT(pRegistries, pTag.getCompound("output"));

        progress = pTag.getInt("progress");
        burnProgress = pTag.getInt("burnProgress");
        fuelTime = pTag.getInt("fuelTime");
        maxFuelTime = pTag.getInt("maxFuelTime");
        maxBurnProgress = pTag.getInt("maxBurnProgress");
        isBurning = pTag.getBoolean("isBurning");
    }

    public ItemStackHandler getFuelHandler() {
        return fuelHandler;
    }

    public ItemStackHandler getInputHandler() {
        return itemHandler;
    }

    public ItemStackHandler getOutputHandler() {
        return outputHandler;
    }

    public ContainerData getData() {
        return data;
    }

    private void tryConsumeFuel() {
        assert level != null;
        this.maxFuelTime = 0;
        //pull in new fuel
        ItemStack stack = fuelHandler.getStackInSlot(0);
        final int factor = 1;
        int burnTimeTicks = factor * stack.getBurnTime(RecipeType.SMELTING);
        if (burnTimeTicks > 0) {
            // BURN IT
            this.maxFuelTime = burnTimeTicks;
            this.fuelTime = this.maxFuelTime;
            if (stack.getCount() == 1 && stack.hasCraftingRemainingItem()) {
                fuelHandler.setStackInSlot(0, stack.getCraftingRemainingItem().copy());
            }
            else {
                stack.shrink(1);
            }
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(4);
        inv.setItem(0, itemHandler.getStackInSlot(0));
        inv.setItem(1, itemHandler.getStackInSlot(1));
        inv.setItem(2, outputHandler.getStackInSlot(0));
        inv.setItem(3, fuelHandler.getStackInSlot(0));

        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    public void tick(Level level, BlockPos pPos, BlockState pState) {
        tryConsumeFuel();
        setChanged(level, pPos, pState);

        if(hasRecipe() && isOutputSlotEmptyOrReceivable()){
            level.setBlockAndUpdate(pPos, pState.setValue(FireplaceBlock.LIT, true));
            increaseCraftingProgress();
            fuelTime=fuelTime-10;

            setChanged(level, pPos, pState);

            if(hasProgressFinished()) {
                craftItem();
                resetProgress();
                setChanged(level, pPos, pState);
            }
        } else {
            resetProgress();
            level.setBlockAndUpdate(pPos, pState.setValue(FireplaceBlock.LIT, false));
            setChanged(level, pPos, pState);
        }
    }

    private void craftItem() {
        Optional<RecipeHolder<FireplaceRecipe>> recipe = getCurrentRecipe();
        if (recipe.isPresent()) {
            List<ItemStack> results = recipe.get().value().getOutput();

            int extractFromSlot0 = 0;
            int extractFromSlot1 = 0;

            for (SizedIngredient ingredient : recipe.get().value().getInputItems()) {
                if (ingredient.test(itemHandler.getStackInSlot(0))) {
                    extractFromSlot0 += Math.min(itemHandler.getStackInSlot(0).getCount(), ingredient.count());
                } else if (ingredient.test(itemHandler.getStackInSlot(1))) {
                    extractFromSlot1 += Math.min(itemHandler.getStackInSlot(1).getCount(), ingredient.count());
                }
            }

            if (extractFromSlot0 > 0) {
                itemHandler.extractItem(0, extractFromSlot0, false);
            }
            if (extractFromSlot1 > 0) {
                itemHandler.extractItem(1, extractFromSlot1, false);
            }

            for (ItemStack result : results) {
                int outputSlot = findSuitableOutputSlot(result);
                if (outputSlot != -1) {
                    this.outputHandler.setStackInSlot(outputSlot, new ItemStack(result.getItem(),
                            this.outputHandler.getStackInSlot(outputSlot).getCount() + result.getCount()));

                } else {
                    System.err.println("No suitable output slot found for item: " + result);
                }
            }
        }
    }

    private int findSuitableOutputSlot(ItemStack result) {
        // Implement logic to find a suitable output slot for the given result
        // Return the slot index or -1 if no suitable slot is found
        for (int i = 0; i < this.outputHandler.getSlots(); i++) {
            ItemStack stackInSlot = this.outputHandler.getStackInSlot(i);
            if (stackInSlot.isEmpty() || (stackInSlot.getItem() == result.getItem() && stackInSlot.getCount() + result.getCount() <= stackInSlot.getMaxStackSize())) {
                return i;
            }
        }
        return -1;
    }


    private boolean hasProgressFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    private void resetProgress() {
        progress = 0;
    }

    private boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }

    private boolean isOutputSlotEmptyOrReceivable() {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ||
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() < this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }

    private boolean hasRecipe() {
        // Get the current recipe, return false if not present
        Optional<RecipeHolder<FireplaceRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) {
            return false;
        }

        // Retrieve recipe inputs and outputs
        FireplaceRecipe currentRecipe = recipe.get().value();
        List<SizedIngredient> recipeIngredients = currentRecipe.getInputItems();
        List<ItemStack> outputResults = currentRecipe.getOutput();

        // Create a mutable copy of the user inputs
        List<ItemStack> userInputs = new ArrayList<>();
        userInputs.add(this.itemHandler.getStackInSlot(0));
        userInputs.add(this.itemHandler.getStackInSlot(1));

        // Check if all recipe ingredients are matched with user inputs
        for (SizedIngredient recipeIngredient : recipeIngredients) {
            boolean ingredientMatched = false;

            // Try to match the ingredient with one of the user inputs
            Iterator<ItemStack> userInputIterator = userInputs.iterator();
            while (userInputIterator.hasNext()) {
                ItemStack userInput = userInputIterator.next();

                if (recipeIngredient.ingredient().test(userInput) && userInput.getCount() >= recipeIngredient.count()) {
                    // Match found; remove the input to avoid duplicate matches
                    userInputIterator.remove();
                    ingredientMatched = true;
                    break;
                }
            }

            if (!ingredientMatched) {
                // If no match is found for a recipe ingredient, the recipe does not match
                return false;
            }
        }

        // Check if outputs can fit into the output slots
        for (ItemStack result : outputResults) {
            if (!canInsertAmountIntoOutputSlot(result) || !canInsertItemIntoOutputSlot(result.getItem())) {
                return false;
            }
        }

        // Final validation of the slots (if any additional checks are required)
        return checkSlot(outputResults);
    }

    private boolean checkSlot(List<ItemStack> results){
        int count = 0;
        int emptyCount = 0;
        for (ItemStack result : results){
            count++;
        }

        for (int i = 0; i < this.outputHandler.getSlots(); i++) {
            ItemStack stackInSlot = this.outputHandler.getStackInSlot(i);
            if(!stackInSlot.isEmpty()){
                for (ItemStack result : results){
                    if(stackInSlot.getItem() == result.getItem()){
                        if(stackInSlot.getCount() + result.getCount() <= 64){
                            emptyCount++;
                        }
                    }
                }
            }
            else {
                emptyCount++;
            }
        }

        return emptyCount >= count;
    }

    private Optional<RecipeHolder<FireplaceRecipe>> getCurrentRecipe(){
        List<ItemStack> inputs = new ArrayList<>();

        for (int i = 0; i < this.itemHandler.getSlots(); i++) {
            inputs.add(this.itemHandler.getStackInSlot(i));
        }

        return this.level.getRecipeManager().getRecipeFor(TolkienRecipesTypes.FIREPLACE_TYPE.get(), new FireplaceRecipeInput(inputs), level);
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        for (int i = 0; i < this.outputHandler.getSlots(); i++) {
            ItemStack stackInSlot = this.outputHandler.getStackInSlot(i);
            if (stackInSlot.isEmpty() || stackInSlot.getItem() == item) {
                return true;
            }
        }
        return false;
    }

    private boolean canInsertAmountIntoOutputSlot(ItemStack  count) {
        for (int i = 0; i < this.outputHandler.getSlots(); i++) {
            ItemStack stackInSlot = this.outputHandler.getStackInSlot(i);
            if (stackInSlot.isEmpty() || (stackInSlot.getItem() == count.getItem() && stackInSlot.getCount() + count.getCount() <= stackInSlot.getMaxStackSize())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}