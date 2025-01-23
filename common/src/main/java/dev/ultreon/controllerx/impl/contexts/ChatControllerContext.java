package dev.ultreon.controllerx.impl.contexts;

import dev.ultreon.controllerx.ControllerX;
import dev.ultreon.controllerx.api.ControllerContext;
import dev.ultreon.controllerx.api.IControllerMapping;
import dev.ultreon.controllerx.api.input.ControllerBoolean;
import dev.ultreon.controllerx.impl.ControllerAction;
import dev.ultreon.controllerx.impl.ControllerMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ChatControllerContext extends ControllerContext {
    public static final ControllerContext INSTANCE = new ChatControllerContext(ControllerX.res("chat"));
    public final ControllerMapping<?> send;
    public final ControllerMapping<?> openKeyboard;
    public final ControllerMapping<?> close;

    public ChatControllerContext(ResourceLocation id) {
        super(id);

        send = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.A), IControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.chat.send"), "send"));
        openKeyboard = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.Y), IControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.chat.open_keyboard"), "open_keyboard"));
        close = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerBoolean.B), IControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.menu.close"), "close"));
    }

    @Override
    public int getYOffset() {
        return 32;
    }
}
