package dev.ultreon.controllerx.mixin.accessors;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Screen.class)
public interface ScreenAccessor {
    @Accessor(value = "renderables")
    List<Renderable> getRenderables();

    @Accessor(value = "children")
    List<GuiEventListener> getChildren();
}
