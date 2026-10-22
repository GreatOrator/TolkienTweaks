package com.greatorator.tolkienmobs.datagen.modelgen;

import com.google.gson.JsonObject;

/** A blockstate file that has been built and can be written out. */
public interface IGeneratedBlockState {
    JsonObject toJson();
}
