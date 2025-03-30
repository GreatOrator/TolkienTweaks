package com.greatorator.tolkienmobs.handler.data;

import com.greatorator.tolkienmobs.handler.interfaces.BackpackFluids;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.ContainerData;

public class BackpackFluidData implements ContainerData {
    BackpackFluids fluidBackpackData;

    public BackpackFluidData(BackpackFluids fluidBackpackData) {
        this.fluidBackpackData = fluidBackpackData;
    }

    @Override
    public int get(int index) {
        return switch (index) {
            case 0 -> BuiltInRegistries.FLUID.getId(fluidBackpackData.getFluidStack().getFluid());
            case 1 -> fluidBackpackData.getAmountStored() & 0xFFFF;
            case 2 -> fluidBackpackData.getAmountStored() >> 16;
            default -> throw new IllegalArgumentException("Invalid index: " + index);
        };
    }

    @Override
    public void set(int index, int value) {
        switch (index) {
            case 0 ->
                    fluidBackpackData.setFluidStack(BuiltInRegistries.FLUID.byId(value), fluidBackpackData.getAmountStored()); //Double it'll be used by who knows
            case 1 ->
                    fluidBackpackData.setAmountStored((fluidBackpackData.getAmountStored() & 0xFFFF0000) | (value & 0xFFFF));
            case 2 ->
                    fluidBackpackData.setAmountStored((fluidBackpackData.getAmountStored() & 0xFFFF) | (value << 16));
            default -> throw new IllegalArgumentException("Invalid index: " + index);
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}