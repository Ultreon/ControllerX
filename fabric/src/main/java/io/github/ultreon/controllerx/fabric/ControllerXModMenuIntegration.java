package io.github.ultreon.controllerx.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.ultreon.controllerx.gui.screen.ControllerXConfigScreen;

public class ControllerXModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ControllerXConfigScreen::new;
    }
}
