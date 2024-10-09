package dev.ultreon.controllerx.config.gui.tabs;

import com.ultreon.mods.lib.client.gui.widget.AbstractContainerWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Tab extends AbstractWidget implements ContainerEventHandler {
    private final List<GuiEventListener> children = new ArrayList<>();
    private final List<Renderable> renderables = new ArrayList<>();
    private final Component title;
    protected Minecraft minecraft = Minecraft.getInstance();
    private @Nullable GuiEventListener focused;

    public Tab(Component title) {
        super(0, 0, 1, 1, title);
        this.title = title;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        for (Renderable renderable : renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public @Nullable ComponentPath nextFocusPath(FocusNavigationEvent event) {
        if (event instanceof FocusNavigationEvent.InitialFocus) {
            return ContainerEventHandler.super.nextFocusPath(new FocusNavigationEvent.ArrowNavigation(ScreenDirection.DOWN));
        }
        return ContainerEventHandler.super.nextFocusPath(event);
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return children;
    }

    @Override
    public boolean isDragging() {
        return false;
    }

    @Override
    public void setDragging(boolean isDragging) {

    }

    @Override
    public @Nullable GuiEventListener getFocused() {
        return focused;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        this.focused = focused;
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    protected <T extends Renderable & GuiEventListener> T addRenderableWidget(T widget) {
        this.children.add(widget);
        this.renderables.add(widget);
        return widget;
    }

    public <T extends Renderable> T addRenderable(T widget) {
        this.renderables.add(widget);
        return widget;
    }

    public <T extends GuiEventListener> T addWidget(T widget) {
        this.children.add(widget);
        return widget;
    }

    public boolean isEmpty() {
        return this.children.isEmpty() && this.renderables.isEmpty();
    }
}
