package dev.ultreon.controllerx.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.ultreon.controllerx.gui.screen.ControllerXConfigScreen;

public class ControllerXModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ControllerXConfigScreen::new;
    }
}
