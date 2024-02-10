package io.github.ultreon.controllerx.impl;

import io.github.ultreon.controllerx.api.ControllerAction;
import io.github.ultreon.controllerx.api.ControllerMapping;
import io.github.ultreon.controllerx.input.ControllerButton;
import net.minecraft.network.chat.Component;

public class CloseableMenuControllerContext extends MenuControllerContext {
    public static final CloseableMenuControllerContext INSTANCE = new CloseableMenuControllerContext();
    public final ControllerMapping<?> back;

    protected CloseableMenuControllerContext() {
        super();

        this.back = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerButton.B), ControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.menu.back")));
    }
}
