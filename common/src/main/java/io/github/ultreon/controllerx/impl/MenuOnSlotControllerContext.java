package io.github.ultreon.controllerx.impl;

import io.github.ultreon.controllerx.api.ControllerAction;
import io.github.ultreon.controllerx.api.ControllerMapping;
import io.github.ultreon.controllerx.input.ControllerButton;
import net.minecraft.network.chat.Component;

public class MenuOnSlotControllerContext extends CloseableMenuControllerContext {
    public static final MenuOnSlotControllerContext INSTANCE = new MenuOnSlotControllerContext();
    public final ControllerMapping<ControllerButton> pickupOrPlace;
    public final ControllerMapping<ControllerButton> splitOrPutSingle;
    public final ControllerMapping<ControllerButton> drop;

    protected MenuOnSlotControllerContext() {
        super();

        this.pickupOrPlace = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerButton.A), ControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.menuOnSlot.pickupOrPlace")));
        this.splitOrPutSingle = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerButton.X), ControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.menuOnSlot.splitOrPutOne")));
        this.drop = mappings.register(new ControllerMapping<>(new ControllerAction.Button(ControllerButton.DPAD_DOWN), ControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.menuOnSlot.drop")));
    }
}
