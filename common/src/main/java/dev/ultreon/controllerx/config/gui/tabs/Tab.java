package dev.ultreon.controllerx.config.gui.tabs;

import com.ultreon.mods.lib.client.gui.widget.AbstractContainerWidget;
import dev.ultreon.controllerx.config.Config;
import dev.ultreon.controllerx.config.gui.BindingsConfigList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Tab extends AbstractContainerWidget {
    private final List<Renderable> children = new ArrayList<>();
    private final Component title;
    protected Minecraft minecraft = Minecraft.getInstance();

    public Tab(Component title) {
        super(0, 0, 1, 1, title);
        this.title = title;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return List.of();
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    protected <T extends Renderable> T addWidget(T widget) {
        this.children.add(widget);
        return widget;
    }
}
