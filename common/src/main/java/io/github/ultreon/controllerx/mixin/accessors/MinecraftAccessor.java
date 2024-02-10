package io.github.ultreon.controllerx.mixin.accessors;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = Minecraft.class, remap = false)
public interface MinecraftAccessor {
    @Invoker(value = "startAttack", remap = true)
    boolean invokeStartAttack();
}
