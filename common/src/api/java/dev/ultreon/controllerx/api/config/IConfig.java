package dev.ultreon.controllerx.api.config;

import net.minecraft.network.chat.Component;

public interface IConfig {
    Component getTitle();

    void load();
    void save();
}
