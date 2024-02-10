package io.github.ultreon.controllerx.impl;

import io.github.ultreon.controllerx.api.ControllerActions;
import io.github.ultreon.controllerx.api.ControllerMapping;
import net.minecraft.network.chat.Component;

public class InGameMenuControllerContext extends MenuControllerContext {
    public final ControllerMapping<?> close;

    public InGameMenuControllerContext() {
        super();

        this.close = mappings.register(new ControllerMapping<>(ControllerActions.START, ControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.inGameMenu.close")));
    }
}
