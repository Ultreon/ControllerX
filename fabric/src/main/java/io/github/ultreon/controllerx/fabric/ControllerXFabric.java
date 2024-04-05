package io.github.ultreon.controllerx.fabric;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import io.github.ultreon.controllerx.ControllerX;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraftforge.fml.config.ModConfig;

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