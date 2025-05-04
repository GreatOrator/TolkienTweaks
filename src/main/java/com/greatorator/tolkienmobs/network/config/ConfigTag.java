package com.greatorator.tolkienmobs.network.config;

import com.greatorator.tolkienmobs.handler.data.MCDataInput;
import com.greatorator.tolkienmobs.handler.data.MCDataOutput;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ConfigTag {
    String getName();

    default String getDesc() {
        return (getParent() != null ? getParent().getDesc() + "." : "") + getName();
    }

    @Nullable
    ConfigCategory getParent();

    ConfigTag setComment(String comment);

    ConfigTag setComment(String... comment);

    ConfigTag setComment(List<String> comment);

    List<String> getComment();

    ConfigTag syncTagToClient();

    boolean requiresClientSync();

    default void forceSync() {
        runSync(ConfigCallback.Reason.MANUAL);
    }

    void runSync(ConfigCallback.Reason reason);

    boolean isNetworkTag();

    default void save() {
        if (getParent() != null) {
            getParent().save();
        }
    }

    boolean isDirty();

    default void delete() {
        if (getParent() != null) {
            getParent().delete(getName());
        }
    }

    void reset();

    ConfigTag copy();

    void write(MCDataOutput out);

    void read(MCDataInput in);

    void resetFromNetwork();
}
