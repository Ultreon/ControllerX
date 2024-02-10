package io.github.ultreon.controllerx.mixin;

import io.github.ultreon.controllerx.ControllerX;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(at = @At("HEAD"), method = "handleKeybinds")
    public void handleKeybinds(CallbackInfo ci) {
//        ControllerX.get().input.update();
    }
}
