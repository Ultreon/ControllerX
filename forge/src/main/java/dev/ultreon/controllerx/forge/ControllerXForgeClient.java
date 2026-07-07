package dev.ultreon.controllerx.forge;

import dev.ultreon.controllerx.api.IControllerX;
import dev.ultreon.controllerx.gui.screen.ControllerXConfigScreen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;

public class ControllerXForgeClient {
    public static void init() {
        IControllerX.get();
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> new ControllerXConfigScreen(screen))
        );
    }
}
