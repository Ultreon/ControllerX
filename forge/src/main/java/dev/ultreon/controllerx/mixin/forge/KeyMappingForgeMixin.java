package dev.ultreon.controllerx.mixin.forge;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.extensions.IForgeKeyMapping;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(KeyMapping.class)
public abstract class KeyMappingForgeMixin implements IForgeKeyMapping {

}
