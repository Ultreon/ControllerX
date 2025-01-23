package dev.ultreon.controllerx.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.InputConstants;
import dev.ultreon.controllerx.ControllerX;
import dev.ultreon.controllerx.api.input.InputType;
import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @WrapOperation(method = "keyPress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;set(Lcom/mojang/blaze3d/platform/InputConstants$Key;Z)V"))
    private void onKeyPress(InputConstants.Key key, boolean held, Operation<Void> original) {
        if (ControllerX.get().getInputType() == InputType.CONTROLLER) {
            ControllerX.get().setInputType(InputType.KEYBOARD_AND_MOUSE);
            original.call(key, held);
        } else {
            original.call(key, held);
        }
    }
}
