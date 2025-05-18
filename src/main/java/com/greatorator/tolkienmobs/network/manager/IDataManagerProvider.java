package com.greatorator.tolkienmobs.network.manager;

import javax.annotation.Nonnull;

public interface IDataManagerProvider {

    @Nonnull
    IDataManager getDataManager();
}