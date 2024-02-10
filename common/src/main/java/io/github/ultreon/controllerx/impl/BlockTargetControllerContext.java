package io.github.ultreon.controllerx.impl;

import io.github.ultreon.controllerx.api.ControllerActions;
import io.github.ultreon.controllerx.api.ControllerMapping;
import net.minecraft.network.chat.Component;

public class BlockTargetControllerContext extends InGameControllerContext {
    public static final BlockTargetControllerContext INSTANCE = new BlockTargetControllerContext();
    public final ControllerMapping<?> destroyBlock;

    public BlockTargetControllerContext() {
        super();

        this.destroyBlock = mappings.register(new ControllerMapping<>(ControllerActions.RIGHT_TRIGGER, ControllerMapping.Side.RIGHT, Component.translatable("controllerx.action.inGame.destroyBlock")));
    }
}
