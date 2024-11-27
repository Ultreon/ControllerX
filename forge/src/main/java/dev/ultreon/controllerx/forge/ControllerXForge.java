package dev.ultreon.controllerx.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.ultreon.controllerx.ControllerX;
import dev.ultreon.controllerx.gui.screen.ControllerXConfigScreen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ControllerX.MOD_ID)
public class ControllerXForge {
    private final ControllerX instance;

    public ControllerXForge() {
		// Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(ControllerX.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        instance = ControllerX.get();

        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> new ControllerXConfigScreen(screen))
        );
    }

    public ControllerX getInstance() {
        return instance;
    }
}