package io.github.ultreon.controllerx.impl;

import io.github.ultreon.controllerx.api.ControllerActions;
import io.github.ultreon.controllerx.api.ControllerMapping;
import net.minecraft.network.chat.Component;

public class InventoryMenuControllerContext extends InGameMenuControllerContext {
    public final ControllerMapping<?> closeInventory;

    public InventoryMenuControllerContext() {
        super();

        this.closeInventory = mappings.register(new ControllerMapping<>(ControllerActions.Y, ControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.inventory.closeInventory")));
    }
}
