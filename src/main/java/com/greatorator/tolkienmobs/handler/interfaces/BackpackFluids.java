package com.greatorator.tolkienmobs.handler.interfaces;

import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public interface BackpackFluids {
    default int getMaxMB() {
        return 8000;
    }

    ContainerData getFluidContainerData();

    FluidTank getFluidTank();

    default FluidStack getFluidStack() {
        return getFluidTank().getFluid();
    }

    default void setFluidStack(Fluid fluid, int amt) {
        getFluidTank().setFluid(new FluidStack(fluid, amt));
    }

    default int getAmountStored() {
        return getFluidTank().getFluidAmount();
    }

    default void setAmountStored(int value) {
        if (getAmountStored() > 128000) {
            getFluidTank().getFluid().setAmount(Integer.MAX_VALUE);
        } else {
            getFluidTank().getFluid().setAmount(value);
        }
    }

    default boolean isFull() {
        return getFluidStack().getAmount() >= getMaxMB();
    }
}