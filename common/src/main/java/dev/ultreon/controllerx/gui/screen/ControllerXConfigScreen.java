package dev.ultreon.controllerx.gui.screen;

import dev.ultreon.controllerx.config.gui.BindingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ControllerXConfigScreen extends Screen {
    private final Screen backScreen;

    private Button bindingsButton;
    private Button genericButton;

    public ControllerXConfigScreen(Screen screen) {
        super(Component.translatable("controllerx.config.title"));

        this.backScreen = screen;
    }

    @Override
    protected void init() {
        super.init();

        this.bindingsButton = addWidget(Button.builder(
                Component.translatable("controllerx.screen.config.bindings"),
                this::openBindings
        ).bounds(width / 2 - 100, height - 30, 200, 20).build());
        this.genericButton = addWidget(Button.builder(
                Component.translatable("controllerx.screen.config.generic"),
                this::openGeneric
        ).bounds(width / 2 + 5, height - 30, 200, 20).build());
    }

    private void openGeneric(Button button) {

    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawCenteredString(font, Component.translatable("controllerx.config.title"), width / 2, 10, 0xffffffff);
    }

    private void openBindings(Button button) {
        new BindingsScreen(this).open();
    }

    @Override
    public void onClose() {
        back();
    }

    public void back() {
        Minecraft mc = this.minecraft;
        if (mc != null) {
            mc.setScreen(backScreen);
        }
    }

    public Button getGenericButton() {
        return genericButton;
    }

    public Button getBindingsButton() {
        return bindingsButton;
    }
}
