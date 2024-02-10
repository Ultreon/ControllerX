package io.github.ultreon.controllerx.impl;

import io.github.ultreon.controllerx.api.ControllerActions;
import io.github.ultreon.controllerx.api.ControllerContext;
import io.github.ultreon.controllerx.api.ControllerMapping;
import net.minecraft.network.chat.Component;

public class ChatControllerContext extends ControllerContext {
    public static final ControllerContext INSTANCE = new ChatControllerContext();
    public final ControllerMapping<?> send;
    public final ControllerMapping<?> openKeyboard;
    public final ControllerMapping<?> close;

    public ChatControllerContext() {
        super();

        this.send = mappings.register(new ControllerMapping<>(ControllerActions.A, ControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.chat.send")));
        this.openKeyboard = mappings.register(new ControllerMapping<>(ControllerActions.Y, ControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.chat.open_keyboard")));
        this.close = mappings.register(new ControllerMapping<>(ControllerActions.B, ControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.inGameMenu.close")));
    }

    @Override
    public int getYOffset() {
        return 32;
    }
}
