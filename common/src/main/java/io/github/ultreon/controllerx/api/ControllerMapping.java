package io.github.ultreon.controllerx.api;

import io.github.ultreon.controllerx.util.InputDefinition;
import net.minecraft.network.chat.Component;

public record ControllerMapping<T extends InputDefinition<?>>(ControllerAction<T> action,
                                                              Side side, Component name) {

    @Override
    public String toString() {
        return "ControllerMapping[" +
                "action=" + action + ", " +
                "side=" + side + ", " +
                "name=" + name + ']';
    }

    public enum Side {
        LEFT,
        RIGHT
    }
}
