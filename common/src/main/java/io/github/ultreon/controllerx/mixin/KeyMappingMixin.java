package io.github.ultreon.controllerx.mixin;

import io.github.ultreon.controllerx.ControllerX;
import io.github.ultreon.controllerx.input.MixinClickHandler;
import io.github.ultreon.controllerx.input.ControllerAxis;
import io.github.ultreon.controllerx.input.ControllerButton;
import io.github.ultreon.controllerx.input.ControllerInput;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyMapping.class)
public abstract class KeyMappingMixin implements MixinClickHandler {
    @Shadow private int clickCount;

    @Inject(method = "isDown", at = @At("RETURN"), cancellable = true)
    private void onIsDown(CallbackInfoReturnable<Boolean> cir) {
        Minecraft mc = Minecraft.getInstance();
        ControllerInput input = ControllerX.getInput();
        if (input.isConnected()) {
            Boolean b = input.doInput(mc, (KeyMapping) (Object) this, eitherAxisOrBtn -> {
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
            if (b != null) cir.setReturnValue(b);
        }
    }

    @Override
    public void controllerX$handleClick() {
        clickCount++;
    }
}
