package com.greatorator.tolkienmobs.network.config;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public interface ConfigValue extends ConfigTag {

    ValueType getType();

    boolean getBoolean();

    String getString();

    int getInt();

    long getLong();

    int getHex();

    double getDouble();

    ConfigValue setBoolean(boolean value);

    ConfigValue setString(String value);

    ConfigValue setInt(int value);

    ConfigValue setLong(long value);

    ConfigValue setHex(int value);

    ConfigValue setDouble(double value);

    boolean getDefaultBoolean();

    String getDefaultString();

    int getDefaultInt();

    long getDefaultLong();

    int getDefaultHex();

    double getDefaultDouble();

    ConfigValue setDefaultBoolean(boolean value);

    ConfigValue setDefaultString(String value);

    ConfigValue setDefaultInt(int value);

    ConfigValue setDefaultLong(long value);

    ConfigValue setDefaultHex(int value);

    ConfigValue setDefaultDouble(double value);

    ConfigValue setRestriction(Restriction restriction);

    @Nullable
    Restriction getRestriction();

    @Override
    ConfigValue syncTagToClient();

    ConfigValue onSync(ConfigCallback<ConfigValue> callback);

    @Override
    ConfigValue setComment(String comment);

    @Override
    ConfigValue setComment(String... comment);

    @Override
    ConfigValue setComment(List<String> comment);
}
