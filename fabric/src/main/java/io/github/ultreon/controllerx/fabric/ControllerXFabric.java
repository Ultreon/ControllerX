package io.github.ultreon.controllerx.fabric;

import io.github.ultreon.controllerx.ControllerX;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

public class ControllerXFabric implements ClientModInitializer {
    private ControllerX instance;

    @Override
    public void onInitializeClient() {
        instance = ControllerX.get();
    }

    public ControllerX getInstance() {
        return instance;
    }
}