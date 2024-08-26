package io.github.ultreon.controllerx;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.MutableComponent;

public class IncomatibilityWarning extends Screen {
    private final MutableComponent title;
    private final MutableComponent description;

    public IncomatibilityWarning(MutableComponent title, MutableComponent description) {
        super(title);
        this.title = title;
        this.description = description;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(Button.builder(CommonComponents.GUI_PROCEED, button -> {
            if (this.minecraft != null) {
                ControllerX.get().initMod();
                ControllerX.get().skippedWarning = true;
                this.minecraft.setScreen(null);
            }
        }).bounds(width / 2 - 50, 3 * height / 4, 100, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        String descFormat = font.substrByWidth(description, width - 20).getString();
        guiGraphics.drawCenteredString(font, title, width / 2, height / 4, 0xffffff);
        guiGraphics.drawCenteredString(font, descFormat, width / 2, height / 4 + 20, 0xffffff);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
