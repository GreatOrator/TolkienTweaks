package com.greatorator.tolkienmobs.network.manager;

import com.google.common.annotations.Beta;

public class DataFlags {

    public static DataFlags NONE = new DataFlags(false, false, false, false, false, false, false, false);

    public static DataFlags SAVE_NBT = new DataFlags(true, false, false, false, false, false, false, false);

    public static DataFlags SAVE_ITEM = new DataFlags(false, true, false, false, false, false, false, false);

    public static DataFlags SYNC_TILE = new DataFlags(false, false, true, false, false, false, false, false);

    public static DataFlags SYNC_CONTAINER = new DataFlags(false, false, false, true, false, false, false, false);

    public static DataFlags TRIGGER_UPDATE = new DataFlags(false, false, false, false, true, false, false, false);

    public static DataFlags SYNC_ON_SET = new DataFlags(false, false, false, false, false, true, false, false);

    @Beta
    public static DataFlags CLIENT_CONTROL = new DataFlags(false, false, false, false, false, false, true, false);
    public static DataFlags SAVE_BOTH = new DataFlags(SAVE_NBT, SAVE_ITEM);
    public static DataFlags SAVE_NBT_SYNC_TILE = new DataFlags(SAVE_NBT, SYNC_TILE);
    public static DataFlags SAVE_NBT_SYNC_CONTAINER = new DataFlags(SAVE_NBT, SYNC_CONTAINER);
    public static DataFlags SAVE_BOTH_SYNC_TILE = new DataFlags(SAVE_BOTH, SYNC_TILE);
    public static DataFlags SAVE_BOTH_SYNC_CONTAINER = new DataFlags(SAVE_BOTH, SYNC_CONTAINER);

    @Deprecated
    public static DataFlags DONT_DIRTY = new DataFlags(false, false, false, false, false, false, false, true);


    public final boolean saveNBT;
    public final boolean saveItem;
    public final boolean syncTile;
    public final boolean syncContainer;
    public final boolean triggerUpdate;
    public final boolean syncOnSet;
    public final boolean allowClientControl;
    public final boolean dontMark;

    DataFlags(boolean saveNBT, boolean saveItem, boolean syncTile, boolean syncContainer, boolean triggerUpdate, boolean syncOnSet, boolean allowClientControl, boolean dontMark) {
        this.saveNBT = saveNBT;
        this.saveItem = saveItem;
        this.syncTile = syncTile;
        this.syncContainer = syncContainer;
        this.triggerUpdate = triggerUpdate;
        this.syncOnSet = syncOnSet;
        this.allowClientControl = allowClientControl;
        this.dontMark = dontMark;
    }

    DataFlags(DataFlags[] combine) {
        this(NONE, combine);
    }

    DataFlags(DataFlags base, DataFlags... combine) {
        boolean saveNBT = base.saveNBT;
        boolean saveItem = base.saveItem;
        boolean syncTile = base.syncTile;
        boolean syncContainer = base.syncContainer;
        boolean triggerUpdate = base.triggerUpdate;
        boolean syncOnSet = base.syncOnSet;
        boolean allowClientControl = base.allowClientControl;
        boolean dontMark = base.dontMark;
        for (DataFlags flag : combine) {
            saveNBT |= flag.saveNBT;
            saveItem |= flag.saveItem;
            syncTile |= flag.syncTile;
            syncContainer |= flag.syncContainer;
            triggerUpdate |= flag.triggerUpdate;
            syncOnSet |= flag.syncOnSet;
            allowClientControl |= flag.allowClientControl;
            dontMark |= flag.dontMark;
        }
        this.saveNBT = saveNBT;
        this.saveItem = saveItem;
        this.syncTile = syncTile;
        this.syncContainer = syncContainer;
        this.triggerUpdate = triggerUpdate;
        this.syncOnSet = syncOnSet;
        this.allowClientControl = allowClientControl;
        this.dontMark = dontMark;
    }

    public boolean syncViaPacket() {
        return syncTile || syncContainer;
    }
}
