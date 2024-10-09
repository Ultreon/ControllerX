package dev.ultreon.controllerx;

import dev.architectury.hooks.client.screen.ScreenAccess;
import dev.ultreon.controllerx.gui.widget.ItemSlot;
import dev.ultreon.controllerx.input.ControllerInput;
import dev.ultreon.controllerx.input.InputType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

import java.util.Optional;

public class Hooks {
    public static Optional<Boolean> hookControllerInput(KeyMapping mapping, boolean isKeyDown) {
        if (!ControllerX.get().skippedWarning) return Optional.empty();

        Minecraft mc = Minecraft.getInstance();
        ControllerInput input = ControllerX.get().input;
        if (isKeyDown) {
            ControllerX.get().setInputType(InputType.KEYBOARD_AND_MOUSE);
            return Optional.empty();
        } else if (!input.isConnected()) {
            ControllerX.get().setInputType(InputType.KEYBOARD_AND_MOUSE);
            return Optional.empty();
        }

        if (ControllerX.get().getInputType() != InputType.KEYBOARD_AND_MOUSE) {
            return Optional.of(ControllerX.get().input.isDown(mc, mapping));
        }

        return Optional.empty();
    }

    public static <T extends AbstractContainerMenu> void hookContainerSlots(AbstractContainerScreen<T> containerScreen, ScreenAccess screenAccess) {
        if (!ControllerX.get().skippedWarning) return;

        Minecraft mc = Minecraft.getInstance();

        if (ControllerX.get().getInputType() == InputType.CONTROLLER) {
            T menu = containerScreen.getMenu();

            for (Slot slot : menu.slots) {
                screenAccess.addRenderableWidget(ItemSlot.getSlot(containerScreen, slot));
            }
        }
    }

    public static void hookFloatingItemRender(GuiGraphics gfx, int x, int y) {
        if (ControllerX.get().getInputType() != InputType.CONTROLLER) return;

        final float scale = 1.5f;
        gfx.pose().scale(scale, scale, scale);
        gfx.pose().translate(-x / 3.0f - 8 / 3.0f, -y / 3.0f - 8 / 3.0f, 0);
    }

    public static boolean isOnSlot(AbstractContainerScreen<?> screen) {
        if (ControllerX.get().getInputType() != InputType.CONTROLLER) return false;
        return screen.getFocused() instanceof ItemSlot;
    }
}
