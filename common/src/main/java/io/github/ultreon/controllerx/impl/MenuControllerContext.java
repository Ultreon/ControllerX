package io.github.ultreon.controllerx.impl;

import dev.architectury.platform.Platform;
import io.github.ultreon.controllerx.GameApi;
import io.github.ultreon.controllerx.api.ControllerAction;
import io.github.ultreon.controllerx.api.ControllerContext;
import io.github.ultreon.controllerx.api.ControllerMapping;
import io.github.ultreon.controllerx.input.ControllerAxis;
import io.github.ultreon.controllerx.input.ControllerButton;
import io.github.ultreon.controllerx.input.ControllerJoystick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;

public class MenuControllerContext extends ControllerContext {
    public static final MenuControllerContext INSTANCE = new MenuControllerContext();
    public final ControllerMapping<?> joystickMove;
    public final ControllerMapping<?> dpadMove;
    public final ControllerMapping<?> activate;
    public final ControllerMapping<?> scrollY;

    protected MenuControllerContext() {
        super();

        this.joystickMove = mappings.register(new ControllerMapping<>(new ControllerAction.Joystick(ControllerJoystick.Left), ControllerMapping.Side.LEFT, Component.translatable("controllerx.action.menu.joystick_move")));
        this.dpadMove = mappings.register(new ControllerMapping<>(new ControllerAction.Joystick(ControllerJoystick.Dpad), ControllerMapping.Side.LEFT, Component.translatable("controllerx.action.menu.dpad_move")));
        this.activate = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerButton.A), ControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.menu.activate")));
        this.scrollY = mappings.register(new ControllerMapping<>(new ControllerAction.Axis(ControllerAxis.RightStickY), ControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.menu.scroll_y")));
    }

    @Override
    public int getYOffset() {
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof ChatScreen) {
            return screen.height;
        }

        if (screen instanceof TitleScreen) {
            if (Platform.isForge()) return 36;
            return 12;
        }

        return super.getYOffset();
    }
}
