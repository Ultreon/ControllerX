package io.github.ultreon.controllerx.mixin.accessors;

import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractSelectionList.class)
public interface AbstractSelectionListAccessor {
    @Accessor("y0")
    int getY0();

    @Accessor("x0")
    int getX0();
}
