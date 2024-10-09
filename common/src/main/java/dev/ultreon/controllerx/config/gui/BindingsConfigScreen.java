package dev.ultreon.controllerx.config.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.ultreon.mods.lib.util.KeyboardHelper;
import dev.ultreon.controllerx.api.ControllerContext;
import dev.ultreon.controllerx.config.Config;
import dev.ultreon.controllerx.config.gui.tabs.Tab;
import dev.ultreon.controllerx.config.gui.tabs.Tabs;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class BindingsConfigScreen extends Screen {
    private final Screen back;
    private BindingsConfigList list;
    private Button doneButton;
    private final Config config;
    private Tabs tabs;

    public BindingsConfigScreen(Screen back, Config config) {
        super(Component.translatable("controllerx.screen.config.bindings.title"));
        this.back = back;
        this.config = config;
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
            this.tabs.resize(this.width, this.height - 96);

            this.addRenderableWidget(tabs);
        } else {
            this.tabs = new Tabs(0, 20, width, height - 96);

            for (ControllerContext context : ControllerContext.getContexts()) {
                Tab tab = new BindingsTab(context);
                this.tabs.addTab(tab);
            }

            this.addRenderableWidget(tabs);
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

    public BindingsConfigList getList() {
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
        private final BindingsConfigList list;

        public BindingsTab(ControllerContext context) {
            super(context.getName());
            list = new BindingsConfigList(this.minecraft, this.width, this.height, 32, this.height - 32, context.getConfig());
            list.addEntries(context.getConfig().values());
            this.addWidget(this.list);
        }

        @Override
        public void resize(int width, int height) {
            super.resize(width, height);

            list.setSize(width, height - 32);
        }
    }
}
