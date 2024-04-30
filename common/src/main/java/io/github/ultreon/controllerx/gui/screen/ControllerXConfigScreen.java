package io.github.ultreon.controllerx.gui.screen;

import com.ultreon.mods.lib.client.gui.screen.GenericMenuScreen;
import com.ultreon.mods.lib.client.gui.widget.BaseButton;
import io.github.ultreon.controllerx.config.gui.ConfigsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ControllerXConfigScreen extends GenericMenuScreen {
    public ControllerXConfigScreen(Screen screen) {
        super(new Properties().panorama().titleLang("controllerx.config.title").back(screen));

        addButtonRow(
                Component.translatable("controllerx.screen.config.bindings"), this::openBindings,
                Component.translatable("controllerx.screen.config.generic"), this::openGeneric
        );
    }

    private void openGeneric(BaseButton button) {

    }

    private void openBindings(BaseButton button) {
        new ConfigsScreen(this).open();
    }

    @Override
    public void onClose() {
        back();
    }
}
