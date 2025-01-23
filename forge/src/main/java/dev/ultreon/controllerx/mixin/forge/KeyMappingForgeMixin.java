package dev.ultreon.controllerx.mixin.forge;

import dev.ultreon.controllerx.Hooks;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.extensions.IForgeKeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyMapping.class)
public abstract class KeyMappingForgeMixin implements IForgeKeyMapping {

}
