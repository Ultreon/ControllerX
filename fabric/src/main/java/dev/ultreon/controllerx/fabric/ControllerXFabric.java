package dev.ultreon.controllerx.fabric;

import dev.ultreon.controllerx.api.IControllerX;
import net.fabricmc.api.ClientModInitializer;

public class ControllerXFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        IControllerX.get();
    }
}