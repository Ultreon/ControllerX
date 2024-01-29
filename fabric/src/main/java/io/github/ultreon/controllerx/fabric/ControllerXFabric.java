package io.github.ultreon.controllerx.fabric;

import io.github.ultreon.controllerx.ControllerX;
import net.fabricmc.api.ModInitializer;

public class ControllerXFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ControllerX.init();
    }
}