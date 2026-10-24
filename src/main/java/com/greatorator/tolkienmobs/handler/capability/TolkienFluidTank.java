package com.greatorator.tolkienmobs.handler.capability;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.function.Predicate;

/**
 * The tank itself carries no extra state: 26.1's {@link FluidTank} is already a
 * {@code ValueIOSerializable}, so it writes and reads its own fluid and capacity and the
 * NBT wrapper this class used to add is gone.
 */
public class TolkienFluidTank extends FluidTank {
    public TolkienFluidTank(int capacity) {
        super(capacity);
    }

    public TolkienFluidTank(int capacity, Predicate<FluidStack> validator) {
        super(capacity, validator);
    }
}
