package dev.ultreon.controllerx.impl;

import dev.ultreon.controllerx.ControllerX;
import dev.ultreon.controllerx.api.ControllerAction;
import dev.ultreon.controllerx.api.ControllerContext;
import dev.ultreon.controllerx.api.ControllerMapping;
import dev.ultreon.controllerx.input.ControllerBoolean;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ChatControllerContext extends ControllerContext {
    public static final ControllerContext INSTANCE = new ChatControllerContext(ControllerX.res("chat"));
    public final ControllerMapping<?> send;
    public final ControllerMapping<?> openKeyboard;
    public final ControllerMapping<?> close;

    public ChatControllerContext(ResourceLocation id) {
        super(id);

        this.send = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.A), ControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.chat.send"), "send"));
        this.openKeyboard = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.Y), ControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.chat.open_keyboard"), "open_keyboard"));
        this.close = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.B), ControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.menu.close"), "close"));
    }

    @Override
    public int getYOffset() {
        return 32;
    }
}
