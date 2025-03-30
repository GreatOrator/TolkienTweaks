package com.greatorator.tolkienmobs.util;

import java.util.Objects;

public class BackpackFluidUpgrades {
    public boolean fluid_tank = false;
    public boolean fluid_tank_2 = false;
    public boolean fluid_tank_3 = false;
    public boolean fluid_tank_4 = false;
    public boolean fluid_tank_5 = false;

    public BackpackFluidUpgrades() {

    }

    public BackpackFluidUpgrades(boolean fluid_tank, boolean fluid_tank_2, boolean fluid_tank_3, boolean fluid_tank_4, boolean fluid_tank_5) {
        this.fluid_tank = fluid_tank;
        this.fluid_tank_2 = fluid_tank_2;
        this.fluid_tank_3 = fluid_tank_3;
        this.fluid_tank_4 = fluid_tank_4;
        this.fluid_tank_5 = fluid_tank_5;
    }


    @Override
    public int hashCode() {
        return Objects.hash(fluid_tank, fluid_tank_2, fluid_tank_3, fluid_tank_4, fluid_tank_5);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        BackpackFluidUpgrades that = (BackpackFluidUpgrades) object;
        return fluid_tank == that.fluid_tank
                && fluid_tank_2 == that.fluid_tank_2
                && fluid_tank_3 == that.fluid_tank_3
                && fluid_tank_4 == that.fluid_tank_4
                && fluid_tank_5 == that.fluid_tank_5;
    }
}
