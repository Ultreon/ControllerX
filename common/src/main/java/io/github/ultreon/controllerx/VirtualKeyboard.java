package io.github.ultreon.controllerx;

import io.github.ultreon.controllerx.gui.screen.TextInputScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Overlay;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class VirtualKeyboard extends Overlay {
    private final TextInputScreen screen;

    VirtualKeyboard() {
        this.screen = new TextInputScreen(this);
    }

    public void open(VirtualKeyboardEditCallback callback, VirtualKeyboardSubmitCallback submitCallback) {
        this.screen.setSubmitCallback(submitCallback);
        this.screen.setEditCallback(callback);
        this.screen.init(Minecraft.getInstance(), Minecraft.getInstance().getWindow().getGuiScaledWidth(), Minecraft.getInstance().getWindow().getGuiScaledHeight());
    }

    public void close() {
        this.screen.close();
        this.screen.setSubmitCallback(() -> {});
        this.screen.setEditCallback(input -> {});
        ControllerX.get().input.handleVirtualKeyboardClosed(this.screen.getInput());
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 2000);
        guiGraphics.fill(0, 0, Minecraft.getInstance().getWindow().getGuiScaledWidth(), Minecraft.getInstance().getWindow().getGuiScaledHeight(), 0x80000000);
        this.screen.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.pose().popPose();
    }

    public TextInputScreen getScreen() {
        return screen;
    }
}
