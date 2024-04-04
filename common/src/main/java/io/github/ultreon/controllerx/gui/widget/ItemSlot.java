package io.github.ultreon.controllerx.gui.widget;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.ultreon.controllerx.ControllerX;
import io.github.ultreon.controllerx.gui.ControllerInputHandler;
import io.github.ultreon.controllerx.input.ControllerButton;
import io.github.ultreon.controllerx.input.ControllerInput;
import io.github.ultreon.controllerx.input.InputType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

public class ItemSlot extends AbstractWidget implements ControllerInputHandler {
    private final AbstractContainerScreen<?> screen;
    private final Slot slot;

    public ItemSlot(int x, int y, int width, int height, AbstractContainerScreen<?> screen, Slot slot) {
        super(x, y, width, height, Component.empty());
        this.screen = screen;
        this.slot = slot;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        if (ControllerX.get().getInputType() != InputType.CONTROLLER) return;
        if (!this.isFocused()) return;

        gfx.fill(getX(), getY(), getX() + width, getY() + height, 0xff00ff00);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }

    @Override
    public boolean isHovered() {
        return false;
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        return false;
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return false;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        // Item slots don't have a message
    }

    @Override
    public boolean handleInput(ControllerInput input) {
        if (input.isButtonJustPressed(ControllerButton.A)) {
            boolean flag = screen.mouseClicked(getX() + 8, getY() + 8, InputConstants.MOUSE_BUTTON_LEFT);
            flag |= screen.mouseReleased(getX() + 8, getY() + 8, InputConstants.MOUSE_BUTTON_LEFT);
            return flag;
        } else if (input.isButtonJustPressed(ControllerButton.X)) {
            boolean flag = screen.mouseClicked(getX() + 8, getY() + 8, InputConstants.MOUSE_BUTTON_MIDDLE);
            flag |= screen.mouseReleased(getX() + 8, getY() + 8, InputConstants.MOUSE_BUTTON_MIDDLE);
            return flag;
        }
        return false;
    }
}
