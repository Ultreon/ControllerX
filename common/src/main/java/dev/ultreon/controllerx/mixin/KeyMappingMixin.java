package dev.ultreon.controllerx.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.injectables.annotations.PlatformOnly;
import dev.ultreon.controllerx.Hooks;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyMapping.class)
public abstract class KeyMappingMixin {
    @Inject(method = "releaseAll", at = @At("RETURN"))
    private static void onReleaseAll(CallbackInfo ci) {
        Hooks.hookReleaseAll();
    }

    @Inject(method = "click", at = @At("HEAD"))
    private static void onClick(InputConstants.Key key, CallbackInfo ci) {
        System.out.println("Key clicked: " + key.getName());
    }

    @Inject(method = "set", at = @At("HEAD"))
    private static void onSet(InputConstants.Key key, boolean held, CallbackInfo ci) {
        System.out.println("Key set: " + key.getName() + " " + held);
    }
}
