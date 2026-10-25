package com.greatorator.tolkienmobs.handler.capability;

import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;

/**
 * The pack's tank on 26.1's transfer API: one slot of {@link FluidResource}, which is what a fluid
 * capability hands out now. The older {@code IFluidHandler} view and its delegation are gone, so
 * {@link #getFluid()}, {@link #getFluidAmount()}, {@link #setFluid} and {@link #setCapacity} are how
 * the tank's own callers read and write it.
 * <p>
 * A tank written by 26.1's {@code FluidTank} stored its contents under {@code Fluid}, so that is read
 * when the new {@code stacks} key is absent.
 */
public class TolkienFluidTank extends FluidStacksResourceHandler {
    private static final String LEGACY_FLUID_KEY = "Fluid";

    public TolkienFluidTank(int capacity) {
        super(1, Math.max(0, capacity));
    }

    /** The backpack's tank grows with its fluid upgrades, so its capacity moves with them. */
    public void setCapacity(int capacity) {
        this.capacity = Math.max(0, capacity);
        if (getAmountAsInt(0) > this.capacity) {
            set(0, getResource(0), this.capacity);
        }
    }

    public FluidStack getFluid() {
        FluidResource resource = getResource(0);
        return resource.isEmpty() ? FluidStack.EMPTY : resource.toStack(getAmountAsInt(0));
    }

    public int getFluidAmount() {
        return getAmountAsInt(0);
    }

    /**
     * Replaces the tank's contents, cut down to the tank's capacity.
     * A negative amount would reach {@code set}, which refuses one outright, so it empties the tank
     * instead: the backpack's menu data can hand over a half-written amount mid-update.
     */
    public void setFluid(FluidStack stack) {
        if (stack == null || stack.isEmpty() || stack.getAmount() <= 0) {
            set(0, FluidResource.EMPTY, 0);
            return;
        }
        set(0, FluidResource.of(stack), Math.min(stack.getAmount(), capacity));
    }

    /** Sets how much of the fluid already in the tank is there, kept between empty and full. */
    public void setAmount(int amount) {
        set(0, getResource(0), Math.max(0, Math.min(amount, capacity)));
    }

    public boolean isEmpty() {
        return ResourceHandlerUtil.isEmpty(this);
    }

    @Override
    public void deserialize(ValueInput input) {
        super.deserialize(input);
        if (isEmpty()) {
            input.read(LEGACY_FLUID_KEY, FluidStack.OPTIONAL_CODEC).ifPresent(this::setFluid);
        }
    }
}
