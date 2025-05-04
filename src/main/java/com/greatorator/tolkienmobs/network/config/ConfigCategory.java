package com.greatorator.tolkienmobs.network.config;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public interface ConfigCategory extends ConfigTag {

    boolean has(String name);

    @Nullable
    ConfigTag findTag(String name);

    ConfigCategory getCategory(String name);

    @Nullable
    ConfigCategory findCategory(String name);

    ConfigValue getValue(String name);

    @Nullable
    ConfigValue findValue(String name);

    ConfigValueList getValueList(String name);

    @Nullable
    ConfigValueList findValueList(String name);

    Collection<ConfigTag> getChildren();

    ConfigCategory delete(String name);

    void clear();

    @Override
    ConfigCategory syncTagToClient();

    ConfigCategory onSync(ConfigCallback<ConfigCategory> callback);

    @Override
    ConfigCategory setComment(String comment);

    @Override
    ConfigCategory setComment(String... comment);

    @Override
    ConfigCategory setComment(List<String> comment);
}
