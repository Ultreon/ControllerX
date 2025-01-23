package dev.ultreon.controllerx.fabric;

import dev.ultreon.controllerx.ControllerX;
import dev.ultreon.controllerx.api.IControllerX;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

public class ControllerXFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        IControllerX.get();
    }
}