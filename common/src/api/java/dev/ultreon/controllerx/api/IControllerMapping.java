package dev.ultreon.controllerx.api;

import dev.ultreon.controllerx.api.input.dyn.IControllerInterDynamic;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public interface IControllerMapping<T extends Enum<T> & IControllerInterDynamic<?>> {
    IControllerAction<T> getAction();

    boolean isVisible();

    @NotNull IControllerAction<T> getActionDirect();

    IControllerMapping.Side getSide();

    Icon getIcon();

    Component getName();

    enum Side {
        LEFT,
        RIGHT
    }
}
