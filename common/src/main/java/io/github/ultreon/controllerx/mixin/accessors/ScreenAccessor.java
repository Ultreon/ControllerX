package io.github.ultreon.controllerx.mixin.accessors;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.List;

@Mixin(Screen.class)
public interface ScreenAccessor {
    @Accessor(value = "renderables", remap = true)
    List<Renderable> getRenderables();

    @Accessor(value = "children", remap = true)
    List<GuiEventListener> getChildren();
}
