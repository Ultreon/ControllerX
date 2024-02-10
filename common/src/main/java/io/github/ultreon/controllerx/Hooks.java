package io.github.ultreon.controllerx;

import io.github.ultreon.controllerx.input.ControllerAxis;
import io.github.ultreon.controllerx.input.ControllerButton;
import io.github.ultreon.controllerx.input.ControllerInput;
import io.github.ultreon.controllerx.input.InputType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class Hooks {
    public static void hookControllerInput(KeyMapping mapping, CallbackInfoReturnable<Boolean> cir) {
        Minecraft mc = Minecraft.getInstance();
        ControllerInput input = ControllerX.get().controllerInput;
        if (cir.getReturnValueZ()) {
            ControllerX.get().setInputType(InputType.KEYBOARD_AND_MOUSE);
        } else if (input.isConnected()) {
            Boolean b = input.doInput(mc, mapping, eitherAxisOrBtn -> {
                if (eitherAxisOrBtn.isLeftPresent()) {
                    ControllerAxis controllerAxis = eitherAxisOrBtn.getLeft();
                    return input.getAxis(controllerAxis) > 0f;
                }
                if (eitherAxisOrBtn.isRightPresent()) {
                    ControllerButton controllerButton = eitherAxisOrBtn.getRight();
                    return input.isButtonPressed(controllerButton);
                }
                return null;
            });

            if (b != null) {
                if (b && ControllerX.get().getInputType() != InputType.CONTROLLER)
                    ControllerX.get().setInputType(InputType.CONTROLLER);
                if (ControllerX.get().getInputType() == InputType.CONTROLLER || !b)
                    cir.setReturnValue(b);
            }
        } else {
            ControllerX.get().setInputType(InputType.KEYBOARD_AND_MOUSE);
        }
    }
}
