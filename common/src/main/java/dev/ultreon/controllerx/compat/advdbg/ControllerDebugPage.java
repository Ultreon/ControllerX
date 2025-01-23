package dev.ultreon.controllerx.compat.advdbg;

import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext;
import dev.ultreon.controllerx.ControllerX;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public class ControllerDebugPage extends DebugPage {
    @Override
    public void render(@NotNull GuiGraphics gfx, IDebugRenderContext ctx) {
        ctx.left("ControllerX");
        ctx.left("Input Type", ControllerX.get().getInputType());
        ctx.left("Input Available", ControllerX.get().input.isAvailable());
        ctx.left("Controller Connected", ControllerX.get().input.isConnected());
        ctx.left("Virtual Keyboard Open", ControllerX.get().input.isVirtualKeyboardOpen());
        ctx.left("Skipped Warning", ControllerX.get().skippedWarning);
    }
}
