package io.github.ultreon.controllerx.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class GenericOptionsScreen extends BaseConfigScreen {
    protected GenericOptionsScreen() {
        super(Component.translatable("controllerx.screen.config.generic.title"));
    }

    @Override
    public void render(@NotNull GuiGraphics gfx, int mouseX, int mouseY, float partialTicks) {
        renderBackground(gfx, partialTicks);
    }

    @Override
    protected boolean hasSearch() {
        return false;
    }

    @Override
    protected int getCount() {
        return 0;
    }
}
