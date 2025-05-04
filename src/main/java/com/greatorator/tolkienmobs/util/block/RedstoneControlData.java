package com.greatorator.tolkienmobs.util.block;


import com.greatorator.tolkienmobs.util.GeneralUtility;

import java.util.Objects;

public class RedstoneControlData {
    public boolean receivingRedstone = false;
    public boolean checkedRedstone = false;
    public boolean pulsed = false;
    public GeneralUtility.RedstoneMode redstoneMode = GeneralUtility.RedstoneMode.TOGGLE;

    public RedstoneControlData() {

    }

    public RedstoneControlData(GeneralUtility.RedstoneMode redstoneMode) {
        this.redstoneMode = redstoneMode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(receivingRedstone, pulsed, redstoneMode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RedstoneControlData that = (RedstoneControlData) o;
        return receivingRedstone == that.receivingRedstone &&
                pulsed == that.pulsed &&
                redstoneMode == that.redstoneMode;
    }
}
