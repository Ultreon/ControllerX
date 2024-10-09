package dev.ultreon.controllerx.mixin;

import dev.ultreon.controllerx.ControllerX;
import dev.ultreon.controllerx.input.ControllerInput;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {
    @Unique
    private boolean controllerX$wasJumpByController = false;

    @Inject(method = "tick", at = @At("RETURN"))
    private void onTick(boolean changedSpeed, float speedModifier, CallbackInfo ci) {
        ControllerInput input = ControllerX.get().input;
        if (input.isConnected()) {
            if (input.forwardImpulse != 0f || input.leftImpulse != 0f) {
                this.forwardImpulse = input.forwardImpulse;
                this.leftImpulse = input.leftImpulse;
            }
            if (input.jumping) this.controllerX$wasJumpByController = true;
            if (controllerX$wasJumpByController) this.jumping = true;
            if (!input.jumping) controllerX$wasJumpByController = false;
        }
    }
}
