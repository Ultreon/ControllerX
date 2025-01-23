package dev.ultreon.controllerx.config.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.ultreon.mods.lib.util.KeyboardHelper;
import dev.ultreon.controllerx.api.ControllerContext;
import dev.ultreon.controllerx.config.Config;
import dev.ultreon.controllerx.config.gui.tabs.Tab;
import dev.ultreon.controllerx.config.gui.tabs.Tabs;
import dev.ultreon.controllerx.impl.contexts.InGameControllerContext;
import dev.ultreon.controllerx.mixin.accessors.KeyMappingAccessor;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BindingsScreen extends Screen {
    private final List<BindingsTab> allTabs = new ArrayList<>();
    private final Screen back;
    private BindingsList list;
    private Button doneButton;
    private Tabs tabs;

    public BindingsScreen(Screen back) {
        super(Component.translatable("controllerx.screen.config.bindings.title"));
        this.back = back;
    }

    @Override
    public void onClose() {
        assert minecraft != null;
        minecraft.setScreen(back);
    }

    @Override
    protected void init() {
        clearWidgets();
        super.init();

        if (tabs != null) {
            tabs.resize(width, height - 70);

            addRenderableWidget(tabs);
        } else {
            tabs = new Tabs(0, 20, width, height - 70, this::setFocus);

            for (ControllerContext context : ControllerContext.getContexts()) {
                BindingsTab tab = new BindingsTab(context, null);
                if (tab.isEmpty()) continue;
                allTabs.add(tab);
                tabs.addTab(tab);
            }

            KeyMappingAccessor.getCategoryNames().stream().sorted((a, b) -> {
                String nameA = Language.getInstance().getOrDefault(a);
                String nameB = Language.getInstance().getOrDefault(b);
                return nameA.compareToIgnoreCase(nameB);
            }).forEach(category -> {
                BindingsTab tab = new BindingsTab(InGameControllerContext.INSTANCE, category);
                if (tab.isEmpty()) return;
                allTabs.add(tab);
                tabs.addTab(tab);
            });

            addRenderableWidget(tabs);
            setInitialFocus(tabs);
        }

        setFocused(tabs);
        doneButton = new Button.Builder(CommonComponents.GUI_DONE, button -> {
            allTabs.forEach(tab -> tab.save());
            assert minecraft != null;
            minecraft.setScreen(back);
        }).bounds(width / 2 + 5, height - 6 - 20, 150, 20).build();
        addRenderableWidget(doneButton);

        Button cancelButton = new Button.Builder(CommonComponents.GUI_CANCEL, button -> {
            assert minecraft != null;
            minecraft.setScreen(back);
        }).bounds(width / 2 - 155, height - 6 - 20, 150, 20).build();
        addRenderableWidget(cancelButton);
    }

    private void setFocus(Tabs tabs) {
        ComponentPath componentPath = ComponentPath.path(this, tabs.focusTab());
        if (componentPath != null) {
            changeFocus(componentPath);
            changeFocus(componentPath);
            return;
        }
        changeFocus(ComponentPath.path(this, ComponentPath.leaf(tabs)));
    }

    @Override
    public void render(@NotNull GuiGraphics gfx, int i, int j, float f) {
        renderBackground(gfx);

        super.render(gfx, i, j, f);

        gfx.drawCenteredString(font, getTitle(), width / 2, 12 - font.lineHeight / 2, 0xffffffff);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == InputConstants.KEY_TAB)
            if (Util.getPlatform() == Util.OS.OSX ? KeyboardHelper.isAltDown() : KeyboardHelper.isCtrlDown()) {
                if (KeyboardHelper.isShiftDown()) {
                    tabs.previousTab();
                    return true;
                }
                tabs.nextTab();
                return true;
            }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public Screen getBack() {
        return back;
    }

    public BindingsList getList() {
        return list;
    }

    public Button getDoneButton() {
        return doneButton;
    }

    public void open() {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(this);
    }

    private static class BindingsTab extends Tab {
        private final BindingsList list;

        public BindingsTab(ControllerContext context, @Nullable String category) {
            super(category != null ? Component.translatable(category) : context.getName());
            list = new BindingsList(minecraft, getWidth(), getHeight(), getY(), getY() + getHeight(), (Config) context.getConfig());
            list.addEntries(((Config) context.getConfig()).values(), category);
            addRenderableWidget(list);
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            list.y0 = getY();
            list.y1 = getY() + getHeight();
            list.x0 = 0;
            list.x1 = getWidth();
            list.setSize(getWidth(), getHeight());

            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        }

        @Override
        public void resize(int width, int height) {
            super.resize(width, height);
        }

        @Override
        public boolean isEmpty() {
            return list.isEmpty() || super.isEmpty();
        }

        public void save() {
            list.save();
        }
    }
}
