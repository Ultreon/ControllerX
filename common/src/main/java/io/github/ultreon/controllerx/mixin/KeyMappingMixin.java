package io.github.ultreon.controllerx.mixin;

import io.github.ultreon.controllerx.Hooks;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyMapping.class)
public abstract class KeyMappingMixin {
    @Inject(method = "isDown", at = @At("RETURN"), cancellable = true)
    private void onIsDown(CallbackInfoReturnable<Boolean> cir) {
        Hooks.hookControllerInput((KeyMapping) (Object) this, cir.getReturnValueZ()).ifPresent(cir::setReturnValue);
    }
}
