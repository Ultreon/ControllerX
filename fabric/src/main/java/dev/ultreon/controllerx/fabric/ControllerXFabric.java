package dev.ultreon.controllerx.fabric;

import dev.ultreon.controllerx.ControllerX;
import net.fabricmc.api.ClientModInitializer;

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