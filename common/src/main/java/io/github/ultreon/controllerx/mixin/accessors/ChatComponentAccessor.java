package io.github.ultreon.controllerx.mixin.accessors;

import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChatComponent.class)
public interface ChatComponentAccessor {
    @Invoker("getLineHeight")
    int invokeGetLineHeight();
}
