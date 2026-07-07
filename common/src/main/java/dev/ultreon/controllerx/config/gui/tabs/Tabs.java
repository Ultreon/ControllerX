package dev.ultreon.controllerx.config.gui.tabs;

import dev.ultreon.controllerx.init.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class Tabs extends AbstractContainerEventHandler implements Renderable, NarratableEntry {
    private final List<Tab> tabs = new ArrayList<>();
    private final Consumer<Tabs> focusSetter;
    private Tab currentTab;
    private int current;
    private int tabWidth = 100;
    private final TabsHeader header = new TabsHeader(this);
    private int x, y;
    private int width, height;

    public Tabs(int x, int y, int width, int height, Consumer<Tabs> focusSetter) {
        super();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.focusSetter = focusSetter;
    }

    public void addTab(Tab tab) {
        tabs.add(tab);
        if (currentTab == null) {
            currentTab = tab;
            current = tabs.indexOf(tab);
            focusSetter.accept(this);
        }

        tab.nextFocusPath(new FocusNavigationEvent.InitialFocus());
        tab.resize(width, height);
    }

    public void selectTab(int index) {
        if (index < 0 || index >= tabs.size() || current == index) return;

        currentTab = tabs.get(index);
        current = index;
        focusSetter.accept(this);
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.MENU_TICK.get(), 1));
    }

    public void previousTab() {
        selectTab((current - 1 + tabs.size()) % tabs.size());
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.MENU_TICK.get(), 1));
    }

    public void nextTab() {
        selectTab((current + 1) % tabs.size());
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.MENU_TICK.get(), 1));
    }

    public int getTabWidth() {
        return tabWidth;
    }

    public void setTabWidth(int tabWidth) {
        if (tabWidth < 15) throw new IllegalArgumentException("Tab width must be at least 15");
        this.tabWidth = tabWidth;
    }

    @Override
    public @Nullable ComponentPath nextFocusPath(FocusNavigationEvent event) {
        if (currentTab != null) {
            ComponentPath currentFocusPath = currentTab.getCurrentFocusPath();
            if (event instanceof FocusNavigationEvent.InitialFocus) {
                if (currentFocusPath == null) {
                    currentTab.setFocused(true);
                }
                currentFocusPath = currentTab.getCurrentFocusPath();
                return ComponentPath.path(this, currentFocusPath == null ? currentTab.nextFocusPath(event) : currentFocusPath);
            } else {
                return ComponentPath.path(this, currentTab.nextFocusPath(event));
            }
        }

        return null;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (header.isMouseOver(mouseX, mouseY) && header.mouseScrolled(mouseX, mouseY, delta)) return true;

        return currentTab != null && currentTab.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        header.setY(getY());
        currentTab.setY(getY() + header.getHeight());
        header.render(guiGraphics, mouseX, mouseY, partialTick);
        currentTab.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        if (currentTab == null) return Collections.emptyList();;
        return Collections.singletonList(currentTab);
    }

    public void resize(int width, int height) {
        setSize(width, height);
        header.resize(width);
        for (Tab tab : tabs) {
            tab.resize(width, height);
        }
    }

    private void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getTabCount() {
        return tabs.size();
    }

    public Tab getCurrentTab() {
        return currentTab;
    }

    public Tab getTabAt(int idx) {
        if (idx < 0 || idx >= tabs.size()) throw new IndexOutOfBoundsException("Tab index out of bounds: " + idx);
        return tabs.get(idx);
    }

    public int getSelectedTab() {
        return current;
    }

    public ComponentPath focusTab() {
        return ComponentPath.path(this, currentTab.nextFocusPath(new FocusNavigationEvent.InitialFocus()));
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {

    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Component getMessage() {
        return header.getMessage();
    }
}
