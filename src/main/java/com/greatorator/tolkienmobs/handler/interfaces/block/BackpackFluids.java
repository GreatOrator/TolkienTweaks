package com.greatorator.tolkienmobs.handler.interfaces.block;

import com.greatorator.tolkienmobs.handler.capability.TolkienFluidTank;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public interface BackpackFluids {
    default int getMaxMB() {
        return 8000;
    }

    ContainerData getFluidContainerData();

    TolkienFluidTank getFluidTank();

    default FluidStack getFluidStack() {
        return getFluidTank().getFluid();
    }

    default void setFluidStack(Fluid fluid, int amt) {
        getFluidTank().setFluid(new FluidStack(fluid, amt));
    }

    default int getAmountStored() {
        return getFluidTank().getFluidAmount();
    }

    /**
     * The tank keeps the amount itself between empty and full. The old branch here compared the
     * stored amount to a number the tank can never reach and then wrote {@code Integer.MAX_VALUE}
     * into it, and any smaller amount - including a negative one - went through untouched.
     */
    default void setAmountStored(int value) {
        getFluidTank().setAmount(value);
    }

    default boolean isFull() {
        return getFluidStack().getAmount() >= getMaxMB();
    }
}
