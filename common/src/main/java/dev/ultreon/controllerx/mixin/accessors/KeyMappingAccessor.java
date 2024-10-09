package dev.ultreon.controllerx.mixin.accessors;

import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(KeyMapping.class)
public interface KeyMappingAccessor {
    @Accessor("CATEGORIES")
    static Set<String> getCategoryNames() {
        throw new AssertionError("Accessor method getCategoryNames() is not implemented by mixin.");
    }
}
