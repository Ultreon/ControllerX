package dev.ultreon.controllerx.config.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.ultreon.mods.lib.util.KeyboardHelper;
import dev.ultreon.controllerx.api.ControllerContext;
import dev.ultreon.controllerx.config.gui.tabs.Tab;
import dev.ultreon.controllerx.config.gui.tabs.Tabs;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class BindingsScreen extends Screen {
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
        assert this.minecraft != null;
        this.minecraft.setScreen(this.back);
    }

    @Override
    protected void init() {
        this.clearWidgets();
        super.init();

        if (this.tabs != null) {
            this.tabs.resize(this.width, this.height - 70);

            this.addRenderableWidget(tabs);
        } else {
            this.tabs = new Tabs(0, 20, width, height - 70, this::setFocus);

            for (ControllerContext context : ControllerContext.getContexts()) {
                Tab tab = new BindingsTab(context);
                this.tabs.addTab(tab);
            }

            this.addRenderableWidget(tabs);
            this.setInitialFocus(tabs);
        }

        this.setFocused(tabs);
        this.doneButton = new Button.Builder(CommonComponents.GUI_DONE, button -> {
            this.list.save();
            assert this.minecraft != null;
            this.minecraft.setScreen(this.back);
        }).bounds(this.width / 2 + 5, this.height - 6 - 20, 150, 20).build();
        this.addRenderableWidget(this.doneButton);

        Button cancelButton = new Button.Builder(CommonComponents.GUI_CANCEL, button -> {
            assert this.minecraft != null;
            this.minecraft.setScreen(this.back);
        }).bounds(this.width / 2 - 155, this.height - 6 - 20, 150, 20).build();
        this.addRenderableWidget(cancelButton);
    }

    private void setFocus(Tabs tabs) {
        ComponentPath componentPath = ComponentPath.path(this, tabs.focusTab());
        if (componentPath != null) {
            this.changeFocus(componentPath);
            return;
        }
        this.changeFocus(ComponentPath.path(this, ComponentPath.leaf(tabs)));
    }

    @Override
    public void render(@NotNull GuiGraphics gfx, int i, int j, float f) {
        this.renderBackground(gfx);

        super.render(gfx, i, j, f);

        gfx.drawCenteredString(this.font, this.getTitle(), this.width / 2, 12 - this.font.lineHeight / 2, 0xffffffff);
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
        return this.back;
    }

    public BindingsList getList() {
        return this.list;
    }

    public Button getDoneButton() {
        return this.doneButton;
    }

    public void open() {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(this);
    }

    private static class BindingsTab extends Tab {
        private final BindingsList list;

        public BindingsTab(ControllerContext context) {
            super(context.getName());
            list = new BindingsList(this.minecraft, this.getWidth(), this.getHeight(), this.getY(), this.getY() + this.getHeight(), context.getConfig());
            list.addEntries(context.getConfig().values());
            this.addRenderableWidget(this.list);
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            list.y0 = this.getY();
            list.y1 = this.getY() + this.getHeight();
            list.x0 = 0;
            list.x1 = this.getWidth();
            list.setSize(this.getWidth(), this.getHeight());

            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        }

        @Override
        public void resize(int width, int height) {
            super.resize(width, height);
        }
    }
}
