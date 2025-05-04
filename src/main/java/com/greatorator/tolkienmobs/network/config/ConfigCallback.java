package com.greatorator.tolkienmobs.network.config;

public interface ConfigCallback<T extends ConfigTag> {

    void onSync(T tag, Reason reason);

    enum Reason {
        MANUAL,
        SYNC,
        ROLLBACK,
    }
}
