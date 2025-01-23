package dev.ultreon.controllerx.compat.advdbg;

import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext;
import dev.ultreon.controllerx.ControllerX;
import dev.ultreon.controllerx.api.input.ControllerBoolean;
import dev.ultreon.controllerx.api.input.ControllerSignedFloat;
import dev.ultreon.controllerx.api.input.ControllerVec2;
import dev.ultreon.controllerx.input.ControllerInput;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public class ControllerDebugPage extends DebugPage {
    @Override
    public void render(@NotNull GuiGraphics gfx, IDebugRenderContext ctx) {
        ctx.left("ControllerX");
        ctx.left("Input Type", ControllerX.get().getInputType());
        ControllerInput input = ControllerX.get().input;
        if (input == null) {
            ctx.left("Input Initialized", false);
            return;
        }
        ctx.left("Input Initialized", true);
        ctx.left("Input Available", input.isAvailable());
        ctx.left("Controller Connected", input.isConnected());
        ctx.left("Virtual Keyboard Open", input.isVirtualKeyboardOpen());
        ctx.left("Skipped Warning", ControllerX.get().skippedWarning);

        ctx.left("Controller Joysticks");
        for (ControllerVec2 axis : ControllerVec2.values()) {
            ctx.left(axis.name(), input.getJoystick(axis));
        }

        ctx.left("Controller Axes");
        for (ControllerSignedFloat axis : ControllerSignedFloat.values()) {
            ctx.left(axis.name(), input.getAxis1(axis));
        }

        ctx.right("Controller Buttons");
        for (ControllerBoolean button : ControllerBoolean.values()) {
            ctx.right(button.name(), input.isButtonPressed(button));
        }

        ctx.left("Controller Triggers");
    }
}
