package io.github.ultreon.controllerx;

import dev.architectury.hooks.client.screen.ScreenAccess;
import io.github.ultreon.controllerx.gui.widget.ItemSlot;
import io.github.ultreon.controllerx.input.ControllerAxis;
import io.github.ultreon.controllerx.input.ControllerButton;
import io.github.ultreon.controllerx.input.ControllerInput;
import io.github.ultreon.controllerx.input.InputType;
import io.github.ultreon.controllerx.mixin.accessors.AbstractContainerScreenAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class Hooks {
    public static void hookControllerInput(KeyMapping mapping, CallbackInfoReturnable<Boolean> cir) {
        Minecraft mc = Minecraft.getInstance();
        ControllerInput input = ControllerX.get().controllerInput;
        if (cir.getReturnValueZ()) {
            ControllerX.get().setInputType(InputType.KEYBOARD_AND_MOUSE);
            return;
        } else if (!input.isConnected()) {
            ControllerX.get().setInputType(InputType.KEYBOARD_AND_MOUSE);
            return;
        }
        Boolean b = input.doInput(mc, mapping, eitherAxisOrBtn -> {
            if (eitherAxisOrBtn.isLeftPresent()) {
                ControllerAxis controllerAxis = eitherAxisOrBtn.getLeft();
                return input.getAxis(controllerAxis) > 0f;
            }
            if (eitherAxisOrBtn.isRightPresent()) {
                ControllerButton controllerButton = eitherAxisOrBtn.getRight();
                return input.isButtonPressed(controllerButton);
            }
            return null;
        });

        if (b == null)
            return;
        if (b && ControllerX.get().getInputType() != InputType.CONTROLLER)
            ControllerX.get().setInputType(InputType.CONTROLLER);
        if (ControllerX.get().getInputType() == InputType.CONTROLLER || !b)
            cir.setReturnValue(b);
    }

    public static <T extends AbstractContainerMenu> void hookContainerSlots(AbstractContainerScreen<T> containerScreen, ScreenAccess screenAccess) {
        Minecraft mc = Minecraft.getInstance();

        if (ControllerX.get().getInputType() == InputType.CONTROLLER) {
            T menu = containerScreen.getMenu();

            for (Slot slot : menu.slots) {
                int x = slot.x + ((AbstractContainerScreenAccessor) containerScreen).getLeftPos();
                int y = slot.y + ((AbstractContainerScreenAccessor) containerScreen).getTopPos();

                screenAccess.addRenderableWidget(new ItemSlot(x, y, 16, 16, containerScreen, slot));
            }
        }
    }

    public static void hookFloatingItemRender(GuiGraphics gfx, int x, int y) {
        if (ControllerX.get().getInputType() != InputType.CONTROLLER) return;

        gfx.pose().scale(2, 2, 2);
        gfx.pose().translate(-x / 2f - 4, -y / 2f - 4, 0);
    }
}
