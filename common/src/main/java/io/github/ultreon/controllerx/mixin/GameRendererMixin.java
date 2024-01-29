package io.github.ultreon.controllerx.mixin;

import io.github.ultreon.controllerx.ControllerX;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements AutoCloseable {
    @Inject(method = "render", at = @At("HEAD"))
    private void onTick(float f, long l, boolean bl, CallbackInfo ci) {
        ControllerX.getInput().update();
    }
}
