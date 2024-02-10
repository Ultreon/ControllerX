package io.github.ultreon.controllerx.impl;

import io.github.ultreon.controllerx.api.ControllerAction;
import io.github.ultreon.controllerx.api.ControllerActions;
import io.github.ultreon.controllerx.api.ControllerMapping;
import io.github.ultreon.controllerx.input.ControllerTrigger;
import net.minecraft.network.chat.Component;

public class EntityTargetControllerContext extends InGameControllerContext {
    public static final EntityTargetControllerContext INSTANCE = new EntityTargetControllerContext();
    public final ControllerMapping<?> attack;

    protected EntityTargetControllerContext() {
        super();

        this.attack = mappings.register(new ControllerMapping<>(ControllerActions.RIGHT_TRIGGER, ControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.attack")));
    }
}
