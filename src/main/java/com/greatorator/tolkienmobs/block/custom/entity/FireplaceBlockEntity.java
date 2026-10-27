package com.greatorator.tolkienmobs.block.custom.entity;

import com.greatorator.tolkienmobs.TolkienMobsConfig;
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
import net.minecraft.resources.Identifier;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import com.greatorator.tolkienmobs.handler.capability.TolkienItemStackHandler;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static com.greatorator.tolkienmobs.block.custom.FireplaceBlock.LIT;

public class FireplaceBlockEntity extends TolkienBlockEntity implements MenuProvider {
    private final TolkienItemStackHandler fuelHandler = new TolkienItemStackHandler(1){
        @Override
        public boolean isValid(int slot, ItemResource resource) {
            return !resource.isEmpty() && resource.toStack().getBurnTime(RecipeType.SMELTING, level.fuelValues()) > 0;
        }

        @Override
        protected void onContentsChanged(int slot, ItemStack stack) {
            setChanged();
        }
    };
    public final TolkienItemStackHandler itemHandler = new TolkienItemStackHandler(2) {
        @Override
        public boolean isValid(int slot, ItemResource resource) {
            return !resource.isEmpty() && resource.test(stack -> stack.is(Tags.Items.FOODS));
        }

        @Override
        protected void onContentsChanged(int slot, ItemStack stack) {
            setChanged();
        }
    };
    public final TolkienItemStackHandler outputHandler = new TolkienItemStackHandler(1){
        @Override
        public boolean isValid(int slot, ItemResource resource) {
            return false;
        }

        @Override
        protected void onContentsChanged(int slot, ItemStack stack) {
            setChanged();
        }
    };
    public final Object2IntOpenHashMap<Identifier> recipes = new Object2IntOpenHashMap<>();
    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = TolkienMobsConfig.fireplaceCookTime();
    private int burnProgress = 0;
    private int maxBurnProgress = 0;
    private int fuelTime = 0;
    private int maxFuelTime = 16000;
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
        RandomSource randomsource = level.getRandom();
        int l;
        if (randomsource.nextFloat() < 0.11F) {
            for(l = 0; l < randomsource.nextInt(2) + 2; ++l) {
                FireplaceBlock.makeParticles(level, pos, (Boolean)state.getValue(LIT), false);
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        fuelHandler.serialize(output.child("fuel"));
        itemHandler.serialize(output.child("input"));
        outputHandler.serialize(output.child("output"));

        output.putInt("progress", progress);
        output.putInt("burnProgress", burnProgress);
        output.putInt("fuelTime", fuelTime);
        output.putInt("maxFuelTime", maxFuelTime);
        output.putInt("maxBurnProgress", maxBurnProgress);
        output.putBoolean("isBurning", isBurning);

        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        fuelHandler.deserialize(input.childOrEmpty("fuel"));
        itemHandler.deserialize(input.childOrEmpty("input"));
        outputHandler.deserialize(input.childOrEmpty("output"));

        progress = input.getIntOr("progress", 0);
        burnProgress = input.getIntOr("burnProgress", 0);
        fuelTime = input.getIntOr("fuelTime", 0);
        maxFuelTime = input.getIntOr("maxFuelTime", 0);
        maxBurnProgress = input.getIntOr("maxBurnProgress", 0);
        isBurning = input.getBooleanOr("isBurning", false);
    }

    public TolkienItemStackHandler getFuelHandler() {
        return fuelHandler;
    }

    public TolkienItemStackHandler getInputHandler() {
        return itemHandler;
    }

    public TolkienItemStackHandler getOutputHandler() {
        return outputHandler;
    }

    public ContainerData getData() {
        return data;
    }

    private void tryConsumeFuel() {
        ItemStack stack = ItemUtil.getStack(fuelHandler, 0);
        final int factor = 1;
        int burnTimeTicks = factor * stack.getBurnTime(RecipeType.SMELTING, level.fuelValues());

        if (this.getBlockState().getValue(LIT)) {
            fuelTime--;
        } else if (!stack.isEmpty() && burnTimeTicks > 0
                && fuelTime < maxFuelTime && ((maxFuelTime - fuelTime) >= burnTimeTicks)) {
            fuelTime = fuelTime + burnTimeTicks;
            // The stack read above is a copy, so the used piece of fuel has to go back into the slot.
            // An empty slot has no burn time and must never reach this write.
            int remaining = stack.getCount() - 1;
            fuelHandler.set(0, remaining > 0 ? ItemResource.of(stack) : ItemResource.EMPTY, remaining);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    /**
     * 26.1 calls this instead of the block's removed {@code onRemove}, which is where the fireplace's
     * four slots - the two inputs, the result and the fuel - are dropped.
     */
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        this.drops();
        super.preRemoveSideEffects(pos, state);
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(4);
        inv.setItem(0, ItemUtil.getStack(itemHandler, 0));
        inv.setItem(1, ItemUtil.getStack(itemHandler, 1));
        inv.setItem(2, ItemUtil.getStack(outputHandler, 0));
        inv.setItem(3, ItemUtil.getStack(fuelHandler, 0));

        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    public void tick(Level level, BlockPos pPos, BlockState pState) {
        tryConsumeFuel();
        setChanged(level, pPos, pState);

        if(hasRecipe() && hasRoomForResult()){
            level.setBlockAndUpdate(pPos, pState.setValue(LIT, true));
            increaseCraftingProgress();
            setChanged(level, pPos, pState);

            if(hasProgressFinished()) {
                craftItem();
                resetProgress();
                setChanged(level, pPos, pState);
            }
        } else {
            resetProgress();
            level.setBlockAndUpdate(pPos, pState.setValue(LIT, false));
            setChanged(level, pPos, pState);
        }
    }

    private void craftItem() {
        Optional<RecipeHolder<FireplaceRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) {
            return;
        }
        List<ItemStack> results = recipe.get().value().createOutputs();

        // Nothing is taken out of the inputs until every result has a slot waiting for it. A result
        // that could not be placed used to be written to stderr, and the food that made it was gone.
        int[] targets = planOutputs(results);
        if (targets == null) {
            return;
        }

        int extractFromSlot0 = 0;
        int extractFromSlot1 = 0;

        for (SizedIngredient ingredient : recipe.get().value().getInputItems()) {
            if (ingredient.test(ItemUtil.getStack(itemHandler, 0))) {
                extractFromSlot0 += Math.min(ItemUtil.getStack(itemHandler, 0).getCount(), ingredient.count());
            } else if (ingredient.test(ItemUtil.getStack(itemHandler, 1))) {
                extractFromSlot1 += Math.min(ItemUtil.getStack(itemHandler, 1).getCount(), ingredient.count());
            }
        }

        takeFromInput(0, extractFromSlot0);
        takeFromInput(1, extractFromSlot1);

        for (int i = 0; i < results.size(); i++) {
            ItemStack result = results.get(i);
            int outputSlot = targets[i];
            this.outputHandler.set(outputSlot, ItemResource.of(result),
                    ItemUtil.getStack(this.outputHandler, outputSlot).getCount() + result.getCount());
        }
    }

    /** Takes a recipe's worth of ingredients out of an input slot. */
    private void takeFromInput(int slot, int amount) {
        if (amount <= 0) return;
        ItemStack stack = ItemUtil.getStack(itemHandler, slot);
        if (stack.isEmpty()) return;
        itemHandler.set(slot, ItemResource.of(stack), stack.getCount() - Math.min(amount, stack.getCount()));
    }

    /**
     * The output slot each result would land in, or null when the fireplace cannot take them all.
     * The slots are simulated in place, so two results share a slot only when both fit there, and
     * the answer is the same before the write as after it. An amount over a slot's own limit is
     * never planned: that write would leave the slot holding more than the item stacks to.
     */
    private int[] planOutputs(List<ItemStack> results) {
        List<ItemStack> simulated = new ArrayList<>(this.outputHandler.size());
        for (int i = 0; i < this.outputHandler.size(); i++) {
            simulated.add(ItemUtil.getStack(this.outputHandler, i).copy());
        }

        int[] plan = new int[results.size()];
        for (int r = 0; r < results.size(); r++) {
            ItemStack result = results.get(r);
            int target = -1;

            for (int i = 0; i < simulated.size(); i++) {
                ItemStack existing = simulated.get(i);
                if (existing.isEmpty()) {
                    target = i;
                    simulated.set(i, result.copy());
                    break;
                }
                if (ItemStack.isSameItemSameComponents(existing, result)
                        && existing.getCount() + result.getCount() <= existing.getMaxStackSize()) {
                    target = i;
                    simulated.set(i, existing.copyWithCount(existing.getCount() + result.getCount()));
                    break;
                }
            }

            if (target == -1) {
                return null;
            }
            plan[r] = target;
        }
        return plan;
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

    /**
     * Whether the output slots can take everything the matching recipe hands back.
     * This used to read slot 0 of the <em>input</em> handler, so it answered for a food item being
     * cooked instead of for the cooked food already waiting in the output, and it never looked at
     * the result at all.
     */
    private boolean hasRoomForResult() {
        Optional<RecipeHolder<FireplaceRecipe>> recipe = getCurrentRecipe();
        return recipe.isPresent() && planOutputs(recipe.get().value().createOutputs()) != null;
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
        List<ItemStack> outputResults = currentRecipe.createOutputs();

        // Create a mutable copy of the user inputs
        List<ItemStack> userInputs = new ArrayList<>();
        userInputs.add(ItemUtil.getStack(this.itemHandler, 0));
        userInputs.add(ItemUtil.getStack(this.itemHandler, 1));

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

        for (int i = 0; i < this.outputHandler.size(); i++) {
            ItemStack stackInSlot = ItemUtil.getStack(this.outputHandler, i);
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

        for (int i = 0; i < this.itemHandler.size(); i++) {
            inputs.add(ItemUtil.getStack(this.itemHandler, i));
        }

        return this.level.getServer().getRecipeManager().getRecipeFor(TolkienRecipesTypes.FIREPLACE_TYPE.get(), new FireplaceRecipeInput(inputs), level);
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        for (int i = 0; i < this.outputHandler.size(); i++) {
            ItemStack stackInSlot = ItemUtil.getStack(this.outputHandler, i);
            if (stackInSlot.isEmpty() || stackInSlot.getItem() == item) {
                return true;
            }
        }
        return false;
    }

    private boolean canInsertAmountIntoOutputSlot(ItemStack  count) {
        for (int i = 0; i < this.outputHandler.size(); i++) {
            ItemStack stackInSlot = ItemUtil.getStack(this.outputHandler, i);
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